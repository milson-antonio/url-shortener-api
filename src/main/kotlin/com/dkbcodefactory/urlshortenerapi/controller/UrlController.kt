package com.dkbcodefactory.urlshortenerapi.controller

import com.dkbcodefactory.urlshortenerapi.dtos.UrlAcknowledgement
import com.dkbcodefactory.urlshortenerapi.dtos.UrlCreate
import com.dkbcodefactory.urlshortenerapi.service.UrlService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.UUID

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

    @GetMapping("/{shorterUrl}")
    fun redirectToOriginalUrl(@PathVariable shorterUrl: String): ResponseEntity<UrlAcknowledgement> {
        val urlAcknowledgement = urlService.findByShortUrl(shorterUrl)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.status(HttpStatus.FOUND).body(urlAcknowledgement)
    }

    @GetMapping("/{id}")
    fun redirectToOriginalUrlById(@PathVariable id: UUID): ResponseEntity<UrlAcknowledgement> {
        val urlAcknowledgement = urlService.findById(id)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.status(HttpStatus.FOUND).body(urlAcknowledgement)
    }

    @DeleteMapping("/{id}")
    fun deleteShortUrl(@PathVariable id: UUID): ResponseEntity<Void> {
        urlService.deleteById(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/list")
    fun listUrls(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<List<UrlAcknowledgement>> {
        return ResponseEntity.ok(urlService.findAllPaginated(page, size))
    }

}