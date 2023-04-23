package de.flyndre.fleventsbackend;

import de.flyndre.fleventsbackend.services.ApiService;
import de.flyndre.fleventsbackend.services.EventService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
//URI Variablen Entsprechend austauschen je nach Testdaten, da noch keine Testdatenbank existiert!!!
class FleventsBackendApplicationTests {
	@Autowired
	private MockMvc mockMvc;

	@Mock
	ApiService apiService;

	@Test
	void contextLoads() {
	}


	@Test
	void getOrgaTest() throws Exception {
		mockMvc.perform(get("/api/organizations")).andExpect(status().isOk());
	}
	@Test
	void getOrganizationTest() throws Exception {
		mockMvc.perform(get("/api/organizations/{id}", "2c918084875296040187529636ff0000")).andExpect(status().isOk());
	}
	@Test
	void getOrganizationAccountsTest() throws Exception {
		mockMvc.perform(get("/api/organizations/{id}/accounts", "2c918084875296040187529636ff0000")).andExpect(status().isOk());
	}
	@Test
	void getOrganizationEventsTest() throws Exception {
		mockMvc.perform(get("/api/organizations/{id}/events", "2c918084875296040187529636ff0000")).andExpect(status().isOk());
	}
	@Test
	void getAccountTest() throws Exception {
		mockMvc.perform(get("/api/accounts/{accountId}", "2c9180848752960401875296378c0002")).andExpect(status().isOk());
	}
	@Test
	void getBookedEventsAccountTest() throws Exception {
		mockMvc.perform(get("/api/accounts/{accountId}/booked-events", "2c9180848752960401875296378c0002")).andExpect(status().isOk());
	}
	@Test
	void getManagedEventsAccountTest() throws Exception {
		mockMvc.perform(get("/api/accounts/{accountId}/managed-events", "2c9180848752960401875296378c0002")).andExpect(status().isOk());
	}
	@Test
	void getExploreEventsAccountTest() throws Exception {
		mockMvc.perform(get("/api/accounts/{accountId}/explore-events", "2c9180848752960401875296378c0002")).andExpect(status().isOk());
	}
	@Test
	void getOrganizationsOfAccountTest() throws Exception {
		mockMvc.perform(get("/api/accounts/{accountId}/managed-organizations", "2c9180848752960401875296378c0002")).andExpect(status().isOk());
	}
	@Test
	void getOrganizersOfEvent() throws Exception {
		mockMvc.perform(get("/api/events/{eventId}/organizers", "2c9180848752d53801875315d979000b")).andExpect(status().isOk());
	}
	@Test
	void getAttendeesOfEvent() throws Exception {
		mockMvc.perform(get("/api/events/{eventId}/attendees", "2c9180848752d53801875315d979000b")).andExpect(status().isOk());
	}
	@Test
	void getEventTest() throws Exception {
		mockMvc.perform(get("/api/events")).andExpect(status().isOk());
	}

	@Test
	void getCheckedInTest() throws  Exception {
		mockMvc.perform(get("/{eventId}/checkedIns", "2c9180848752d53801875315d979000b")).andExpect(status().isOk());
	}



}
