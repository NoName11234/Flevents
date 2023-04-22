package de.flyndre.fleventsbackend.dtos;

import de.flyndre.fleventsbackend.Models.Role;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * This Class is the Data Transfer Object for the Preview of Events.
 * It provides getter as well as setter.
 * @implNote This DTO should only be returned in the Controller
 * @author Lukas Burkhardt
 * @version $I$
 */
@Setter
@Getter
public class EventPreview {
    private String uuid;
    private String name;
    private String description;
    private String image;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Role role;
    private String location;
}
