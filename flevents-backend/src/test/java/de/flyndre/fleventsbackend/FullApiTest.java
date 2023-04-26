package de.flyndre.fleventsbackend;

import de.flyndre.fleventsbackend.Models.Organization;
import de.flyndre.fleventsbackend.controller.FleventsAccountController;
import de.flyndre.fleventsbackend.controller.OrganizationController;
import de.flyndre.fleventsbackend.dtos.OrganizationInformation;
import de.flyndre.fleventsbackend.repositories.OrganizationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

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

    }

    public void compare(OrganizationInformation information, Organization organization){
        assertThat(information.getName()).isEqualTo(organization.getName());
        assertThat(information.getDescription()).isEqualTo(organization.getDescription());
        assertThat(information.getAddress()).isEqualTo(organization.getAddress());
        assertThat(information.getPhoneContact()).isEqualTo(organization.getPhoneContact());
        assertThat(information.getIcon()).isEqualTo(organization.getIcon());
    }
}
