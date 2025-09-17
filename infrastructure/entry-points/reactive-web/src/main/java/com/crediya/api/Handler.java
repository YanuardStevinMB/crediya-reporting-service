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
// com/crediya/api/Handler.java
@Component
@RequiredArgsConstructor
public class Handler {

    private final ShowReportsUseCase showReportsUseCase;

    public Mono<ServerResponse> listenGETUseCase(ServerRequest req) {
        return showReportsUseCase.execute()
                // Si execute() devuelve ReportDto, deja como está:
                // .map(dto -> ApiResponse.ok(dto, "Operación exitosa", req.path()))
                // Si devuelve Report (dominio), mapea:
                .map(report -> ApiResponse.ok(toDto(report), "Operación exitosa", req.path()))
                .flatMap(body -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(body))
                .switchIfEmpty(ServerResponse.status(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ApiResponse.fail("No hay reporte", null, req.path())))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(ApiResponse.fail("Error interno", e.getMessage(), req.path())));
    }

    // Ajusta getters según tu dominio
    private ReportDto toDto(com.crediya.model.report.Report r) {
        return ReportDto.builder()
                .metricId(r.getMetricId())
                .count(r.getCount())
                .updatedAt(r.getUpdatedAt())
                .totalAmountCents(r.getTotalAmountCents())
                .build();
    }
}

