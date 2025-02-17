package com.company.enroller.controllers;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.company.enroller.model.Participant;
import com.company.enroller.persistence.MeetingService;
import com.company.enroller.persistence.ParticipantService;

@RunWith(SpringRunner.class)
@WebMvcTest(ParticipantRestController.class)
public class ParticipantRestControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private MeetingService meetingService;

	@MockBean
	private ParticipantService participantService;

	@Test
	public void getParticipants() throws Exception {
		Participant participant = new Participant();
		participant.setLogin("testlogin");
		participant.setPassword("testpassword");

		Collection<Participant> allParticipants = singletonList(participant);
		given(participantService.getAll()).willReturn(allParticipants);

		mvc.perform(get("/participants").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1))).andExpect(jsonPath("$[0].login", is(participant.getLogin())));
	}

	@Test
	public void addParticipant() throws Exception {
		Participant participant = new Participant();
		participant.setLogin("testlogin");
		participant.setPassword("testpassword");
		String inputJSON = "{\"login\":\"testlogin\", \"password\":\"somepassword\"}";

		given(participantService.findByLogin("testlogin")).willReturn((Participant)null);
		given(participantService.register(participant)).willReturn(participant);
		mvc.perform(post("/participants").content(inputJSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());

		given(participantService.findByLogin("testlogin")).willReturn(participant);
		mvc.perform(post("/participants").content(inputJSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isConflict());

		verify(participantService, times(2)).findByLogin("testlogin");
	}

	@Test
	public void removeParticipant() throws Exception {
		Participant participant = new Participant();
		participant.setLogin("testlogin");
		participant.setPassword("testpassword");
		String inputJSON = "{\"login\":\"testlogin\", \"password\":\"somepassword\"}";

		given(participantService.findByLogin("testlogin")).willReturn((Participant)null);
		given(participantService.register(participant)).willReturn(participant);
		mvc.perform(post("/participants").content(inputJSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());

		given(participantService.findByLogin("testlogin")).willReturn(participant);
		mvc.perform(delete("/participants/remove/testlogin").content(inputJSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());


		given(participantService.findByLogin("testestest")).willReturn((Participant)null);
		mvc.perform(delete("/participants/remove/testestest").content(inputJSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
	}

	@Test
	public void updateParticipant() throws Exception {
		Participant participant = new Participant();
		participant.setLogin("testlogin");
		participant.setPassword("testpassword");
		String inputJSON = "{\"login\":\"testlogin\", \"password\":\"somepassword\"}";

		given(participantService.findByLogin("testlogin")).willReturn((Participant)null);
		given(participantService.register(participant)).willReturn(participant);
		mvc.perform(post("/participants").content(inputJSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());

		Participant newParticipant = new Participant();
		newParticipant.setLogin("testlogin");
		newParticipant.setPassword("newTestPassword");
		String secondInputJSON = "{\"login\":\"testlogin\", \"password\":\"newTestPassword\"}";

		mvc.perform(put("/participants/update/testlogin").content(secondInputJSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());
		given(participantService.findByLogin("testlogin")).willReturn(newParticipant);
	}


}
