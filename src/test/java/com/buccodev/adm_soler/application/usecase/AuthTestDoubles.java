package com.buccodev.adm_soler.application.usecase;

import com.buccodev.adm_soler.core.domain.RefreshToken;
import com.buccodev.adm_soler.core.domain.User;
import com.buccodev.adm_soler.core.repository.RefreshTokenRepository;
import com.buccodev.adm_soler.core.repository.UserRepository;
import com.buccodev.adm_soler.core.security.AccessTokenProvider;
import com.buccodev.adm_soler.core.security.PasswordHasher;
import com.buccodev.adm_soler.core.security.RefreshTokenCodec;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Dublês em memória para os casos de uso de autenticação. Os use cases são
 * POJOs, então não é preciso subir o Spring para testá-los.
 */
final class AuthTestDoubles {

    private AuthTestDoubles() {
    }

    static final class InMemoryUserRepository implements UserRepository {

        private final Map<UUID, User> byId = new LinkedHashMap<>();

        @Override
        public User save(User user) {
            byId.put(user.getId(), user);
            return user;
        }

        @Override
        public Optional<User> findById(UUID id) {
            return Optional.ofNullable(byId.get(id));
        }

        @Override
        public Optional<User> findByEmail(String email) {
            return byId.values().stream().filter(u -> u.getEmail().equals(email)).findFirst();
        }

        @Override
        public boolean existsByEmail(String email) {
            return findByEmail(email).isPresent();
        }

        @Override
        public List<User> findAll() {
            return new ArrayList<>(byId.values());
        }

        @Override
        public void deleteById(UUID id) {
            byId.remove(id);
        }

        @Override
        public boolean existsById(UUID id) {
            return byId.containsKey(id);
        }
    }

    static final class InMemoryRefreshTokenRepository implements RefreshTokenRepository {

        private final Map<UUID, RefreshToken> byId = new LinkedHashMap<>();

        @Override
        public RefreshToken save(RefreshToken refreshToken) {
            byId.put(refreshToken.getId(), refreshToken);
            return refreshToken;
        }

        @Override
        public Optional<RefreshToken> findByTokenHash(String tokenHash) {
            return byId.values().stream().filter(t -> t.getTokenHash().equals(tokenHash)).findFirst();
        }

        @Override
        public List<RefreshToken> findActiveByUserId(UUID userId) {
            return byId.values().stream()
                    .filter(t -> t.getUserId().equals(userId) && !t.isRevoked())
                    .toList();
        }

        @Override
        public void deleteByUserId(UUID userId) {
            byId.values().removeIf(t -> t.getUserId().equals(userId));
        }

        @Override
        public void deleteExpiredBefore(LocalDateTime instant) {
            byId.values().removeIf(t -> t.isExpired(instant));
        }

        int size() {
            return byId.size();
        }
    }

    /** Hash reversível de propósito: o teste precisa enxergar o que foi guardado. */
    static final class FakePasswordHasher implements PasswordHasher {

        static final String PREFIX = "hash::";

        @Override
        public String hash(String rawPassword) {
            return PREFIX + rawPassword;
        }

        @Override
        public boolean matches(String rawPassword, String hashedPassword) {
            return hashedPassword != null && hashedPassword.equals(hash(rawPassword));
        }
    }

    static final class FakeAccessTokenProvider implements AccessTokenProvider {

        @Override
        public String generate(User user) {
            return "access::" + user.getId() + "::" + user.getRole();
        }

        @Override
        public long expiresInSeconds() {
            return 900;
        }
    }

    /** Valores sequenciais em vez de aleatórios, para o teste poder afirmar sobre eles. */
    static final class FakeRefreshTokenCodec implements RefreshTokenCodec {

        private int counter;

        @Override
        public String newValue() {
            return "refresh-" + (++counter);
        }

        @Override
        public String fingerprint(String value) {
            return "fp::" + value;
        }
    }
}
