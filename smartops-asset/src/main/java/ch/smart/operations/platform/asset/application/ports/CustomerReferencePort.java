package ch.smart.operations.platform.asset.application.ports;

import java.util.UUID;

public interface CustomerReferencePort {
    boolean customerExists(UUID customerId);
    boolean siteExists(UUID siteId);
    
}
