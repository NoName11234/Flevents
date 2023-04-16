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
		mockMvc.perform(get("/api/organizations/{id}", "402882e4874e461001874e4637a00000")).andExpect(status().isOk());
	}
	@Test
	void getOrganizationAccountsTest() throws Exception {
		mockMvc.perform(get("/api/organizations/{id}/accounts", "402882e4874e461001874e4637a00000")).andExpect(status().isOk());
	}
	@Test
	void getOrganizationEventsTest() throws Exception {
		mockMvc.perform(get("/api/organizations/{id}/events", "402882e4874e461001874e4637a00000")).andExpect(status().isOk());
	}
	@Test
	void getAccountTest() throws Exception {
		mockMvc.perform(get("/api/accounts/{accountId}", "402882e4874e461001874e4637c40002")).andExpect(status().isOk());
	}
	@Test
	void getBookedEventsAccountTest() throws Exception {
		mockMvc.perform(get("/api/accounts/{accountId}/booked-events", "402882e4874e461001874e4637c40002")).andExpect(status().isOk());
	}
	@Test
	void getManagedEventsAccountTest() throws Exception {
		mockMvc.perform(get("/api/accounts/{accountId}/managed-events", "402882e4874e461001874e4637c40002")).andExpect(status().isOk());
	}
	@Test
	void getExploreEventsAccountTest() throws Exception {
		mockMvc.perform(get("/api/accounts/{accountId}/explore-events", "402882e4874e461001874e4637c40002")).andExpect(status().isOk());
	}
	@Test
	void getOrganizationsOfAccountTest() throws Exception {
		mockMvc.perform(get("/api/accounts/{accountId}/managed-organizations", "402882e4874e461001874e4637c40002")).andExpect(status().isOk());
	}
	@Test
	void getOrganizersOfEvent() throws Exception {
		mockMvc.perform(get("/api/events/{eventId}/organizers", "402882e4874e461001874e46fe5d000e")).andExpect(status().isOk());
	}
	@Test
	void getAttendeesOfEvent() throws Exception {
		mockMvc.perform(get("/api/events/{eventId}/attendees", "402882e4874e461001874e46fe5d000e")).andExpect(status().isOk());
	}
	@Test
	void getEventTest() throws Exception {
		mockMvc.perform(get("/api/events")).andExpect(status().isOk());
	}



}
