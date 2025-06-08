package com.kushwaha.book.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {
    Optional<Token> findByToken(String token);

    @Query("""
            select t from Token t where t.user.id = :userId and (t.expired=false and t.revoked=false)
            """)
    List<Token> findAllValidTokenByUser(Integer userId);

    @Modifying
    @Query("""
            UPDATE Token t SET t.revoked = true, t.revokedAt = :time where t.user.id = :userId and t.revoked=false
            """)
    public int revokeAllUserToken(Integer userId, LocalDateTime time);
}
