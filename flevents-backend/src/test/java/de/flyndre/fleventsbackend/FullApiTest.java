package de.flyndre.fleventsbackend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import de.flyndre.fleventsbackend.Models.FleventsAccount;
import de.flyndre.fleventsbackend.Models.InvitationToken;
import de.flyndre.fleventsbackend.Models.Organization;
import de.flyndre.fleventsbackend.Models.OrganizationRole;
import de.flyndre.fleventsbackend.dtos.AccountInformation;
import de.flyndre.fleventsbackend.dtos.OrganizationInformation;
import de.flyndre.fleventsbackend.dtos.OrganizationPreview;
import de.flyndre.fleventsbackend.repositories.InvitationTokenRepository;
import de.flyndre.fleventsbackend.security.payload.request.LoginRequest;
import de.flyndre.fleventsbackend.security.payload.response.JwtResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-test.properties")
public class FullApiTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    InvitationTokenRepository tokenRepository;

    private final ObjectMapper mapper = new JsonMapper();



    @Test
    @WithMockUser(authorities = {"platformAdmin"})
    void apiTest() throws Exception {
        OrganizationInformation testOrga = testCreateOrganization();
        testGetAllOrganizations();
        testGetOrganization(testOrga);
        AccountInformation testAcc = testCreateAccount();
        InvitationToken token = testInviteAccountToOrga(testAcc,testOrga,OrganizationRole.admin);
        //JwtResponse jwt = testLogin(testAcc);
        testAcceptOrganizationInvitation(token);



        testDeleteOrganization(testOrga);
    }

    public OrganizationInformation testCreateOrganization() throws Exception {
        OrganizationPreview organizationPreview = new OrganizationPreview();
        organizationPreview.setName("Test Orga");
        organizationPreview.setDescription("Test description");
        organizationPreview.setCustomerNumber("001");
        organizationPreview.setPhoneContact("0193293093");
        var result = mockMvc.perform(MockMvcRequestBuilders.post("/api/platform/organizations")
                        .param("email","test@flyndre.de")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(organizationPreview)))
                .andExpect(status().isCreated())
                .andReturn();
        return mapper.readValue(result.getResponse().getContentAsString(),OrganizationInformation.class);
    }

    public void testGetAllOrganizations() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/organizations")).andExpect(status().isOk());
    }

    public void testDeleteOrganization(OrganizationInformation testOrga) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/platform/organizations/"+testOrga.getUuid()))
                .andExpect(status().isOk());
    }
    public void testGetOrganization(OrganizationInformation testOrga) throws Exception {
       var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/organizations/"+testOrga.getUuid()))
               .andExpect(status().isOk())
               .andReturn();
       OrganizationInformation orgaResult = mapper.readValue(result.getResponse().getContentAsString(), OrganizationInformation.class);
       assertThat(orgaResult.equals(testOrga));
    }
    public AccountInformation testCreateAccount()throws Exception{
        FleventsAccount accPre = new FleventsAccount();
        accPre.setEmail("test@flyndre.de");
        accPre.setFirstname("Olaf");
        accPre.setLastname("Müller");
        accPre.setSecret("1");
        var result = mockMvc.perform(MockMvcRequestBuilders.post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(accPre)))
                .andExpect(status().isCreated())
                .andReturn();
        return mapper.readValue(result.getResponse().getContentAsString(),AccountInformation.class);
    }
    public InvitationToken testInviteAccountToOrga(AccountInformation testAcc, OrganizationInformation testOrga, OrganizationRole role)throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.post("/api/organizations/"+testOrga.getUuid()+"/invite?email=test@flyndre.de&role="+role.toString()))
                .andExpect(status().isOk());
        InvitationToken token = tokenRepository.findAll().stream().findFirst().get();
        assertThat(token.getInvitedToId().equals(testOrga.getUuid()));
        assertThat(token.getRole().equals(role));
        return token;
    }

    public JwtResponse testLogin(AccountInformation testAcc) throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername(testAcc.getEmail());
        request.setPassword("TopSecret");
        var result = mockMvc.perform(MockMvcRequestBuilders.post("/api/accounts/login").
                contentType(MediaType.APPLICATION_JSON).
                content(mapper.writeValueAsString(request))).
                andExpect(status().isOk()).
                andReturn();
        return mapper.readValue(result.getResponse().getContentAsString(),JwtResponse.class);
    }

    public void testAcceptOrganizationInvitation(InvitationToken token) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/organizations/"+token.getInvitedToId()+"/add-account").param("token", token.getId())).andExpect(status().isOk());
    }
}
