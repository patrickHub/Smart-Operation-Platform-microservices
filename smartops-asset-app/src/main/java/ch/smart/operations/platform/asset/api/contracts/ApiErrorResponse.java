package ch.smart.operations.platform.asset.api.contracts;

import java.util.Map;

public record ApiErrorResponse(
        String title,
        int status,
        String detail,
        Map<String, String[]> errors,
        String traceId
) {
}
