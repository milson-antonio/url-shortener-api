package com.dkbcodefactory.urlshortenerapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class UrlShortenerApiApplication

fun main(args: Array<String>) {
    runApplication<UrlShortenerApiApplication>(*args)
}
