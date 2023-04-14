// Java Program to Illustrate EmailDetails Class

package de.flyndre.fleventsbackend.dtos;

// Importing required classes
import lombok.*;

import java.util.List;

// Annotations
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
