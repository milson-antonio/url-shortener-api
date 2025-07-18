package com.dkbcodefactory.urlshortenerapi.controller

import com.dkbcodefactory.urlshortenerapi.dtos.UrlAcknowledgement
import com.dkbcodefactory.urlshortenerapi.dtos.UrlCreate
import com.dkbcodefactory.urlshortenerapi.kafka.UrlProducer
import com.dkbcodefactory.urlshortenerapi.service.UrlService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

@RestController
@RequestMapping("/api/v1/url")
@Validated
class UrlController(private val urlService: UrlService) {

    @PostMapping
    fun createShortUrl(@Valid @RequestBody urlCreate: UrlCreate): ResponseEntity<UrlAcknowledgement> {
        val urlAcknowledgement = urlService.createShortUrl(urlCreate);

        val location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(urlAcknowledgement.id)
            .toUri();

        return ResponseEntity.created(location).body(urlAcknowledgement)
    }

}
