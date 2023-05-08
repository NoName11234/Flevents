package de.flyndre.fleventsbackend;

import de.flyndre.fleventsbackend.Models.Post;
import de.flyndre.fleventsbackend.services.ApiService;
import de.flyndre.fleventsbackend.services.EventService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
//URI Variablen Entsprechend austauschen je nach Testdaten, da noch keine Testdatenbank existiert!!!
class FleventsBackendApplicationTests {
	@Autowired
	private MockMvc mockMvc;

	String uuid="";
	String accesstoken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI0MDI4MzJkMjg3Y2NkZWNhMDE4N2NjZTFiYzQ2MDAwMCIsImlhdCI6MTY4MzUzNTQ3OCwiZXhwIjoxNjgzNjIxODc4fQ.s7CSR1xC7VEkadgAveQTUbzQgVcF9ncl1qW478WqkTS7sTxCipHQT3qYyImgotNyd3KK9ZMwDk2zX4o7UcSUqw";
	@Mock
	ApiService apiService;

	@Test
	void contextLoads() {
	}

	@Test
	@WithMockUser
	void login() throws  Exception {
		MvcResult result = mockMvc.perform(post("/api/accounts/login", "2c9180848752d53801875315d979000b").contentType(MediaType.APPLICATION_JSON).content("{\"username\":\"pixelpasi@gmail.com\",\"password\":\"1234\"}")).andExpect(status().isOk()).andReturn();
		System.out.println(result.getResponse().getContentAsString());
		String resultString = result.getResponse().getContentAsString().split(",")[4].replace("\"accessToken\":", "").replace("\"", "");
		System.out.println("HERR KESCHER!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! " + resultString);
		accesstoken = resultString;
	}

	@Test
	@WithMockUser
	void getOrgaTest() throws Exception {
		mockMvc.perform(get("/api/organizations").header(HttpHeaders.AUTHORIZATION, "Bearer " + accesstoken)).andExpect(status().isOk());
	}

	@Test
	@WithMockUser
	void getOrganizationTest() throws Exception {
		createOrganizationTest();
		mockMvc.perform(get("/api/organizations/{id}", uuid/*"2c918084875296040187529636ff0000"*/).header(HttpHeaders.AUTHORIZATION, "Bearer " + accesstoken)).andExpect(status().isOk());
	}

	@Test
	@WithMockUser
	void getOrganizationAccountsTest() throws Exception {
		mockMvc.perform(get("/api/organizations/{id}/accounts", "2c918084875296040187529636ff0000").header(HttpHeaders.AUTHORIZATION, "Bearer " + accesstoken)).andExpect(status().isOk());
	}

	@Test
	@WithMockUser
	void getOrganizationEventsTest() throws Exception {
		mockMvc.perform(get("/api/organizations/{id}/events", "2c918084875296040187529636ff0000").header(HttpHeaders.AUTHORIZATION, "Bearer " + accesstoken)).andExpect(status().isOk());
	}

	@Test
	@WithMockUser
	void getAccountTest() throws Exception {
		mockMvc.perform(get("/api/accounts/{accountId}", "2c9180848752960401875296378c0002").header(HttpHeaders.AUTHORIZATION, "Bearer " + accesstoken)).andExpect(status().isOk());
	}

	@Test
	@WithMockUser
	void getBookedEventsAccountTest() throws Exception {
		mockMvc.perform(get("/api/accounts/{accountId}/booked-events", "2c9180848752960401875296378c0002").header(HttpHeaders.AUTHORIZATION, "Bearer " + accesstoken)).andExpect(status().isOk());
	}

	@Test
	@WithMockUser
	void getManagedEventsAccountTest() throws Exception {
		mockMvc.perform(get("/api/accounts/{accountId}/managed-events", "2c9180848752960401875296378c0002").header(HttpHeaders.AUTHORIZATION, "Bearer " + accesstoken)).andExpect(status().isOk());
	}

	@Test
	@WithMockUser
	void getExploreEventsAccountTest() throws Exception {
		mockMvc.perform(get("/api/accounts/{accountId}/explore-events", "2c9180848752960401875296378c0002").header(HttpHeaders.AUTHORIZATION, "Bearer " + accesstoken)).andExpect(status().isOk());
	}

	@Test
	@WithMockUser
	void getOrganizationsOfAccountTest() throws Exception {
		mockMvc.perform(get("/api/accounts/{accountId}/managed-organizations", "2c9180848752960401875296378c0002").header(HttpHeaders.AUTHORIZATION, "Bearer " + accesstoken)).andExpect(status().isOk());
	}

	@Test
	@WithMockUser
	void getOrganizersOfEvent() throws Exception {
		mockMvc.perform(get("/api/events/{eventId}/organizers", "2c9180848752d53801875315d979000b").header(HttpHeaders.AUTHORIZATION, "Bearer " + accesstoken)).andExpect(status().isOk());
	}

	@Test
	@WithMockUser
	void getAttendeesOfEvent() throws Exception {
		mockMvc.perform(get("/api/events/{eventId}/attendees", "2c9180848752d53801875315d979000b").header(HttpHeaders.AUTHORIZATION, "Bearer " + accesstoken)).andExpect(status().isOk());
	}

	@Test
	@WithMockUser
	void getEventTest() throws Exception {
		mockMvc.perform(get("/api/events").header(HttpHeaders.AUTHORIZATION, "Bearer " + accesstoken)).andExpect(status().isOk());
	}


