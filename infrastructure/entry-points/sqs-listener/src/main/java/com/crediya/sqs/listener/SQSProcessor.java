package com.crediya.sqs.listener;


import com.crediya.model.report.Report;
import com.crediya.sqs.listener.dto.GenerateReportEventDto;
import com.crediya.usecase.generatereport.GenerateReportUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class SQSProcessor implements Function<Message, Mono<Void>> {
    private final ObjectMapper objectMapper;
    private final GenerateReportUseCase saveReport;

    @Override
    public Mono<Void> apply(Message message) {
        return Mono.fromCallable(() -> objectMapper.readValue(message.body(), GenerateReportEventDto.class))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(evt -> {
                    log.info("SQS[{}] payload -> status={}, approvedAmount={}", message.messageId(), evt.getStatus(), evt.getApprovedAmount());
                    return saveReport.execute(evt.getApprovedAmount());
                })
                .doOnSuccess(v -> log.info(" SQS[{}] Report guardado", message.messageId()))
                .doOnError(e -> log.error(" SQS[{}] Error procesando: {}", message.messageId(), e.getMessage(), e))
                .then(); // Mono<Void>
    }
}
