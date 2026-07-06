package ch.smart.operations.platform.gateway.web;

import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class GatewayFallbackController {

    @GetMapping("/fallback/customers")
    public ResponseEntity<Map<String, Object>> customersFallback() {
        return serviceUnavailable("CUSTOMER_SERVICE_UNAVAILABLE", "Customer service is temporarily unavailable.");
    }

     @GetMapping("/fallback/assets")
    public ResponseEntity<Map<String, Object>> assetsFallback() {
        return serviceUnavailable("ASSET_SERVICE_UNAVAILABLE",
                "Asset service is temporarily unavailable.");
    }

    @GetMapping("/fallback/workorders")
    public ResponseEntity<Map<String, Object>> workordersFallback() {
        return serviceUnavailable("WORKORDER_SERVICE_UNAVAILABLE",
                "Work order service is temporarily unavailable.");
    }

    @GetMapping("/fallback/billing")
    public ResponseEntity<Map<String, Object>> billingFallback() {
        return serviceUnavailable("BILLING_SERVICE_UNAVAILABLE",
                "Billing service is temporarily unavailable.");
    }

    @GetMapping("/fallback/notifications")
    public ResponseEntity<Map<String, Object>> notificationsFallback() {
        return serviceUnavailable("NOTIFICATION_SERVICE_UNAVAILABLE",
                "Notification service is temporarily unavailable.");
    }


    private ResponseEntity<Map<String, Object>> serviceUnavailable(String code, String message){
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                    "timestamp", OffsetDateTime.now().toString(),
                    "status", 503, // this give the frontend a clean 503 response instead of browser-level connection faillures.
                    "error", "Service Unavailable",
                    "code", code,
                    "message", message
                ));
    }
    
    
}
