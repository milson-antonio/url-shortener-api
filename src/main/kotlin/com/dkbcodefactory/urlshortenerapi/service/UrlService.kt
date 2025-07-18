package com.dkbcodefactory.urlshortenerapi.service

import com.dkbcodefactory.urlshortenerapi.dtos.UrlAcknowledgement
import com.dkbcodefactory.urlshortenerapi.dtos.UrlCreate
import com.dkbcodefactory.urlshortenerapi.dtos.UrlStatus
import com.dkbcodefactory.urlshortenerapi.kafka.UrlProducer
import com.dkbcodefactory.urlshortenerapi.mapper.Mapper
import com.dkbcodefactory.urlshortenerapi.repository.UrlRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.UUID
import java.util.concurrent.CompletableFuture

@Service
class UrlService(
    private val urlProducer: UrlProducer,
    private val urlRepository: UrlRepository
) {

    @Transactional
    public fun createShortUrl(urlCreate: UrlCreate): UrlAcknowledgement {
        val existingUrlOptional = urlRepository.findByOriginalUrl(urlCreate.originalUrl)

        if (existingUrlOptional.isPresent) {
            val existingUrl = existingUrlOptional.get()
            return Mapper.mapToUrlAcknowledgement(existingUrl)
        }

        val urlAcknowledgement = UrlAcknowledgement(
            id = UUID.randomUUID(),
            originalUrl = urlCreate.originalUrl,
            shorterUrl = "",
            status = UrlStatus.RECEIVED,
            message = "URL created successfully"
        )

        val urlEntity = Mapper.mapToUrlEntity(urlAcknowledgement)
        urlRepository.save(urlEntity)

        CompletableFuture.runAsync {
            urlProducer.sendUrl(urlAcknowledgement)
        }

        return urlAcknowledgement
    }

}
