package com.kushwaha.book.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class AuthenticateResponse {
    private String token;
    private String username;
    private List<String> rolename;
}
