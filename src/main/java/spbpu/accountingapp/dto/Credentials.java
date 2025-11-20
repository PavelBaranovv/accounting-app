package spbpu.accountingapp.dto;

import spbpu.accountingapp.enums.Role;

public record Credentials(
        String username,
        String password,
        Role role
) {}
