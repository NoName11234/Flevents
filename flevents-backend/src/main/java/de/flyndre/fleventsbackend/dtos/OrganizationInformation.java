package de.flyndre.fleventsbackend.dtos;


import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.util.List;


public class OrganizationInformation extends OrganizationPreview{

    private ModelMapper mapper;
    @Setter
    @Getter
    private List<AccountPreview> accountPreviews;
    @Getter
    @Setter
    private List<EventPreview> eventPreviews;



}

