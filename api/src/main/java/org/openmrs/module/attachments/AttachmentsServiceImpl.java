package org.openmrs.module.attachments;

import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.Visit;

import org.openmrs.api.context.Context;

import org.openmrs.module.attachments.obs.Attachment;

import java.util.ArrayList;
import java.util.List;

public class AttachmentsServiceImpl implements AttachmentsService {
	
	@Override
	public List<Attachment> getAttachments(Patient patient, Visit visit, Encounter encounter, boolean includeRetired) {
		
		List<Person> personList = new ArrayList<>();
		personList.add(patient);
		
		List<Attachment> attachments = new ArrayList<>();
		if (visit == null && encounter == null) {
			List<Obs> obs = Context.getObsService().getObservations(personList, null, null, null, null, null, null, null,
			    null, null, null, includeRetired);
			for (Obs observation : obs) {
				attachments.add(new Attachment(observation));
			}
			return attachments;
		} else if (visit == null) {
			List<Encounter> encounterList = new ArrayList<>();
			encounterList.add(encounter);
			List<Obs> obs = Context.getObsService().getObservations(personList, encounterList, null, null, null, null, null,
			    null, null, null, null, includeRetired);
			for (Obs observation : obs) {
				attachments.add(new Attachment(observation));
			}
			return attachments;
		} else {
			List<Encounter> encounterList = new ArrayList<>();
			encounterList.add(encounter);
			List<Obs> obs = Context.getObsService().getObservations(personList, encounterList, null, null, null, null, null,
			    null, null, null, null, includeRetired);
			for (Obs observation : obs) {
				if (observation.getEncounter().getVisit() == visit) {
					attachments.add(new Attachment(observation));
				}
			}
			return attachments;
		}
	}
	
}
