package ch.smart.operations.platform.customer.application.commands;



public record CreateCustomerCommand(
    String legalName,
    String displayName,
    String taxIdentifier,
    String industry,
    String notes
) {}
