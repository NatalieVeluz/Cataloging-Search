package com.catalog.repository;

import com.catalog.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {

    Optional<PasswordResetToken> findByEmailAndOtp(String email, String otp);

    void deleteByEmail(String email);
}