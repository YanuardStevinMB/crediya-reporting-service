package com.crediya.api.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor  // <- agrega constructor vacío
@AllArgsConstructor
@Builder
public class ReportDto {
    private String metricId;
    private Long count;
    private String updatedAt;
    private BigDecimal totalAmountCents;
}
