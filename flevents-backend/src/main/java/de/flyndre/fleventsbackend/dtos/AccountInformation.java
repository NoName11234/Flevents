package de.flyndre.fleventsbackend.dtos;

import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import java.util.List;

/**
 * This Class is the Data Transfer Object for specific Information of the Account.
 * It provides getter as well as setter.
 * @implNote This DTO should only be returned in the Controller
 * @author Lukas Burkhardt
 * @version $I$
 */
public class AccountInformation extends AccountPreview{
    private ModelMapper mapper;
    private String secret;
    @Setter
    @Getter
    private List<OrganizationPreview> organizationPreviews;
    @Getter
    @Setter
    private List<EventPreview> eventPreviews;

}
