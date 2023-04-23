package de.flyndre.fleventsbackend.dtos;

import de.flyndre.fleventsbackend.Models.Role;
import lombok.Getter;
import lombok.Setter;

/**
 * This Class is the Data Transfer Object for specific Preview of the Account.
 * It provides getter as well as setter.
 * @implNote This DTO should only be returned in the Controller
 * @author Lukas Burkhardt
 * @version $I$
 */
@Getter
@Setter
public class AccountPreview {

    private String uuid;

    private String firstname;

    private String lastname;

    private String email;

    private String icon;

    private Role role;

    private boolean checkedIn;

    private Boolean isActive;
}
