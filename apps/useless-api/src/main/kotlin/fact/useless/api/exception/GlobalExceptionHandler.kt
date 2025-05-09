package fact.useless.api.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.time.LocalDateTime

data class ErrorResponse(
  val timestamp: LocalDateTime = LocalDateTime.now(),
  val status: Int,
  val error: String,
  val message: String,
  val path: String
)

@RestControllerAdvice
class GlobalExceptionHandler {

  @ExceptionHandler(ResponseStatusException::class)
  fun handleResponseStatusException(
    ex: ResponseStatusException,
    exchange: ServerWebExchange
  ): Mono<ResponseEntity<ErrorResponse>> {
    val status = ex.statusCode
    val response = ErrorResponse(
      status = status.value(),
      error = status.toString(),
      message = ex.reason ?: "Unexpected error",
      path = exchange.request.path.toString()
    )
    return Mono.just(ResponseEntity.status(status).body(response))
  }

  @ExceptionHandler(RuntimeException::class)
  fun handleRuntimeException(
    ex: RuntimeException,
    exchange: ServerWebExchange
  ): Mono<ResponseEntity<ErrorResponse>> {
    val status = HttpStatus.INTERNAL_SERVER_ERROR
    val response = ErrorResponse(
      status = status.value(),
      error = status.name,
      message = ex.message ?: "Unexpected server error",
      path = exchange.request.path.toString()
    )
    return Mono.just(ResponseEntity.status(status).body(response))
  }

  @ExceptionHandler(Exception::class)
  fun handleGenericException(
    ex: Exception,
    exchange: ServerWebExchange
  ): Mono<ResponseEntity<ErrorResponse>> {
    val status = HttpStatus.BAD_REQUEST
    val response = ErrorResponse(
      status = status.value(),
      error = status.name,
      message = ex.message ?: "Unexpected error",
      path = exchange.request.path.toString()
    )
    return Mono.just(ResponseEntity.status(status).body(response))
  }
}

