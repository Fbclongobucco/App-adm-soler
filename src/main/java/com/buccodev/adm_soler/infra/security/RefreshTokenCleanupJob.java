package com.buccodev.adm_soler.infra.security;

import com.buccodev.adm_soler.core.repository.RefreshTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Faxina dos refresh tokens vencidos. Eles nao servem mais para autenticar,
 * mas continuariam engordando a tabela indefinidamente.
 */
@Component
public class RefreshTokenCleanupJob {

    private static final Logger log = LoggerFactory.getLogger(RefreshTokenCleanupJob.class);

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenCleanupJob(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void purgeExpired() {
        LocalDateTime cutoff = LocalDateTime.now();
        refreshTokenRepository.deleteExpiredBefore(cutoff);
        log.info("Refresh tokens expirados antes de {} removidos", cutoff);
    }
}
