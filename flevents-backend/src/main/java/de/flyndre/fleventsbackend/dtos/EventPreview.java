package de.flyndre.fleventsbackend.dtos;

import de.flyndre.fleventsbackend.Models.Role;
import lombok.Getter;
import lombok.Setter;

import java.net.URI;
import java.sql.Timestamp;
import java.time.LocalDateTime;

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
