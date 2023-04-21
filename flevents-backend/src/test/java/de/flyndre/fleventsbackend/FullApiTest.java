package de.flyndre.fleventsbackend;

import de.flyndre.fleventsbackend.Models.FleventsAccount;
import de.flyndre.fleventsbackend.Models.Organization;
import de.flyndre.fleventsbackend.controller.FleventsAccountController;
import de.flyndre.fleventsbackend.controller.OrganizationController;
import de.flyndre.fleventsbackend.controllerServices.OrganizationControllerService;
import de.flyndre.fleventsbackend.dtos.AccountInformation;
import de.flyndre.fleventsbackend.dtos.OrganizationInformation;
import de.flyndre.fleventsbackend.repositories.OrganizationRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;


import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestPropertySource("classpath:application-local.properties")
public class FullApiTest {
    @Autowired
    private OrganizationController organizationController;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private FleventsAccountController fleventsAccountController;


    @Test
    void createOrganization(){
        Organization organization = new Organization();
        organization.setName("TestOrganization");
        organization.setDescription("This is an Organization");
        organization.setAddress("93389 Horb");
        organization.setPhoneContact("74888930384");
        organization.setIcon("This should be an icon");
        OrganizationInformation information = (OrganizationInformation) organizationController.createOrganisation(organization,"test@test").getBody();
        compare(information,organization);
        assertNotNull(information.getUuid());
        FleventsAccount account = new FleventsAccount(null,"Lukas","Burkhardt",true,"test@test.com","icon","geheim",null,null);
        List list = organizationRepository.findAll();
    }

    public void compare(OrganizationInformation information, Organization organization){
        assertThat(information.getName()).isEqualTo(organization.getName());
        assertThat(information.getDescription()).isEqualTo(organization.getDescription());
        assertThat(information.getAddress()).isEqualTo(organization.getAddress());
        assertThat(information.getPhoneContact()).isEqualTo(organization.getPhoneContact());
        assertThat(information.getIcon()).isEqualTo(organization.getIcon());
    }
}
