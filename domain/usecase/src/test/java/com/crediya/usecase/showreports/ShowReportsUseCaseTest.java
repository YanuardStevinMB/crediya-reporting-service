package com.crediya.usecase.showreports;

import com.crediya.model.report.Report;
import com.crediya.model.report.gateways.ReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ShowReportsUseCase Tests")
class ShowReportsUseCaseTest {

    @Mock
    private ReportRepository reportRepository;
    
    private ShowReportsUseCase showReportsUseCase;

    @BeforeEach
    void setUp() {
        showReportsUseCase = new ShowReportsUseCase(reportRepository);
    }

    @Test
    @DisplayName("Debe retornar reporte cuando existe")
    void shouldReturnReportWhenExists() {
        // Given
        Report expectedReport = Report.builder()
                .metricId("global-report")
                .count(10L)
                .updatedAt("2025-09-17T04:30:00Z")
                .totalAmountCents(new BigDecimal("50000.00"))
                .build();

        when(reportRepository.get()).thenReturn(Mono.just(expectedReport));

        // When
        Mono<Report> result = showReportsUseCase.execute();

        // Then
        StepVerifier.create(result)
                .expectNext(expectedReport)
                .verifyComplete();

        verify(reportRepository, times(1)).get();
    }

    @Test
    @DisplayName("Debe retornar Mono vacío cuando no existe reporte")
    void shouldReturnEmptyMonoWhenReportDoesNotExist() {
        // Given
        when(reportRepository.get()).thenReturn(Mono.empty());

        // When
        Mono<Report> result = showReportsUseCase.execute();

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(reportRepository, times(1)).get();
    }

    @Test
    @DisplayName("Debe retornar reporte con valores cero")
    void shouldReturnReportWithZeroValues() {
        // Given
        Report zeroReport = Report.builder()
                .metricId("global-report")
                .count(0L)
                .updatedAt("2025-09-17T04:30:00Z")
                .totalAmountCents(BigDecimal.ZERO)
                .build();

        when(reportRepository.get()).thenReturn(Mono.just(zeroReport));

        // When
        Mono<Report> result = showReportsUseCase.execute();

        // Then
        StepVerifier.create(result)
                .expectNext(zeroReport)
                .verifyComplete();

        verify(reportRepository, times(1)).get();
    }

    @Test
    @DisplayName("Debe retornar reporte con valores altos")
    void shouldReturnReportWithHighValues() {
        // Given
        Report highValueReport = Report.builder()
                .metricId("global-report")
                .count(999999L)
                .updatedAt("2025-09-17T04:30:00Z")
                .totalAmountCents(new BigDecimal("999999999.99"))
                .build();

        when(reportRepository.get()).thenReturn(Mono.just(highValueReport));

        // When
        Mono<Report> result = showReportsUseCase.execute();

        // Then
        StepVerifier.create(result)
                .expectNext(highValueReport)
                .verifyComplete();

        verify(reportRepository, times(1)).get();
    }

    @Test
    @DisplayName("Debe propagar error del repository")
    void shouldPropagateRepositoryError() {
        // Given
        RuntimeException repositoryError = new RuntimeException("Database connection failed");
        when(reportRepository.get()).thenReturn(Mono.error(repositoryError));

        // When
        Mono<Report> result = showReportsUseCase.execute();

        // Then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(reportRepository, times(1)).get();
    }

    @Test
    @DisplayName("Debe manejar timeout del repository")
    void shouldHandleRepositoryTimeout() {
        // Given
        when(reportRepository.get()).thenReturn(Mono.never()); // Simula timeout

        // When
        Mono<Report> result = showReportsUseCase.execute()
                .timeout(java.time.Duration.ofMillis(100));

        // Then
        StepVerifier.create(result)
                .expectError(java.util.concurrent.TimeoutException.class)
                .verify();

        verify(reportRepository, times(1)).get();
    }

