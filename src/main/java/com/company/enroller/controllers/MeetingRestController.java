package com.company.enroller.controllers;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import com.company.enroller.persistence.MeetingService;
import com.company.enroller.persistence.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/meetings")
public class MeetingRestController {

    @Autowired
    MeetingService meetingService;

    @Autowired
    ParticipantService participantService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getMeetings() {
        Collection<Meeting> meetings = meetingService.getAll();
        return new ResponseEntity<Collection<Meeting>>(meetings, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getMeeting(@PathVariable("id") Long id) {
        Meeting meeting = meetingService.findById(id);
        if (meeting == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> createMeeting(@RequestBody Meeting meeting) {
        if (meetingService.findById(meeting.getId()) != null) {
            return new ResponseEntity<String>("Unable to create. A meeting with id " + meeting.getId() + " already exist.", HttpStatus.CONFLICT);
        }
        meetingService.create(meeting);
        return new ResponseEntity<>(meeting, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> removeMeeting(@PathVariable("id") Long id) {
        Meeting entity = meetingService.findById(id);
        if (entity == null) {
            return new ResponseEntity<String>("Meeting not found", HttpStatus.NOT_FOUND);
        }
        meetingService.remove(entity);
        return new ResponseEntity<Meeting>(entity, HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateMeeting(@PathVariable("id") Long id, @RequestBody Meeting meeting) {
        Meeting entity = meetingService.findById(id);
        if (entity == null) {
            return new ResponseEntity<String>("Meeting not found", HttpStatus.NOT_FOUND);
        }
        entity.setTitle(meeting.getTitle());
        entity.setDescription(meeting.getDescription());
        entity.setDate(meeting.getDate());
        meetingService.update(meeting);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "{id}/allparticipants", method = RequestMethod.GET)
    public ResponseEntity<?> getParticipants(@PathVariable("id") Long id) {
        Meeting meeting = meetingService.findById(id);
        if (meeting == null) {
            return new ResponseEntity<String>("Meeting not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Collection<Participant>>(meeting.getParticipants(), HttpStatus.OK);
    }

    @RequestMapping(value = "{meetingId}/addparticipant/{participantLogin}", method = RequestMethod.POST)
    public ResponseEntity<?> addParticipant(@PathVariable("meetingId") Long meetingId, @PathVariable("participantLogin") String participantLogin) {
        Meeting meeting = meetingService.findById(meetingId);
        if (meeting == null) {
            return new ResponseEntity<String>("Meeting not found", HttpStatus.NOT_FOUND);
        }

        Participant participant = participantService.findByLogin(participantLogin);
        if (participant == null) {
            return new ResponseEntity<String>("Participant not found", HttpStatus.NOT_FOUND);
        }

        this.meetingService.addParticipant(meetingId, participantLogin);
        return new ResponseEntity<Collection<Participant>>(meeting.getParticipants(), HttpStatus.OK);
    }

    @RequestMapping(value = "{meetingId}/deleteparticipant/{participantLogin}", method = RequestMethod.POST)
    public ResponseEntity<?> deleteParticipant(@PathVariable("meetingId") Long meetingId, @PathVariable("participantLogin") String participantLogin) {
        Meeting meeting = meetingService.findById(meetingId);
        if (meeting == null) {
            return new ResponseEntity<String>("Meeting not found", HttpStatus.NOT_FOUND);
        }

        Participant participant = participantService.findByLogin(participantLogin);
        if (participant == null) {
            return new ResponseEntity<String>("Participant not found", HttpStatus.NOT_FOUND);
        }

        this.meetingService.removeParticipant(meetingId, participantLogin);
        return new ResponseEntity<Collection<Participant>>(meeting.getParticipants(), HttpStatus.OK);
    }
}
