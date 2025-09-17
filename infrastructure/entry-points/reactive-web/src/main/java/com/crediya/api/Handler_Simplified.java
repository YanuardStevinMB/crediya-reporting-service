package com.crediya.api;

import com.crediya.api.dto.ApiResponse;
import com.crediya.api.dto.ReportDto;
import com.crediya.usecase.showreports.ShowReportsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * EJEMPLO: Handler simplificado que delega el manejo de errores al GlobalExceptionHandler.
 * Este archivo es solo para demostrar cómo se simplifica el código con el global handler.
 * El archivo Handler.java original sigue funcionando exactamente igual.
 */
// @Component  // Comentado para no conflictuar con el Handler original
@RequiredArgsConstructor
public class Handler_Simplified {

    private final ShowReportsUseCase showReportsUseCase;

    public Mono<ServerResponse> listenGETUseCase(ServerRequest req) {
        return showReportsUseCase.execute()
                .map(report -> ApiResponse.ok(toDto(report), "Operación exitosa", req.path()))
                .flatMap(body -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(body))
                .switchIfEmpty(ServerResponse.status(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ApiResponse.fail("No hay reporte", null, req.path())));
                // ¡No más onErrorResume! El GlobalExceptionHandler se encarga de todos los errores
    }

    private ReportDto toDto(com.crediya.model.report.Report r) {
        return ReportDto.builder()
                .metricId(r.getMetricId())
                .count(r.getCount())
                .updatedAt(r.getUpdatedAt())
                .totalAmountCents(r.getTotalAmountCents())
                .build();
    }
}