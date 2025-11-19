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

import jakarta.validation.Valid;

/**
 * Controlador REST para gerenciar usuários.
 * Fornece endpoints para criar, recuperar, atualizar e deletar usuários.
 * O acesso a estes endpoints é restrito a administradores.
 */
@RestController
@RequestMapping("/users")
public class UserController {
	private final UserService userService;
	private final PasswordEncoder passwordEncoder;
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	public UserController(UserService userService, PasswordEncoder passwordEncoder) {
		this.userService = userService;
		this.passwordEncoder = passwordEncoder;
	}

	/**
	 * GET /users : Recupera todos os usuários.
	 *
	 * @return um ResponseEntity com uma lista de usuários (como DTOs).
	 */
	@GetMapping
	public ResponseEntity<List<UserResponseDTO>> getAll() {
		logger.info("Buscando todos os usuários");
		List<UserResponseDTO> userDTOs = userService.findAll().stream()
				.map(UserResponseDTO::fromEntity)
				.collect(Collectors.toList());
		return ResponseEntity.ok(userDTOs);
	}

	/**
	 * GET /users/{id} : Recupera um usuário específico pelo seu ID.
	 *
	 * @param id o ID do usuário a ser recuperado.
	 * @return um ResponseEntity com o usuário (como DTO), ou 404 (Not Found).
	 */
	@GetMapping("/{id}")
	public ResponseEntity<UserResponseDTO> getById(@PathVariable Long id) {
		logger.info("Buscando usuário com ID: {}", id);
		Optional<User> user = userService.findById(id);
        if (user.isPresent()) {
            logger.info("Usuário com ID: {} encontrado.", id);
            return ResponseEntity.ok(UserResponseDTO.fromEntity(user.get()));
        } else {
            logger.warn("Usuário com ID: {} não encontrado.", id);
            return ResponseEntity.notFound().build();
        }
	}

	/**
	 * POST /users : Cria um novo usuário.
	 * A senha do usuário será codificada antes de salvar.
	 *
	 * @param user o objeto de usuário a ser criado.
	 * @return um ResponseEntity com o usuário criado (como DTO).
	 */
	@PostMapping
	public ResponseEntity<UserResponseDTO> create(@Valid @RequestBody User user) {
		logger.info("Criando novo usuário com email: {}", user.getEmail());
		String hashedPassword = passwordEncoder.encode(user.getPassword());
		user.setPasswordHash(hashedPassword);
		
		User savedUser = userService.save(user);
		logger.info("Usuário criado com sucesso. ID: {}", savedUser.getId());
		return ResponseEntity.status(HttpStatus.CREATED)
								.body(UserResponseDTO.fromEntity(savedUser));
	}

	/**
	 * PUT /users/{id} : Atualiza um usuário existente.
	 *
	 * @param id o ID do usuário a ser atualizado.
	 * @param userDetails os dados atualizados do usuário.
	 * @return um ResponseEntity com o usuário atualizado (como DTO), ou 404 (Not Found).
	 */
	@PutMapping("/{id}")
	public ResponseEntity<UserResponseDTO> update(@PathVariable Long id, @Valid @RequestBody User userDetails) {
		logger.info("Iniciando atualização para usuário com ID: {}", id);
		return userService.findById(id)
			.map(user -> {
				user.setName(userDetails.getName());
				user.setEmail(userDetails.getEmail());
				user.setRole(userDetails.getRole());
				// Nota: A senha não é atualizada aqui. Use o endpoint dedicado.
				User updated = userService.save(user);
				logger.info("Usuário com ID: {} atualizado com sucesso.", updated.getId());
				return ResponseEntity.ok(UserResponseDTO.fromEntity(updated));
			})
			.orElseGet(() -> {
                logger.warn("Falha ao atualizar. Usuário com ID: {} não encontrado.", id);
                return ResponseEntity.notFound().build();
            });
	}

	/**
	 * PATCH /users/{id}/change-password : Altera a senha de um usuário.
	 *
	 * @param id o ID do usuário.
	 * @param request DTO contendo a senha antiga e a nova.
	 * @return um ResponseEntity com status 204 (No Content) em sucesso, ou 400 (Bad Request) em falha.
	 */
	@PatchMapping("/{id}/change-password")
	public ResponseEntity<Void> changePassword(@PathVariable Long id, @Valid @RequestBody PasswordChangeRequestDTO request) {
		logger.info("Iniciando troca de senha para usuário com ID: {}", id);
		try {
			userService.changePassword(id, request.getOldPassword(), request.getNewPassword());
			logger.info("Senha alterada com sucesso para usuário ID: {}", id);
			return ResponseEntity.noContent().build();
		} catch (RuntimeException e) {
			logger.warn("Falha na lógica de troca de senha para ID: {}. Motivo: {}", id, e.getMessage());
			return ResponseEntity.badRequest().build();
		}
	}

	/**
	 * DELETE /users/{id} : Deleta um usuário pelo seu ID.
	 *
	 * @param id o ID do usuário a ser deletado.
	 * @return um ResponseEntity com status 204 (No Content) ou 404 (Not Found).
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		logger.info("Iniciando exclusão do usuário com ID: {}", id);
		if (!userService.findById(id).isPresent()) {
			logger.warn("Falha ao excluir. Usuário com ID: {} não encontrado.", id);
			return ResponseEntity.notFound().build();
		}
		userService.deleteById(id);
		logger.info("Usuário com ID: {} excluído com sucesso.", id);
		return ResponseEntity.noContent().build();
	}
}