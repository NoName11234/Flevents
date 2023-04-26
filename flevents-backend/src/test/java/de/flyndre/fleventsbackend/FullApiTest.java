package de.flyndre.fleventsbackend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import de.flyndre.fleventsbackend.Models.Organization;
import de.flyndre.fleventsbackend.dtos.OrganizationInformation;
import de.flyndre.fleventsbackend.dtos.OrganizationPreview;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-local.properties")
public class FullApiTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new JsonMapper();



    @Test
    @WithMockUser(authorities = {"platformAdmin"})
    void apiTest() throws Exception {
        OrganizationPreview organizationPreview = new OrganizationPreview();
        organizationPreview.setName("Test Orga");
        organizationPreview.setDescription("Test description");
        var result = mockMvc.perform(MockMvcRequestBuilders.post("/api/platform/organizations")
                        .param("email","test@flyndre.de")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(organizationPreview)))
                .andExpect(status().isCreated())
                .andReturn();
        OrganizationInformation testOrga = mapper.readValue(result.getResponse().getContentAsString(),OrganizationInformation.class);



        mockMvc.perform(MockMvcRequestBuilders.delete("/api/platform/organizations/"+testOrga.getUuid()))
                .andExpect(status().isOk());
    }

    public void compare(OrganizationInformation information, Organization organization){
        assertThat(information.getName()).isEqualTo(organization.getName());
        assertThat(information.getDescription()).isEqualTo(organization.getDescription());
        assertThat(information.getAddress()).isEqualTo(organization.getAddress());
        assertThat(information.getPhoneContact()).isEqualTo(organization.getPhoneContact());
        assertThat(information.getIcon()).isEqualTo(organization.getIcon());
    }
}
