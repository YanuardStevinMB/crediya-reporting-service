package com.crediya.api;

import com.crediya.api.handler.GlobalExceptionHandler;
import com.crediya.api.dto.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/reports",
                    method = RequestMethod.GET,
                    operation = @Operation(
                            operationId = "getUseCase",
                            summary = "Obtener datos del primer caso de uso",
                            description = "Endpoint GET para obtener información del primer caso de uso de reporting",
                            responses = {
                                @ApiResponse(
                                    responseCode = "200",
                                    description = "OK",
                                    content = @Content(schema = @Schema(implementation = Object.class))
                                ),
                                @ApiResponse(
                                    responseCode = "400",
                                    description = "Bad Request - Error de validación",
                                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
                                ),
                                @ApiResponse(
                                    responseCode = "404",
                                    description = "Not Found - Recurso no encontrado",
                                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
                                ),
                                @ApiResponse(
                                    responseCode = "500",
                                    description = "Internal Server Error - Error interno del servidor",
                                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
                                )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler, 
                                                         GlobalExceptionHandler globalExceptionHandler) {
        return route(GET("/api/v1/reports"), handler::listenGETUseCase)
                .filter(globalExceptionHandler);
    }
}
