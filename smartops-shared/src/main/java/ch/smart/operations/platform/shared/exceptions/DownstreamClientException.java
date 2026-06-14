package ch.smart.operations.platform.shared.exceptions;

import org.springframework.http.HttpStatusCode;

public class DownstreamClientException extends RuntimeException {

    private final String serviceName;
    private final HttpStatusCode statusCode;
    private final String responseBody;

    public DownstreamClientException(String serviceName, HttpStatusCode statusCode, String responseBody) {
        super(serviceName + " returned a client error: " + statusCode);
        this.serviceName = serviceName;
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public String getServiceName() {
        return serviceName;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }
}