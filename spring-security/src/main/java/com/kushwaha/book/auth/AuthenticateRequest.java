package com.kushwaha.book.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthenticateRequest {

    @NotEmpty(message = "email cannot be Empty")
    @NotBlank(message = "email can not be blank")
    @Email(message = "please provide valid email id")
    private String email;
    @NotEmpty(message = "password cannot be Empty")
    @NotBlank(message = "password can not be blank")
    @Size(min=8, message = "password should be minimum 8 character")
    private String password;
}
