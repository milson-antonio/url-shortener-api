package com.dkbcodefactory.urlshortenerapi.exceptions

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.time.LocalDateTime

@ControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    override fun handleNoHandlerFoundException(
        ex: NoHandlerFoundException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> {
        return createResponseEntity(
            httpStatus = HttpStatus.NOT_FOUND,
            message = "No handler found for ${ex.httpMethod} ${ex.requestURL}",
            title = "Resource Not Found",
            fieldWithError = null
        )
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> {
        val fieldErrors = ex.bindingResult.fieldErrors.map { fieldError ->
            ExceptionDto.FieldWithError(
                field = fieldError.field,
                detailedErrorMessage = fieldError.defaultMessage
            )
        }

        return createResponseEntity(
            httpStatus = HttpStatus.BAD_REQUEST,
            message = "Validation failed for the request",
            title = "Bad Request",
            fieldWithError = fieldErrors
        )
    }

    @ExceptionHandler(UrlNotFoundException::class)
    fun handleUrlNotFound(exception: UrlNotFoundException, request: WebRequest): ResponseEntity<ExceptionDto> {
        return createResponseEntity(
            httpStatus = HttpStatus.NOT_FOUND,
            message = exception.message,
            title = "Resource Not Found"
        )
    }

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequest(exception: BadRequestException, request: WebRequest): ResponseEntity<ExceptionDto> {
        return createResponseEntity(
            httpStatus = HttpStatus.BAD_REQUEST,
            message = exception.message,
            title = "Bad Request"
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(exception: Exception, request: WebRequest): ResponseEntity<ExceptionDto> {
        return createResponseEntity(
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
            message = exception.message ?: "An unexpected error occurred",
            title = "Internal Server Error"
        )
    }

    private fun <T> createResponseEntity(
        httpStatus: HttpStatus,
        message: String?,
        title: String,
        fieldWithError: List<ExceptionDto.FieldWithError>? = null
    ): ResponseEntity<T> {
        val exceptionDto = buildExceptionDto(
            httpStatus = httpStatus,
            message = message,
            title = title,
            fieldWithError = fieldWithError
        )
        @Suppress("UNCHECKED_CAST")
        return ResponseEntity(exceptionDto as T, httpStatus)
    }

    private fun buildExceptionDto(
        httpStatus: HttpStatus,
        message: String?,
        title: String,
        fieldWithError: List<ExceptionDto.FieldWithError>?
    ): ExceptionDto {
        return ExceptionDto(
            code = httpStatus.value(),
            status = httpStatus.reasonPhrase,
            localDateTime = LocalDateTime.now(),
            title = title,
            message = message,
            fieldWithError = fieldWithError
        )
    }
}