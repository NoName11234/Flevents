package de.flyndre.fleventsbackend.dtos;

import de.flyndre.fleventsbackend.Models.PostComment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

/**
 * This Class is the Data Transfer Object for the Information of Posts.
 * It provides getter as well as setter.
 * @implNote This DTO should only be returned in the Controller
 * @author Lukas Burkhardt
 * @version $I$
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PostInformation extends PostPreview{

    private EventPreview event;
    private List<PostComment> comments;
    private List<AttachmentPreview> attachments;
}
