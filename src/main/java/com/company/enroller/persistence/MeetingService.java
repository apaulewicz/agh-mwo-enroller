package com.company.enroller.persistence;

import java.util.Collection;

import com.company.enroller.model.Participant;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;

import com.company.enroller.model.Meeting;

@Component("meetingService")
public class MeetingService {

	DatabaseConnector connector;

	public MeetingService() {
		connector = DatabaseConnector.getInstance();
	}

	public Collection<Meeting> getAll() {
		String hql = "FROM Meeting";
		Query query = connector.getSession().createQuery(hql);
		return query.list();
	}

	public Meeting findById(Long id) {
		Meeting meeting = connector.getSession().get(Meeting.class, id);
		return meeting;
	}

	public void create(Meeting meeting) {
		Transaction transaction = connector.getSession().beginTransaction();
		connector.getSession().save(meeting);
		transaction.commit();
	}

	public void remove(Meeting meeting) {
		Transaction transaction = connector.getSession().beginTransaction();
		connector.getSession().delete(meeting);
		transaction.commit();
	}

	public void update(Meeting meeting) {
		Transaction transaction = connector.getSession().beginTransaction();
		connector.getSession().merge(meeting);
		transaction.commit();
	}

	public void addParticipant(Long id, String login) {
		Meeting meeting = connector.getSession().get(Meeting.class, id);
		Participant participant = connector.getSession().get(Participant.class, login);
		meeting.addParticipant(participant);
		Transaction transaction = connector.getSession().beginTransaction();
		connector.getSession().merge(meeting);
		transaction.commit();
	}

	public void removeParticipant(Long id, String login) {
		Meeting meeting = connector.getSession().get(Meeting.class, id);
		Participant participant = connector.getSession().get(Participant.class, login);
		meeting.removeParticipant(participant);
		Transaction transaction = connector.getSession().beginTransaction();
		connector.getSession().merge(meeting);
		transaction.commit();
	}


}
