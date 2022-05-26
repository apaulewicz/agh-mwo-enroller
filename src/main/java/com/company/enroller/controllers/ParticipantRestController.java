package com.company.enroller.controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.company.enroller.model.Participant;
import com.company.enroller.persistence.ParticipantService;

@RestController
@RequestMapping("/participants")
public class ParticipantRestController {

	@Autowired
	ParticipantService participantService;

	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<?> getParticipants() {
		Collection<Participant> participants = participantService.getAll();
		return new ResponseEntity<Collection<Participant>>(participants, HttpStatus.OK);
	}

	@RequestMapping(value = "/{login}", method = RequestMethod.GET)
	public ResponseEntity<?> getParticipant(@PathVariable("login") String login) {
		Participant participant = participantService.findByLogin(login);
		if (participant == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Participant>(participant, HttpStatus.OK);
	}

	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<?> registerParticipant(@RequestBody Participant participant) {
		var result = participantService.findByLogin(participant.getLogin());

		if (result != null) {
			return new ResponseEntity<String>(
					"Unable to create. A participant with login " + participant.getLogin() + " already exist.",
					HttpStatus.CONFLICT);
		}
		Participant newParticipant = participantService.register(participant);
		return new ResponseEntity<Participant>(newParticipant, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/remove/{login}", method = RequestMethod.DELETE)
	public ResponseEntity<?> remove(@PathVariable("login") String login) {
		Participant entity = participantService.findByLogin(login);
		if (entity == null) {
			return new ResponseEntity<String>("Participant not found", HttpStatus.NOT_FOUND);
		}
		participantService.remove(entity);
		return new ResponseEntity<Participant>(HttpStatus.NO_CONTENT);
	}

	@RequestMapping(value = "/update/{login}", method = RequestMethod.PUT)
	public ResponseEntity<?> update(@PathVariable("login") String login, @RequestBody Participant participant) {
		Participant entity = participantService.findByLogin(login);
		if (entity == null) {
			return new ResponseEntity<String>("Participant not found", HttpStatus.NOT_FOUND);
		}
		entity.setPassword(participant.getPassword());
		participantService.update(entity);
		return new ResponseEntity<Participant>(HttpStatus.NO_CONTENT);
	}

}

