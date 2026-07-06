package ch.smart.operations.platform.identity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        System.out.println("admin123     = " + encoder.encode("admin123"));
        System.out.println("support123   = " + encoder.encode("support123"));
        System.out.println("dispatcher123  = " + encoder.encode("dispatcher123"));
        System.out.println("technician123      = " + encoder.encode("technician123"));
        System.out.println("billing123   = " + encoder.encode("billing123"));
    }
}