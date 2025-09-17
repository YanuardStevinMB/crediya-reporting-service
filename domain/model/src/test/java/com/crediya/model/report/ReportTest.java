package com.crediya.model.report;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Report Model Tests")
class ReportTest {

    @Test
    @DisplayName("Debe crear un Report con constructor por defecto")
    void shouldCreateReportWithDefaultConstructor() {
        // Given & When
        Report report = new Report();
        
        // Then
        assertNotNull(report);
        assertNull(report.getMetricId());
        assertNull(report.getCount());
        assertNull(report.getUpdatedAt());
        assertNull(report.getTotalAmountCents());
    }

    @Test
    @DisplayName("Debe crear un Report con constructor con parámetros")
    void shouldCreateReportWithAllArgsConstructor() {
        // Given
        String metricId = "global-report";
        Long count = 10L;
        String updatedAt = "2025-09-17T04:30:00Z";
        BigDecimal totalAmount = new BigDecimal("50000.00");
        
        // When
        Report report = new Report(metricId, count, updatedAt, totalAmount);
        
        // Then
        assertNotNull(report);
        assertEquals(metricId, report.getMetricId());
        assertEquals(count, report.getCount());
        assertEquals(updatedAt, report.getUpdatedAt());
        assertEquals(totalAmount, report.getTotalAmountCents());
    }

    @Test
    @DisplayName("Debe crear un Report usando builder")
    void shouldCreateReportWithBuilder() {
        // Given
        String metricId = "test-metric";
        Long count = 5L;
        String updatedAt = "2025-09-17T04:30:00Z";
        BigDecimal totalAmount = new BigDecimal("25000.50");
        
        // When
        Report report = Report.builder()
                .metricId(metricId)
                .count(count)
                .updatedAt(updatedAt)
                .totalAmountCents(totalAmount)
                .build();
        
        // Then
        assertNotNull(report);
        assertEquals(metricId, report.getMetricId());
        assertEquals(count, report.getCount());
        assertEquals(updatedAt, report.getUpdatedAt());
        assertEquals(totalAmount, report.getTotalAmountCents());
    }

    @Test
    @DisplayName("Debe permitir modificar campos usando setters")
    void shouldAllowModificationUsingSetters() {
        // Given
        Report report = new Report();
        String metricId = "modified-metric";
        Long count = 100L;
        String updatedAt = "2025-09-17T05:00:00Z";
        BigDecimal totalAmount = new BigDecimal("75000.75");
        
        // When
        report.setMetricId(metricId);
        report.setCount(count);
        report.setUpdatedAt(updatedAt);
        report.setTotalAmountCents(totalAmount);
        
        // Then
        assertEquals(metricId, report.getMetricId());
        assertEquals(count, report.getCount());
        assertEquals(updatedAt, report.getUpdatedAt());
        assertEquals(totalAmount, report.getTotalAmountCents());
    }

    @Test
    @DisplayName("Debe manejar valores null correctamente")
    void shouldHandleNullValuesCorrectly() {
        // Given & When
        Report report = Report.builder()
                .metricId(null)
                .count(null)
                .updatedAt(null)
                .totalAmountCents(null)
                .build();
        
        // Then
        assertNotNull(report);
        assertNull(report.getMetricId());
        assertNull(report.getCount());
        assertNull(report.getUpdatedAt());
        assertNull(report.getTotalAmountCents());
    }

    @Test
    @DisplayName("Debe manejar valores BigDecimal con precisión")
    void shouldHandleBigDecimalWithPrecision() {
        // Given
        BigDecimal preciseTotalAmount = new BigDecimal("123456789.123456789");
        
        // When
        Report report = Report.builder()
                .totalAmountCents(preciseTotalAmount)
                .build();
        
        // Then
        assertEquals(preciseTotalAmount, report.getTotalAmountCents());
        assertEquals(0, preciseTotalAmount.compareTo(report.getTotalAmountCents()));
    }

    @Test
    @DisplayName("Debe implementar equals y hashCode correctamente")
    void shouldImplementEqualsAndHashCodeCorrectly() {
        // Given
        String metricId = "test-metric";
        Long count = 10L;
        String updatedAt = "2025-09-17T04:30:00Z";
        BigDecimal totalAmount = new BigDecimal("50000.00");
        
        Report report1 = Report.builder()
                .metricId(metricId)
                .count(count)
                .updatedAt(updatedAt)
                .totalAmountCents(totalAmount)
                .build();
        
        Report report2 = Report.builder()
                .metricId(metricId)
                .count(count)
                .updatedAt(updatedAt)
                .totalAmountCents(totalAmount)
                .build();
        
        Report report3 = Report.builder()
                .metricId("different-metric")
                .count(count)
                .updatedAt(updatedAt)
                .totalAmountCents(totalAmount)
                .build();
        
        // Then
        assertEquals(report1, report2);
        assertNotEquals(report1, report3);
        assertEquals(report1.hashCode(), report2.hashCode());
        assertNotEquals(report1.hashCode(), report3.hashCode());
    }

    @Test
    @DisplayName("Debe generar toString correctamente")
    void shouldGenerateToStringCorrectly() {
        // Given
        Report report = Report.builder()
                .metricId("test-metric")
                .count(10L)
                .updatedAt("2025-09-17T04:30:00Z")
                .totalAmountCents(new BigDecimal("50000.00"))
                .build();
        
        // When
        String toString = report.toString();
        
        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("test-metric"));
        assertTrue(toString.contains("10"));
        assertTrue(toString.contains("2025-09-17T04:30:00Z"));
        assertTrue(toString.contains("50000.00"));
    }

    @Test
    @DisplayName("Debe manejar BigDecimal cero correctamente")
    void shouldHandleZeroBigDecimalCorrectly() {
        // Given
        BigDecimal zero = BigDecimal.ZERO;
        
        // When
        Report report = Report.builder()
                .totalAmountCents(zero)
                .count(0L)
                .build();
        
        // Then
        assertEquals(zero, report.getTotalAmountCents());
        assertEquals(0L, report.getCount());
        assertEquals(0, BigDecimal.ZERO.compareTo(report.getTotalAmountCents()));
    }
}