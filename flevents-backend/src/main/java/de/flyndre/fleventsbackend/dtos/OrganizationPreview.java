package de.flyndre.fleventsbackend.dtos;

import de.flyndre.fleventsbackend.Models.Role;
import lombok.Getter;
import lombok.Setter;

/**
 * This Class is the Data Transfer Object for the Preview of Organizations.
 * It provides getter as well as setter.
 * @implNote This DTO should only be returned in the Controller
 * @author Lukas Burkhardt
 * @version $I$
 */
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
    private String customerNumber;
}
