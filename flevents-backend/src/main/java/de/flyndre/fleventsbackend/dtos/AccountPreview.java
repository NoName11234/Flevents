package de.flyndre.fleventsbackend.dtos;

import de.flyndre.fleventsbackend.Models.Role;
import lombok.Getter;
import lombok.Setter;

import java.net.URI;

@Getter
@Setter
public class AccountPreview {

    private String uuid;

    private String firstname;

    private String lastname;

    private String email;

    private String icon;

    private Role role;

    private Boolean isActive;
}
