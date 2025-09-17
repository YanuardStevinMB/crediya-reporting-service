package com.crediya.model.report.gateways;

import com.crediya.model.report.Report;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface ReportRepository {
    Mono<Void> updateReport(BigDecimal approvedAmountCents);
    Mono<Report> get();
}
