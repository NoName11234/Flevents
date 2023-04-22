package de.flyndre.fleventsbackend.dtos;

import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import java.util.List;

/**
 * This Class is the Data Transfer Object for the Information of Organizations.
 * It provides getter as well as setter.
 * @implNote This DTO should only be returned in the Controller
 * @author Lukas Burkhardt
 * @version $I$
 */
public class OrganizationInformation extends OrganizationPreview{

    private ModelMapper mapper;
    @Setter
    @Getter
    private List<AccountPreview> accountPreviews;
    @Getter
    @Setter
    private List<EventPreview> eventPreviews;



}

