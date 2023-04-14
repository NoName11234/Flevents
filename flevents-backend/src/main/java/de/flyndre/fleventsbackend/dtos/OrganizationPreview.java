package de.flyndre.fleventsbackend.dtos;

import de.flyndre.fleventsbackend.Models.Role;
import lombok.Getter;
import lombok.Setter;

import java.net.URI;

@Setter
@Getter
public class OrganizationPreview {

    private String uuid;

    private String name;

    private String description;

    private String icon;

    private Role role;
    private String address;
    private String phoneContact;
}
