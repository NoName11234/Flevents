package de.flyndre.fleventsbackend.dtos;

import de.flyndre.fleventsbackend.Models.MailConfig;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.util.List;

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

}
