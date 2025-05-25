package com.kushwaha.book.email;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum EmailTemplateName {
    ACTIVATE_ACCOUNT("activate_account");
    private final String name;
    private EmailTemplateName(String name) {
        this.name = name;
    }
}
