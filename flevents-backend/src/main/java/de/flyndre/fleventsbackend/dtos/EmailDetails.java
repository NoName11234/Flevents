package de.flyndre.fleventsbackend.dtos;

import lombok.*;
import java.util.List;

/**
 * This Class is the Data Transfer Object for Details of Mail.
 * It provides getter as well as setter.
 * @implNote This DTO should only be returned in the Controller
 * @author Lukas Burkhardt
 * @version $I$
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
// Class
public class EmailDetails {

    // Class data members
    private List<String> to;
    private List<String> cc;
    private List<String> bcc;
    private String subject;
    private String msgBody;
    private String attachment;
}
