package com.kushwaha.book.token;

import com.kushwaha.book.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Token {

    @Id
    @GeneratedValue
    private Integer id;
    @Column(length = 1024)
    private String token;
    @Enumerated(EnumType.STRING)
    private TokenType tokenType;
    private LocalDateTime createdAt;
    private LocalDateTime revokedAt;
    private boolean expired;
    private boolean revoked;
    @ManyToOne
    @JoinColumn(name="userId", nullable=false)
    private User user;

}
