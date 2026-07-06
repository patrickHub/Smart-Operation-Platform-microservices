package ch.smart.operations.platform.identity.api.controllers;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ch.smart.operations.platform.identity.api.contracts.CreateUserRequest;
import ch.smart.operations.platform.identity.api.contracts.LoginRequest;
import ch.smart.operations.platform.identity.application.dtos.LoginResponseDto;
import ch.smart.operations.platform.identity.application.dtos.UserResponse;
import ch.smart.operations.platform.identity.application.services.IdentityApplicationService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final IdentityApplicationService identityApplicationService;

    public AuthController(IdentityApplicationService identityApplicationService) {
        this.identityApplicationService = identityApplicationService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(identityApplicationService.login(request));
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse response = identityApplicationService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> searchUsers(
        @RequestParam(required = false) String username,
        @RequestParam(required = false) String email
    ){
        List<UserResponse> users = identityApplicationService.searchUsers(username, email);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/username/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(
        @PathVariable String username
    ){
        UserResponse user = identityApplicationService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }
}