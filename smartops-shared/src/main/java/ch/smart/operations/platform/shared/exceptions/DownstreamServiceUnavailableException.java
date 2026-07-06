package ch.smart.operations.platform.shared.exceptions;

public class DownstreamServiceUnavailableException extends RuntimeException {

    private final String serviceName;

    public DownstreamServiceUnavailableException(String serviceName, Throwable cause){
        super(serviceName + " is temporarily unavailable", cause);
        this.serviceName = serviceName;
    }

    public DownstreamServiceUnavailableException(String serviceName){
        super(serviceName + " is temporarily unavailable");
        this.serviceName = serviceName;
    }

    public String getServiceName(){
        return this.serviceName;
    }
    
}
