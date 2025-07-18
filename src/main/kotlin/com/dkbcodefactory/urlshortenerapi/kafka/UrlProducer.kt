package com.dkbcodefactory.urlshortenerapi.kafka

import com.dkbcodefactory.urlshortenerapi.dtos.UrlAcknowledgement
import org.springframework.stereotype.Component

@Component
class UrlProducer {

    fun sendUrl(urlAcknowledgement: UrlAcknowledgement) {
        println("Sending URL to Kafka: ${urlAcknowledgement.id} - ${urlAcknowledgement.originalUrl}")
    }
}
