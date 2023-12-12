package com.nikita.instazoo.web;

import com.nikita.instazoo.payload.request.LoginRequest;
import com.nikita.instazoo.payload.request.SingUpRequest;
import com.nikita.instazoo.payload.response.JWTTokenSuccessResponse;
import com.nikita.instazoo.payload.response.MessageResponse;
import com.nikita.instazoo.security.JWTTokenProvider;
import com.nikita.instazoo.security.SecurityConstants;
import com.nikita.instazoo.services.UserService;
import com.nikita.instazoo.validations.ResponseErrorValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/api/auth")
@PreAuthorize("permitAll()")
public class AuthController {

    private ResponseErrorValidation responseErrorValidation;
    private UserService userService;
    private AuthenticationManager authenticationManager;
    private JWTTokenProvider jwtTokenProvider;

    @Autowired
    public AuthController(ResponseErrorValidation responseErrorValidation,
                          UserService userService,
                          AuthenticationManager authenticationManager,
                          JWTTokenProvider jwtTokenProvider) {
        this.responseErrorValidation = responseErrorValidation;
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/singin")
    public ResponseEntity<Object> authenticateUser(@Valid @RequestBody LoginRequest loginRequest,
                                                   BindingResult result) {
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(result);
        if (!ObjectUtils.isEmpty(errors)) return errors;

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = SecurityConstants.TOKEN_PREFIX + jwtTokenProvider.generateWebToken(authentication);
        return ResponseEntity.ok(new JWTTokenSuccessResponse(jwt, true));
    }


    @PostMapping("/singup")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody SingUpRequest singUpRequest,
                                               BindingResult result) {
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(result);
        if (errors != null) {
            return errors;
        } else {

            userService.createUser(singUpRequest);
            return ResponseEntity.ok(new MessageResponse("User registered successfully"));
        }
    }
}
