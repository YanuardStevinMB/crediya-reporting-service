package com.crediya.api.handler;

import com.crediya.api.dto.ErrorResponseDto;
import com.crediya.api.mapper.ExceptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeoutException;

/**
 * Global Exception Handler para programación reactiva.
 * Maneja todas las excepciones de forma centralizada sin modificar dependencias existentes.
 * Mantiene compatibilidad total con el código actual.
 */
@Component
public class GlobalExceptionHandler implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    private final ExceptionMapper exceptionMapper;

    public GlobalExceptionHandler(ExceptionMapper exceptionMapper) {
        this.exceptionMapper = exceptionMapper;
    }

    @Override
    public Mono<ServerResponse> filter(ServerRequest request, HandlerFunction<ServerResponse> next) {
        return next.handle(request)
                // === MANEJO ESPECÍFICO DE EXCEPCIONES ===
                .onErrorResume(IllegalArgumentException.class, 
                    ex -> handleIllegalArgumentException(ex, request))
                
                .onErrorResume(NullPointerException.class,
                    ex -> handleNullPointerException(ex, request))
                
                .onErrorResume(TimeoutException.class,
                    ex -> handleTimeoutException(ex, request))
                
                .onErrorResume(RuntimeException.class,
                    ex -> handleRuntimeException(ex, request))

                // === UNWRAP REACTOR EXCEPTIONS ===
                .onErrorResume(throwable -> {
                    Throwable unwrapped = Exceptions.unwrap(throwable);
                    
                    if (unwrapped instanceof IllegalArgumentException ex) {
                        return handleIllegalArgumentException(ex, request);
                    } else if (unwrapped instanceof NullPointerException ex) {
                        return handleNullPointerException(ex, request);
                    } else if (unwrapped instanceof TimeoutException ex) {
                        return handleTimeoutException(ex, request);
                    } else if (unwrapped instanceof RuntimeException ex) {
                        return handleRuntimeException(ex, request);
                    }
                    
                    // Fallback para excepciones no controladas
                    return handleGenericException(unwrapped, request);
                })
                .switchIfEmpty(ServerResponse.noContent().build());
    }

    /**
     * Maneja IllegalArgumentException (400 - Bad Request).
     */
    private Mono<ServerResponse> handleIllegalArgumentException(IllegalArgumentException ex, ServerRequest request) {
        logWarn("Illegal argument error", ex, request);
        ErrorResponseDto errorResponse = exceptionMapper.mapIllegalArgument(ex, request);
        return createResponse(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja NullPointerException (400 - Bad Request).
     */
    private Mono<ServerResponse> handleNullPointerException(NullPointerException ex, ServerRequest request) {
        logWarn("Null pointer error", ex, request);
        ErrorResponseDto errorResponse = exceptionMapper.mapNullPointer(ex, request);
        return createResponse(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja TimeoutException (408 - Request Timeout).
     */
    private Mono<ServerResponse> handleTimeoutException(TimeoutException ex, ServerRequest request) {
        logWarn("Timeout error", ex, request);
        ErrorResponseDto errorResponse = exceptionMapper.mapTimeoutException(ex, request);
        return createResponse(errorResponse, HttpStatus.REQUEST_TIMEOUT);
    }

    /**
     * Maneja RuntimeException genérica (500 - Internal Server Error).
     */
    private Mono<ServerResponse> handleRuntimeException(RuntimeException ex, ServerRequest request) {
        logError("Runtime error", ex, request);
        ErrorResponseDto errorResponse = exceptionMapper.mapRuntimeException(ex, request);
        return createResponse(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Maneja excepciones genéricas no controladas (500 - Internal Server Error).
     */
    private Mono<ServerResponse> handleGenericException(Throwable ex, ServerRequest request) {
        logError("Unhandled exception", ex, request);
        ErrorResponseDto errorResponse = exceptionMapper.mapGenericException(ex, request);
        return createResponse(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Crea la respuesta HTTP con el ErrorResponseDto.
     */
    private Mono<ServerResponse> createResponse(ErrorResponseDto errorResponse, HttpStatus status) {
        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(errorResponse);
    }

    // === MÉTODOS DE LOGGING ===

    private void logError(String message, Throwable ex, ServerRequest request) {
        logger.error("{} - Path: {} {}, Error: {}", 
                message, 
                request.methodName(), 
                request.path(), 
                ex.getMessage(), 
                ex);
    }

    private void logWarn(String message, Throwable ex, ServerRequest request) {
        logger.warn("{} - Path: {} {}, Error: {}", 
                message, 
                request.methodName(), 
                request.path(), 
                ex.getMessage());
    }
}