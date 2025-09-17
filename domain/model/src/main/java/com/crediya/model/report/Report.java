package com.crediya.model.report;
import lombok.*;

import java.math.BigDecimal;
//import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor  // <- agrega constructor vacío
@AllArgsConstructor
@Builder
public class Report {
    private String metricId;
    private Long count;
    private String updatedAt;
    private BigDecimal totalAmountCents;
}
