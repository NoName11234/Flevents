package de.flyndre.fleventsbackend.dtos;

import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.util.List;

public class AccountInformation extends AccountPreview{
    private ModelMapper mapper;

    //@Getter
    //@Setter
    private String secret;
    @Setter
    @Getter
    private List<OrganizationPreview> organizationPreviews;
    @Getter
    @Setter
    private List<EventPreview> eventPreviews;

}