	/*
	* Kann so nicht funktionieren, falsche API-Adresse
	* sollte gefixt sein
	* */
	@Test
	@WithMockUser
	void getCheckedInTest() throws  Exception {
		mockMvc.perform(get("/api/{eventId}/check-ins", "2c9180848752d53801875315d979000b").header(HttpHeaders.AUTHORIZATION, "Bearer " + accesstoken)).andExpect(status().isOk());
	}

	@Test
	void postEvent() throws  Exception {
		login();
		mockMvc.perform(post("/api/organizations/{orgaUuid}/create-event", "402832d287f731910187f7319d180001").header(HttpHeaders.AUTHORIZATION, "Bearer " + accesstoken).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Testevent\",\"description\":\"Testbeschreibung\",\"startTime\":\"2023-04-23T12:30\",\"endeTime\":\"2023-04-23T13:30\",\"location\":\"Horb a.N.\"}")).andExpect(status().isOk());
	}

	@Test
	void createUser() throws  Exception {
		mockMvc.perform(post("/api/accounts").header(HttpHeaders.AUTHORIZATION, "Bearer " + accesstoken).contentType(MediaType.APPLICATION_JSON).content("{\n" +
				"    \"firstname\":\"tete\",\n" +
				"    \"lastname\":\"tester\",\n" +
				"    \"email\":\"test@flevents.de\",\n" +
				"    \"secret\":\"123\"\n" +
				"\n" +
				"}")).andExpect(status().isOk());
	}

	@Test
	@WithMockUser
	void createOrganizationTest() throws  Exception {
		MvcResult mvcResult =  mockMvc.perform(post("/api/platform/organizations?email=test@flyndre.de").header(HttpHeaders.AUTHORIZATION, "Bearer " + accesstoken).contentType(MediaType.APPLICATION_JSON).content(
				"{"+
						"\"name\": \"TestOrga\","+
						"\"customerNumber\": \"99999\","+
						"\"phoneContact\":\"012345678910\""+
						"}"
		)).andExpect(status().isCreated()).andReturn();
		uuid = mvcResult.getResponse().getContentAsString().split("\"")[3];
	}

	@Test
	void logout() throws  Exception {
		login();
		mockMvc.perform(post("/api/accounts/logout").header(HttpHeaders.AUTHORIZATION, "Bearer " + accesstoken).contentType(MediaType.APPLICATION_JSON).content("{\n" +
				"    \"token\":\""+ accesstoken +"\"" +
				"}")).andExpect(status().isOk());
	}

	@Test
	void setMailConfigTest() throws Exception {
		createOrganizationTest();
		String mainConfig =
				"{" +
						"\"registerMessage\": \"TestReg\","+
						"\"infoMessage\": \"TestInfo\",\n"+
						//"\"infoMessageTime\": \"2023-05-20T10:40:00\","+
						"\"feedbackMessage\": \"TestFeedback\","+
						//"\"feedbackMessageTime\": \"2023-05-20T10:40:00\","+
						"\"organizationInvitation\": \"TestOrgaInvite\","+
						"\"eventInvitation\": \"TestEventInvite\""+
						"}";
		String url = "/api/organizations/"+uuid+"/mailConfig";
		mockMvc.perform(post(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accesstoken).contentType(MediaType.APPLICATION_JSON).content(
				mainConfig)).andExpect(status().isOk());
		deleteOrga();
	}
	void setMailConfig() throws Exception {
		String mainConfig =
				"{" +
						"\"registerMessage\": \"TestReg\","+
						"\"infoMessage\": \"TestInfo\",\n"+
						"\"infoMessageTime\": \"2023-05-20T10:40:00\","+
						"\"feedbackMessage\": \"TestFeedback\","+
						"\"feedbackMessageTime\": \"2023-05-20T10:40:00\","+
						"\"organizationInvitation\": \"TestOrgaInvite\","+
						"\"eventInvitation\": \"TestEventInvite\""+
						"}";
		String url = "/api/organization/"+uuid+"/mailConfig";
		mockMvc.perform(post(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + accesstoken).contentType(MediaType.APPLICATION_JSON).content(
				mainConfig)).andExpect(status().isCreated());

	}

	@Test
	void getMailConfigTest() throws Exception {
		createOrganizationTest();
		setMailConfig();
		String mailConfig =
				"{" +
						"\"registerMessage\": \"TestReg\","+
						"\"infoMessage\": \"TestInfo\",\n"+
						"\"infoMessageTime\": \"2023-05-20T10:40:00\","+
						"\"feedbackMessage\": \"TestFeedback\","+
						"\"feedbackMessageTime\": \"2023-05-20T10:40:00\","+
						"\"organizationInvitation\": \"TestOrgaInvite\","+
						"\"eventInvitation\": \"TestEventInvite\""+
						"}";
		mockMvc.perform(get("/api/organization/"+uuid+"/mailConfig").header(HttpHeaders.AUTHORIZATION, "Bearer " + accesstoken)).andExpect(status().isOk());
		deleteOrga();
	}

	@Test
	void deleteOrgaTest() throws  Exception{
		createOrganizationTest();
		mockMvc.perform(delete("/api/platform/organizations/"+uuid).header(HttpHeaders.AUTHORIZATION, "Bearer " + accesstoken)).andExpect(status().isOk());
	}

	void deleteOrga() throws  Exception{
		mockMvc.perform(delete("/api/platform/organizations/"+uuid).header(HttpHeaders.AUTHORIZATION, "Bearer " + accesstoken)).andExpect(status().isOk());
	}


}
