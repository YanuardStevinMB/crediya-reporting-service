package com.crediya.api.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * DTO para respuestas de error estructuradas.
 * Mantiene compatibilidad con ApiResponse existente pero agrega información adicional.
 */
@Data
@Builder
public class ErrorResponseDto {
    private boolean success;
    private String message;
    private Object data;
    private Object errors;
    private String path;
    private Instant timestamp;
    
    // Campos adicionales para manejo avanzado de errores
    private String errorCode;
    private String technicalMessage;
    private String userMessage;
    private List<ErrorDetailDto> details;
    private String traceId;

    /**
     * Crea una respuesta de error básica compatible con ApiResponse.
     */
    public static ErrorResponseDto basic(String message, Object errors, String path) {
        return ErrorResponseDto.builder()
                .success(false)
                .message(message)
                .data(null)
                .errors(errors)
                .path(path)
                .timestamp(Instant.now())
                .build();
    }

    /**
     * Crea una respuesta de error enriquecida con información adicional.
     */
    public static ErrorResponseDto enriched(String message, String errorCode, 
                                           String technicalMessage, String userMessage, 
                                           String path) {
        return ErrorResponseDto.builder()
                .success(false)
                .message(message)
                .data(null)
                .errors(technicalMessage)
                .path(path)
                .timestamp(Instant.now())
                .errorCode(errorCode)
                .technicalMessage(technicalMessage)
                .userMessage(userMessage)
                .traceId(java.util.UUID.randomUUID().toString().substring(0, 8))
                .build();
    }

    /**
     * Crea una respuesta de error con detalles específicos.
     */
    public static ErrorResponseDto withDetails(String message, String errorCode,
                                              String technicalMessage, String userMessage,
                                              String path, List<ErrorDetailDto> details) {
        return ErrorResponseDto.builder()
                .success(false)
                .message(message)
                .data(null)
                .errors(technicalMessage)
                .path(path)
                .timestamp(Instant.now())
                .errorCode(errorCode)
                .technicalMessage(technicalMessage)
                .userMessage(userMessage)
                .details(details)
                .traceId(java.util.UUID.randomUUID().toString().substring(0, 8))
                .build();
    }
}