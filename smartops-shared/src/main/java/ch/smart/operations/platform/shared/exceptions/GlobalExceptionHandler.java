package ch.smart.operations.platform.shared.exceptions;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import ch.smart.operations.platform.shared.api.contracts.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.converter.HttpMessageNotReadableException;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        Map<String, String[]> errors = new LinkedHashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), new String[]{error.getDefaultMessage()})
        );

        return ResponseEntity.badRequest().body(
                new ApiErrorResponse(
                        "Validation failed",
                        400,
                        "One or more validation errors occurred.",
                        errors,
                        request.getRequestURI()
                )
        );
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
            ValidationException ex,
            HttpServletRequest request
    ) {
        return ResponseEntity.badRequest().body(
                new ApiErrorResponse(
                        "Validation failed",
                        400,
                        ex.getMessage(),
                        ex.getErrors(),
                        request.getRequestURI()
                )
        );
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFoundException(
            NotFoundException ex,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ApiErrorResponse(
                        "Resource not found",
                        404,
                        ex.getMessage(),
                        null,
                        request.getRequestURI()
                )
        );
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessRuleException(
            BusinessRuleException ex,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ApiErrorResponse(
                        "Business rule violation",
                        409,
                        ex.getMessage(),
                        null,
                        request.getRequestURI()
                )
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadable(
                HttpMessageNotReadableException ex,
                HttpServletRequest request
        ) {
        return ResponseEntity.badRequest().body(
                new ApiErrorResponse(
                        "Invalid request payload",
                        400,
                        "One or more fields have an invalid format.",
                        null,
                        request.getRequestURI()
                )
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingServletRequestParameter(
        MissingServletRequestParameterException ex,
        HttpServletRequest request
    ) {
        return ResponseEntity.badRequest().body(
            new ApiErrorResponse(
                    "Invalid request",
                    400,
                    "Missing required request parameter: " + ex.getParameterName(),
                    null,
                    request.getRequestURI()
            )
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpRequestMethodNotSupported(
        HttpRequestMethodNotSupportedException ex,
        HttpServletRequest request
    ) {
        return ResponseEntity.badRequest().body(
            new ApiErrorResponse(
                    "Invalid request",
                    400,
                    "Http Request Method Not Supported: " + ex.getMethod(),
                    null,
                    request.getRequestURI()
            )
        );
    }

    @ExceptionHandler(ResponseStatusException.class)
        public ResponseEntity<ApiErrorResponse> handleResponseStatusException(
                ResponseStatusException ex,
                HttpServletRequest request
        ) {
        int statusCode = ex.getStatusCode().value();

        String title = switch (statusCode) {
                case 400 -> "Bad Request";
                case 401 -> "Unauthorized";
                case 403 -> "Forbidden";
                case 404 -> "Resource not found";
                case 409 -> "Conflict";
                case 415 -> "Unsupported Media Type";
                default -> "Request failed";
        };

        String detail = ex.getReason() != null
                ? ex.getReason()
                : "The request could not be processed.";

        return ResponseEntity.status(ex.getStatusCode()).body(
                new ApiErrorResponse(
                        title,
                        statusCode,
                        detail,
                        null,
                        request.getRequestURI()
                )
        );
    }

    
    @ExceptionHandler(DownstreamServiceUnavailableException.class)
    public ResponseEntity<ApiErrorResponse> handleDownstreamUnavailableEntity(
        DownstreamServiceUnavailableException ex,
        HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(
            new ApiErrorResponse(
                    "Service Unavailable",
                    503,
                    ex.getMessage(),
                    Map.of(
                        "timestamp", new String[]{OffsetDateTime.now().toString()},
                        "service", new String[]{ex.getServiceName()},
                        "code", new String[]{"DOWNSTREAM_SERVICE_UNAVAILABLE"}
                    ),
                    request.getRequestURI()
            )
        );
   }

   @ExceptionHandler(DownstreamClientException.class)
   public ResponseEntity<String> handleDownstreamClientException(
        DownstreamClientException ex
   ) {
      return ResponseEntity
            .status(ex.getStatusCode())
            .header("Content-Type", "application/json")
            .body(ex.getResponseBody());
   }

   @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error("Unhandled exception while processing {} {}", request.getMethod(), request.getRequestURI(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ApiErrorResponse(
                        "Internal Server Error",
                        500,
                        "An unexpected error occurred.",
                        null,
                        request.getRequestURI()
                )
        );
    }

}