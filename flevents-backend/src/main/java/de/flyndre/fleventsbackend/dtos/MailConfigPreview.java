package de.flyndre.fleventsbackend.dtos;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
/**
 * This Class is the Data Transfer Object for the Preview of mail configurations.
 * It provides getter as well as setter.
 * @implNote This DTO should only be returned in the Controller
 * @author Pascal Fuchs
 * @version $I$
 */
@Setter
@Getter
public class MailConfigPreview {
    private String mailText;
    private LocalDateTime localDateTime;
}
