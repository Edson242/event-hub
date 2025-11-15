package com.edson.eventHub.controllers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.edson.eventHub.dto.PasswordChangeRequestDTO;
import com.edson.eventHub.dto.UserResponseDTO;
import com.edson.eventHub.entities.User;
import com.edson.eventHub.services.UserService;

@RestController
@RequestMapping("/users")
public class UserController {
	private final UserService userService;
	private final PasswordEncoder passwordEncoder;
	// LOG 1: Declaração do Logger
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	public UserController(UserService userService, PasswordEncoder passwordEncoder) {
		this.userService = userService;
		this.passwordEncoder = passwordEncoder;
	}

	@GetMapping
	public ResponseEntity<List<UserResponseDTO>> getAll() {
		logger.info("Buscando todos os usuários");
		try {
			List<UserResponseDTO> userDTOs = userService.findAll().stream()
					.map(UserResponseDTO::fromEntity)
					.collect(Collectors.toList());
			return ResponseEntity.ok(userDTOs);
		} catch (Exception e) {
			logger.error("Erro ao buscar todos os usuários", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<UserResponseDTO> getById(@PathVariable Long id) {
		logger.info("Buscando usuário com ID: {}", id);
		try {
			Optional<User> user = userService.findById(id);
			if (user.isPresent()) {
				logger.info("Usuário com ID: {} encontrado.", id);
				return ResponseEntity.ok(UserResponseDTO.fromEntity(user.get()));
			} else {
				logger.warn("Usuário com ID: {} não encontrado.", id);
				return ResponseEntity.notFound().build();
			}
		} catch (Exception e) {
			logger.error("Erro ao buscar usuário com ID: {}", id, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping
	public ResponseEntity<UserResponseDTO> create(@RequestBody User user) {
		// Loga o email, que é seguro. NUNCA logar a senha.
		logger.info("Criando novo usuário com email: {}", user.getEmail());
		try {
			// A senha recebida em user.getPasswordHash() é a senha pura,
			// que será codificada
			String hashedPassword = passwordEncoder.encode(user.getPasswordHash());
			user.setPasswordHash(hashedPassword);
			
			User savedUser = userService.save(user);
			logger.info("Usuário criado com sucesso. ID: {}", savedUser.getId());
			// Retorna 201 Created com o DTO
			return ResponseEntity.status(HttpStatus.CREATED)
								 .body(UserResponseDTO.fromEntity(savedUser));
		} catch (Exception e) {
			logger.error("Erro ao criar usuário com email: {}", user.getEmail(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<UserResponseDTO> update(@PathVariable Long id, @RequestBody User userDetails) {
		logger.info("Iniciando atualização para usuário com ID: {}", id);
		try {
			Optional<User> userOpt = userService.findById(id);
			if (!userOpt.isPresent()) {
				logger.warn("Falha ao atualizar. Usuário com ID: {} não encontrado.", id);
				return ResponseEntity.notFound().build();
			}
			User user = userOpt.get();
			user.setName(userDetails.getName());
			user.setEmail(userDetails.getEmail());
			user.setRole(userDetails.getRole());
			user.setParticipations(userDetails.getParticipations());
			// A senha NÃO é atualizada aqui, o que é correto.

			User updated = userService.save(user);
			logger.info("Usuário com ID: {} atualizado com sucesso.", updated.getId());
			return ResponseEntity.ok(UserResponseDTO.fromEntity(updated));
			
		} catch (Exception e) {
			logger.error("Erro ao atualizar usuário com ID: {}", id, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PatchMapping("/{id}/change-password")
	public ResponseEntity<Void> changePassword(@PathVariable Long id, @RequestBody PasswordChangeRequestDTO request) {
		// NUNCA logar o DTO 'request', pois ele contém senhas
		logger.info("Iniciando troca de senha para usuário com ID: {}", id);
		try {
			userService.changePassword(id, request.getOldPassword(), request.getNewPassword());
			logger.info("Senha alterada com sucesso para usuário ID: {}", id);
			return ResponseEntity.noContent().build(); // 204 No Content
			
		} catch (RuntimeException e) {
			// Captura erros de negócio (ex: "senha antiga errada")
			logger.warn("Falha na lógica de troca de senha para ID: {}. Motivo: {}", id, e.getMessage());
			// Retorna 400 Bad Request (ou 401/403 dependendo da sua regra)
			return ResponseEntity.badRequest().body(null); 
			
		} catch (Exception e) {
			// Captura erros inesperados (ex: falha de banco)
			logger.error("Erro inesperado ao trocar senha para ID: {}", id, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		logger.info("Iniciando exclusão do usuário com ID: {}", id);
		try {
			if (!userService.findById(id).isPresent()) {
				logger.warn("Falha ao excluir. Usuário com ID: {} não encontrado.", id);
				return ResponseEntity.notFound().build();
			}
			userService.deleteById(id);
			logger.info("Usuário com ID: {} excluído com sucesso.", id);
			return ResponseEntity.noContent().build(); // 204 No Content

		} catch (Exception e) {
			logger.error("Erro ao excluir usuário com ID: {}", id, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}