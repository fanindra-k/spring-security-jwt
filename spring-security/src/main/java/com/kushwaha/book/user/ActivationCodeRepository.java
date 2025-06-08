package com.kushwaha.book.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActivationCodeRepository extends JpaRepository<ActivationCode, Integer> {
    Optional<ActivationCode> findByCode(String code);
}