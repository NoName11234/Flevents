package de.flyndre.fleventsbackend;

import de.flyndre.fleventsbackend.Models.Organization;
import de.flyndre.fleventsbackend.controller.OrganizationController;
import de.flyndre.fleventsbackend.controllerServices.OrganizationControllerService;
import de.flyndre.fleventsbackend.dtos.OrganizationInformation;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;


import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestPropertySource("classpath:application-integration.properties")
public class FullApiTest {
    @Autowired
    private OrganizationController organizationController;



    @Test
    void createOrganization(){
        Organization organization = new Organization();
        organization.setName("TestOrganization");
        organization.setDescription("This is an Organization");
        organization.setAddress("93389 Horb");
        organization.setPhoneContact("74888930384");
        organization.setIcon("This should be an icon");
        OrganizationInformation information = (OrganizationInformation) organizationController.createOrganisation(organization).getBody();
        compare(information,organization);
        assertNotNull(information.getUuid());
    }

    public void compare(OrganizationInformation information, Organization organization){
        assertThat(information.getName()).isEqualTo(organization.getName());
        assertThat(information.getDescription()).isEqualTo(organization.getDescription());
        assertThat(information.getAddress()).isEqualTo(organization.getAddress());
        assertThat(information.getPhoneContact()).isEqualTo(organization.getPhoneContact());
        assertThat(information.getIcon()).isEqualTo(organization.getIcon());
    }
}
