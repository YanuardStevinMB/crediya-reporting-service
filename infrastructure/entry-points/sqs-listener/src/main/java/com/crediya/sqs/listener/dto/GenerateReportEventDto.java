package com.crediya.sqs.listener.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GenerateReportEventDto {
    private String status;             // "APPROVED", "REJECTED", etc.
    private BigDecimal approvedAmount; // 50056.00
}
