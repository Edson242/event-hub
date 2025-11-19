package com.edson.eventHub.services;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.edson.eventHub.entities.User;
import com.edson.eventHub.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

/**
 * Classe de serviço para operações relacionadas a usuários.
 * Implementa a interface UserDetailsService do Spring Security para se integrar ao contexto de segurança.
 */
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constrói um UserService com as dependências necessárias.
     *
     * @param userRepository  O repositório para acesso aos dados de usuários.
     * @param passwordEncoder O codificador para hashing e verificação de senhas.
     */
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Recupera todos os usuários.
     *
     * @return uma lista de todos os usuários.
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Encontra um usuário pelo seu ID.
     *
     * @param id O ID do usuário a ser encontrado.
     * @return um Optional contendo o usuário encontrado, ou um Optional vazio se não for encontrado.
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Salva um novo usuário ou atualiza um existente.
     * Nota: Este método não lida com a codificação de senhas. A senha deve ser codificada antes de chamar este método.
     *
     * @param user A entidade de usuário a ser salva.
     * @return a entidade de usuário salva.
     */
    public User save(User user) {
        return userRepository.save(user);
    }

    /**
     * Deleta um usuário pelo seu ID.
     *
     * @param id O ID do usuário a ser deletado.
     */
    public void deleteById(Long id) {
    	userRepository.deleteById(id);
    }
    
    /**
     * Altera a senha de um determinado usuário.
     *
     * @param userId      O ID do usuário cuja senha será alterada.
     * @param oldPassword A senha atual do usuário.
     * @param newPassword A nova senha a ser definida.
     * @throws EntityNotFoundException se o usuário não for encontrado.
     * @throws RuntimeException se a senha antiga não corresponder.
     */
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com o id: " + userId));

        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new RuntimeException("A senha antiga está incorreta.");
        }

        String newHashedPassword = passwordEncoder.encode(newPassword);

        user.setPasswordHash(newHashedPassword);
        userRepository.save(user);
    }
    
    /**
     * Carrega um usuário pelo seu endereço de e-mail (username).
     * Este método é exigido pela interface UserDetailsService para o Spring Security.
     *
     * @param email O endereço de e-mail do usuário a ser carregado.
     * @return o objeto UserDetails para o usuário encontrado.
     * @throws UsernameNotFoundException se nenhum usuário for encontrado com o e-mail fornecido.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o email: " + email));
    }
}
