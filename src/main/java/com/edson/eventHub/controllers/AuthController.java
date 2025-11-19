package com.edson.eventHub.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edson.eventHub.dto.LoginRequestDTO;
import com.edson.eventHub.dto.LoginResponseDTO;
import com.edson.eventHub.entities.User;
import com.edson.eventHub.repository.UserRepository;
import com.edson.eventHub.services.TokenService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

/**
 * Controlador REST para lidar com requisições de autenticação.
 * Fornece o endpoint para login de usuário.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthenticationManager authenticationManager, TokenService tokenService, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    /**
     * POST /auth/login : Autentica um usuário e retorna um token JWT.
     *
     * @param loginRequest DTO contendo o e-mail e a senha do usuário.
     * @return um ResponseEntity com status 200 (OK) e um token JWT no corpo em caso de sucesso,
     *         ou 401 (Unauthorized) se as credenciais forem inválidas.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        
        logger.info("Tentativa de login para o email: {}", loginRequest.email());

        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password());
            Authentication auth = authenticationManager.authenticate(usernamePassword);

            var userDetails = (UserDetails) auth.getPrincipal();
            
            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado após autenticação bem-sucedida"));

            logger.info("Usuário autenticado com sucesso: {}", userDetails.getUsername());
            String token = tokenService.generateToken(user);

            logger.debug("Token JWT gerado para o usuário: {}", user.getEmail());

            return ResponseEntity.ok(new LoginResponseDTO(token));

        } catch (BadCredentialsException e) {
            logger.warn("Falha na autenticação (credenciais inválidas) para o email: {}", loginRequest.email());
            return ResponseEntity.status(401).build();
        }
    }
}