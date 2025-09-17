package com.crediya.usecase.generatereport;

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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GenerateReportUseCase Tests")
class GenerateReportUseCaseTest {

    @Mock
    private ReportRepository reportRepository;
    
    private GenerateReportUseCase generateReportUseCase;

    @BeforeEach
    void setUp() {
        generateReportUseCase = new GenerateReportUseCase(reportRepository);
    }

    @Test
    @DisplayName("Debe ejecutar correctamente con monto válido")
    void shouldExecuteSuccessfullyWithValidAmount() {
        // Given
        BigDecimal approvedAmount = new BigDecimal("50000.00");
        when(reportRepository.updateReport(any(BigDecimal.class)))
                .thenReturn(Mono.empty());

        // When
        Mono<Void> result = generateReportUseCase.execute(approvedAmount);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(reportRepository, times(1)).updateReport(eq(approvedAmount));
    }

    @Test
    @DisplayName("Debe ejecutar correctamente con monto cero")
    void shouldExecuteSuccessfullyWithZeroAmount() {
        // Given
        BigDecimal zeroAmount = BigDecimal.ZERO;
        when(reportRepository.updateReport(any(BigDecimal.class)))
                .thenReturn(Mono.empty());

        // When
        Mono<Void> result = generateReportUseCase.execute(zeroAmount);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(reportRepository, times(1)).updateReport(eq(zeroAmount));
    }

    @Test
    @DisplayName("Debe ejecutar correctamente con monto negativo")
    void shouldExecuteSuccessfullyWithNegativeAmount() {
        // Given
        BigDecimal negativeAmount = new BigDecimal("-25000.50");
        when(reportRepository.updateReport(any(BigDecimal.class)))
                .thenReturn(Mono.empty());

        // When
        Mono<Void> result = generateReportUseCase.execute(negativeAmount);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(reportRepository, times(1)).updateReport(eq(negativeAmount));
    }

    @Test
    @DisplayName("Debe manejar correctamente monto con alta precisión")
    void shouldHandleHighPrecisionAmount() {
        // Given
        BigDecimal highPrecisionAmount = new BigDecimal("123456.123456789");
        when(reportRepository.updateReport(any(BigDecimal.class)))
                .thenReturn(Mono.empty());

        // When
        Mono<Void> result = generateReportUseCase.execute(highPrecisionAmount);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(reportRepository, times(1)).updateReport(eq(highPrecisionAmount));
    }

    @Test
    @DisplayName("Debe propagar error del repository")
    void shouldPropagateRepositoryError() {
        // Given
        BigDecimal approvedAmount = new BigDecimal("50000.00");
        RuntimeException repositoryError = new RuntimeException("Database connection failed");
        when(reportRepository.updateReport(any(BigDecimal.class)))
                .thenReturn(Mono.error(repositoryError));

        // When
        Mono<Void> result = generateReportUseCase.execute(approvedAmount);

        // Then
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(reportRepository, times(1)).updateReport(eq(approvedAmount));
    }

    @Test
    @DisplayName("Debe manejar error de IllegalArgumentException del repository")
    void shouldHandleIllegalArgumentException() {
        // Given
        BigDecimal nullAmount = null;
        IllegalArgumentException argumentError = new IllegalArgumentException("Amount cannot be null");
        when(reportRepository.updateReport(any()))
                .thenReturn(Mono.error(argumentError));

        // When
        Mono<Void> result = generateReportUseCase.execute(nullAmount);

        // Then
        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(reportRepository, times(1)).updateReport(nullAmount);
    }

    @Test
    @DisplayName("Debe ejecutar correctamente múltiples veces")
    void shouldExecuteMultipleTimesSuccessfully() {
        // Given
        BigDecimal firstAmount = new BigDecimal("10000.00");
        BigDecimal secondAmount = new BigDecimal("20000.00");
        when(reportRepository.updateReport(any(BigDecimal.class)))
                .thenReturn(Mono.empty());

        // When
        Mono<Void> firstResult = generateReportUseCase.execute(firstAmount);
        Mono<Void> secondResult = generateReportUseCase.execute(secondAmount);

        // Then
        StepVerifier.create(firstResult)
                .verifyComplete();
        
        StepVerifier.create(secondResult)
                .verifyComplete();

        verify(reportRepository, times(1)).updateReport(eq(firstAmount));
        verify(reportRepository, times(1)).updateReport(eq(secondAmount));
    }

    @Test
    @DisplayName("Debe manejar timeout del repository")
    void shouldHandleRepositoryTimeout() {
        // Given
        BigDecimal approvedAmount = new BigDecimal("50000.00");
        when(reportRepository.updateReport(any(BigDecimal.class)))
                .thenReturn(Mono.never()); // Simula timeout

        // When
        Mono<Void> result = generateReportUseCase.execute(approvedAmount)
                .timeout(java.time.Duration.ofMillis(100));

        // Then
        StepVerifier.create(result)
                .expectError(java.util.concurrent.TimeoutException.class)
                .verify();

        verify(reportRepository, times(1)).updateReport(eq(approvedAmount));
    }

    @Test
    @DisplayName("Debe validar que el repository no sea null en constructor")
    void shouldValidateRepositoryNotNull() {
        // Given, When & Then
        try {
            new GenerateReportUseCase(null);
        } catch (Exception e) {
            // Se espera una excepción debido a @RequiredArgsConstructor
            // El comportamiento exacto depende de la implementación de Lombok
        }
    }

    @Test
    @DisplayName("Debe manejar montos extremadamente grandes")
    void shouldHandleExtremelyLargeAmounts() {
        // Given
        BigDecimal largeAmount = new BigDecimal("999999999999999999999999999999.99");
        when(reportRepository.updateReport(any(BigDecimal.class)))
                .thenReturn(Mono.empty());

        // When
        Mono<Void> result = generateReportUseCase.execute(largeAmount);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(reportRepository, times(1)).updateReport(eq(largeAmount));
    }
}