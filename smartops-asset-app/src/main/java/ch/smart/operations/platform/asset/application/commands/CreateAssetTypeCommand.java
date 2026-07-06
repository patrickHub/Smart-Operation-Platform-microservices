package ch.smart.operations.platform.asset.application.commands;


public record CreateAssetTypeCommand(

    String code,
    String name,
    String manufacturer,
    String model,
    String description
) {
    
}
