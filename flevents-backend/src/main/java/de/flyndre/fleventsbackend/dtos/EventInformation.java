package de.flyndre.fleventsbackend.dtos;

import de.flyndre.fleventsbackend.Models.MailConfig;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import java.util.List;

/**
 * This Class is the Data Transfer Object for Information of Events.
 * It provides getter as well as setter.
 * @implNote This DTO should only be returned in the Controller
 * @author Lukas Burkhardt
 * @version $I$
 */
public class EventInformation extends EventPreview{
    private ModelMapper mapper;

    @Getter
    @Setter
    private MailConfig mailConfig;
    @Getter
    @Setter
    private OrganizationPreview organizationPreview;
    @Getter
    @Setter
    private List<AccountPreview> accountPreviews;
    @Getter
    @Setter
    private List<PostInformation> posts;

}