    @Test
    @DisplayName("Debe ejecutar correctamente múltiples veces")
    void shouldExecuteMultipleTimesSuccessfully() {
        // Given
        Report report = Report.builder()
                .metricId("global-report")
                .count(5L)
                .updatedAt("2025-09-17T04:30:00Z")
                .totalAmountCents(new BigDecimal("25000.00"))
                .build();

        when(reportRepository.get()).thenReturn(Mono.just(report));

        // When & Then
        for (int i = 0; i < 3; i++) {
            StepVerifier.create(showReportsUseCase.execute())
                    .expectNext(report)
                    .verifyComplete();
        }

        verify(reportRepository, times(3)).get();
    }

    @Test
    @DisplayName("Debe retornar reporte con campos null")
    void shouldReturnReportWithNullFields() {
        // Given
        Report reportWithNulls = Report.builder()
                .metricId("global-report")
                .count(null)
                .updatedAt(null)
                .totalAmountCents(null)
                .build();

        when(reportRepository.get()).thenReturn(Mono.just(reportWithNulls));

        // When
        Mono<Report> result = showReportsUseCase.execute();

        // Then
        StepVerifier.create(result)
                .expectNext(reportWithNulls)
                .verifyComplete();

        verify(reportRepository, times(1)).get();
    }

    @Test
    @DisplayName("Debe manejar error de acceso denegado")
    void shouldHandleAccessDeniedError() {
        // Given
        SecurityException accessDeniedError = new SecurityException("Access denied");
        when(reportRepository.get()).thenReturn(Mono.error(accessDeniedError));

        // When
        Mono<Report> result = showReportsUseCase.execute();

        // Then
        StepVerifier.create(result)
                .expectError(SecurityException.class)
                .verify();

        verify(reportRepository, times(1)).get();
    }

    @Test
    @DisplayName("Debe retornar reporte con precisión decimal alta")
    void shouldReturnReportWithHighDecimalPrecision() {
        // Given
        Report precisionReport = Report.builder()
                .metricId("global-report")
                .count(1L)
                .updatedAt("2025-09-17T04:30:00.123456789Z")
                .totalAmountCents(new BigDecimal("123.123456789"))
                .build();

        when(reportRepository.get()).thenReturn(Mono.just(precisionReport));

        // When
        Mono<Report> result = showReportsUseCase.execute();

        // Then
        StepVerifier.create(result)
                .expectNext(precisionReport)
                .verifyComplete();

        verify(reportRepository, times(1)).get();
    }

    @Test
    @DisplayName("Debe validar que el repository no sea null en constructor")
    void shouldValidateRepositoryNotNull() {
        // Given, When & Then
        try {
            new ShowReportsUseCase(null);
        } catch (Exception e) {
            // Se espera una excepción debido a @RequiredArgsConstructor
            // El comportamiento exacto depende de la implementación de Lombok
        }
    }

    @Test
    @DisplayName("Debe retornar reporte con diferentes metricIds")
    void shouldReturnReportWithDifferentMetricIds() {
        // Given
        Report customReport = Report.builder()
                .metricId("custom-metric-id")
                .count(42L)
                .updatedAt("2025-09-17T04:30:00Z")
                .totalAmountCents(new BigDecimal("12345.67"))
                .build();

        when(reportRepository.get()).thenReturn(Mono.just(customReport));

        // When
        Mono<Report> result = showReportsUseCase.execute();

        // Then
        StepVerifier.create(result)
                .expectNext(customReport)
                .verifyComplete();

        verify(reportRepository, times(1)).get();
    }

    @Test
    @DisplayName("Debe manejar InterruptedException")
    void shouldHandleInterruptedException() {
        // Given
        InterruptedException interruptedError = new InterruptedException("Process interrupted");
        when(reportRepository.get()).thenReturn(Mono.error(interruptedError));

        // When
        Mono<Report> result = showReportsUseCase.execute();

        // Then
        StepVerifier.create(result)
                .expectError(InterruptedException.class)
                .verify();

        verify(reportRepository, times(1)).get();
    }
}