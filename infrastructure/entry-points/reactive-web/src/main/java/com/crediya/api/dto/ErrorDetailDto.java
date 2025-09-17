package com.crediya.api.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * DTO para detalles específicos de errores.
 * Proporciona información granular sobre errores de validación o negocio.
 */
@Data
@Builder
public class ErrorDetailDto {
    private String field;
    private Object rejectedValue;
    private String technicalMessage;
    private String userMessage;
    private String errorCode;
    private String resourceType;
    private String resourceId;
    private Map<String, Object> additionalInfo;

    /**
     * Crea un detalle de error para validación de campo.
     */
    public static ErrorDetailDto fieldValidation(String field, Object rejectedValue, 
                                                 String technicalMessage, String userMessage) {
        return ErrorDetailDto.builder()
                .field(field)
                .rejectedValue(rejectedValue)
                .technicalMessage(technicalMessage)
                .userMessage(userMessage)
                .errorCode("FIELD_VALIDATION")
                .build();
    }

    /**
     * Crea un detalle de error para recurso no encontrado.
     */
    public static ErrorDetailDto resourceNotFound(String resourceType, String resourceId, 
                                                  String technicalMessage, String userMessage) {
        return ErrorDetailDto.builder()
                .resourceType(resourceType)
                .resourceId(resourceId)
                .technicalMessage(technicalMessage)
                .userMessage(userMessage)
                .errorCode("RESOURCE_NOT_FOUND")
                .build();
    }

    /**
     * Crea un detalle de error genérico.
     */
    public static ErrorDetailDto generic(String errorCode, String technicalMessage, String userMessage) {
        return ErrorDetailDto.builder()
                .technicalMessage(technicalMessage)
                .userMessage(userMessage)
                .errorCode(errorCode)
                .build();
    }
}