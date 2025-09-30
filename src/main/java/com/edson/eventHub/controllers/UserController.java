package com.edson.eventHub.controllers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edson.eventHub.dto.PasswordChangeRequestDTO;
import com.edson.eventHub.dto.UserResponseDTO;
import com.edson.eventHub.entities.User;
import com.edson.eventHub.services.UserService;

@RestController
@RequestMapping("/users")
public class UserController {
	private final UserService userService;
	private final PasswordEncoder passwordEncoder;

	public UserController(UserService userService, PasswordEncoder passwordEncoder) {
		this.userService = userService;
		this.passwordEncoder = passwordEncoder;
	}

	@GetMapping
	public List<UserResponseDTO> getAll() {
		return userService.findAll().stream().map(UserResponseDTO::fromEntity) // Converte cada User para
																				// UserResponseDTO
				.collect(Collectors.toList());
	}

	@GetMapping("/{id}")
	public ResponseEntity<UserResponseDTO> getById(@PathVariable Long id) {
		Optional<User> user = userService.findById(id);
		return user.map(u -> ResponseEntity.ok(UserResponseDTO.fromEntity(u)))
                .orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<UserResponseDTO>  create(@RequestBody User user) {
		String hashedPassword = passwordEncoder.encode(user.getPasswordHash());
		user.setPasswordHash(hashedPassword);
		User savedUser = userService.save(user);
		 return ResponseEntity.ok(UserResponseDTO.fromEntity(savedUser));
	}

	@PutMapping("/{id}")
	public ResponseEntity<UserResponseDTO> update(@PathVariable Long id, @RequestBody User userDetails) {
		Optional<User> userOpt = userService.findById(id);
		if (!userOpt.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		User user = userOpt.get();
		user.setName(userDetails.getName());
		user.setEmail(userDetails.getEmail());
		user.setRole(userDetails.getRole());
		user.setParticipations(userDetails.getParticipations());

		User updated = userService.save(user);
		return ResponseEntity.ok(UserResponseDTO.fromEntity(updated));
	}

	@PatchMapping("/{id}/change-password")
	public ResponseEntity<Void> changePassword(@PathVariable Long id, @RequestBody PasswordChangeRequestDTO request) {
		try {
			userService.changePassword(id, request.getOldPassword(), request.getNewPassword());
			return ResponseEntity.noContent().build();
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		if (!userService.findById(id).isPresent()) {
			return ResponseEntity.notFound().build();
		}
		userService.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}
