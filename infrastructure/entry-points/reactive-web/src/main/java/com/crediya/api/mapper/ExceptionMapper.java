package com.crediya.api.mapper;

import com.crediya.api.dto.ErrorDetailDto;
import com.crediya.api.dto.ErrorResponseDto;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Collections;
import java.util.List;

/**
 * Mapper para convertir excepciones en respuestas HTTP apropiadas.
 * Mantiene la separación de responsabilidades y centraliza la lógica de mapeo.
 */
@Component
public class ExceptionMapper {

    /**
     * Mapea IllegalArgumentException a ErrorResponseDto.
     */
    public ErrorResponseDto mapIllegalArgument(IllegalArgumentException ex, ServerRequest request) {
        return ErrorResponseDto.enriched(
                "Parámetros inválidos",
                "INVALID_ARGUMENT",
                ex.getMessage(),
                "Los parámetros proporcionados no son válidos",
                request.path()
        );
    }

    /**
     * Mapea NullPointerException a ErrorResponseDto.
     */
    public ErrorResponseDto mapNullPointer(NullPointerException ex, ServerRequest request) {
        return ErrorResponseDto.enriched(
                "Error de datos requeridos",
                "NULL_POINTER",
                ex.getMessage(),
                "Faltan datos requeridos para completar la operación",
                request.path()
        );
    }

    /**
     * Mapea RuntimeException genérica a ErrorResponseDto.
     */
    public ErrorResponseDto mapRuntimeException(RuntimeException ex, ServerRequest request) {
        return ErrorResponseDto.enriched(
                "Error de ejecución",
                "RUNTIME_ERROR",
                ex.getMessage(),
                "Ha ocurrido un error durante la ejecución de la operación",
                request.path()
        );
    }

    /**
     * Mapea excepciones genéricas a ErrorResponseDto.
     */
    public ErrorResponseDto mapGenericException(Throwable ex, ServerRequest request) {
        return ErrorResponseDto.enriched(
                "Error interno del sistema",
                "INTERNAL_ERROR",
                ex.getMessage() != null ? ex.getMessage() : "Error desconocido",
                "Ha ocurrido un error inesperado en el sistema. Por favor contacte al administrador.",
                request.path()
        );
    }

    /**
     * Mapea timeout exceptions.
     */
    public ErrorResponseDto mapTimeoutException(Exception ex, ServerRequest request) {
        return ErrorResponseDto.enriched(
                "Tiempo de espera agotado",
                "TIMEOUT_ERROR",
                ex.getMessage(),
                "La operación tomó más tiempo del esperado. Por favor intente más tarde.",
                request.path()
        );
    }

    /**
     * Mapea errores de conectividad.
     */
    public ErrorResponseDto mapConnectivityException(Exception ex, ServerRequest request) {
        return ErrorResponseDto.enriched(
                "Error de conectividad",
                "CONNECTIVITY_ERROR",
                ex.getMessage(),
                "Error temporal de conectividad. Por favor intente más tarde.",
                request.path()
        );
    }

    /**
     * Mapea errores de validación con detalles específicos.
     */
    public ErrorResponseDto mapValidationException(String field, Object rejectedValue, 
                                                  String technicalMessage, String userMessage, 
                                                  ServerRequest request) {
        ErrorDetailDto detail = ErrorDetailDto.fieldValidation(
                field, rejectedValue, technicalMessage, userMessage
        );
        
        return ErrorResponseDto.withDetails(
                "Error de validación",
                "VALIDATION_ERROR",
                technicalMessage,
                userMessage,
                request.path(),
                Collections.singletonList(detail)
        );
    }

    /**
     * Mapea errores de recurso no encontrado.
     */
    public ErrorResponseDto mapNotFoundError(String resourceType, String resourceId, 
                                            ServerRequest request) {
        ErrorDetailDto detail = ErrorDetailDto.resourceNotFound(
                resourceType, 
                resourceId,
                String.format("%s with ID %s not found", resourceType, resourceId),
                String.format("El %s solicitado no fue encontrado", resourceType.toLowerCase())
        );
        
        return ErrorResponseDto.withDetails(
                "Recurso no encontrado",
                "NOT_FOUND",
                String.format("%s not found", resourceType),
                "El recurso solicitado no fue encontrado",
                request.path(),
                Collections.singletonList(detail)
        );
    }
}