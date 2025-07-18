package com.dkbcodefactory.urlshortenerapi.kafka

import com.dkbcodefactory.urlshortenerapi.dtos.UrlAcknowledgement
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

@Component
class UrlProducer(
    private val kafkaTemplate: KafkaTemplate<String, UrlAcknowledgement>
) {
    private val logger = LoggerFactory.getLogger(UrlProducer::class.java)

    @Value("\${kafka.topic.url-acknowledgement}")
    private lateinit var topic: String

    @Retryable(
        value = [Exception::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 1000, multiplier = 2.0)
    )
    fun sendUrl(urlAcknowledgement: UrlAcknowledgement) {
        logger.info("Sending URL to Kafka topic {}: {} - {}", 
            topic, urlAcknowledgement.id, urlAcknowledgement.originalUrl)
        try {
            val future: CompletableFuture<SendResult<String, UrlAcknowledgement>> = 
                kafkaTemplate.send(topic, urlAcknowledgement.id.toString(), urlAcknowledgement)

            future.whenComplete { result, ex ->
                if (ex == null) {
                    logger.info("Successfully sent URL to Kafka: {}, offset: {}", 
                        urlAcknowledgement.id, result.recordMetadata.offset())
                } else {
                    logger.error("Failed to send URL to Kafka: {} - {}", 
                        urlAcknowledgement.id, ex.message, ex)
                    throw RuntimeException("Failed to send message to Kafka", ex)
                }
            }
        } catch (ex: Exception) {
            logger.error("Failed to send URL to Kafka: {} - {}", 
                urlAcknowledgement.id, ex.message, ex)
            throw ex
        }
    }
}