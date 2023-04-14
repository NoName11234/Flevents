package de.flyndre.fleventsbackend.dtos;

import de.flyndre.fleventsbackend.Models.Role;
import lombok.Getter;
import lombok.Setter;

import java.net.URI;

public class AccountPreview {
    @Getter
    @Setter
    private String uuid;
    @Getter
    @Setter
    private String firstname;
    @Getter
    @Setter
    private String lastname;
    @Getter
    @Setter
    private String email;
    @Getter
    @Setter
    private String icon;
    @Getter
    @Setter
    private Role role;
}
