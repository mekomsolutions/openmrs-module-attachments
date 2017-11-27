package org.openmrs.module.attachments.rest;

import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.attachments.AttachmentsConstants;
import org.openmrs.module.attachments.obs.Attachment;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.GenericRestException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@Resource(name = RestConstants.VERSION_1 + "/attachment", supportedClass = Attachment.class, supportedOpenmrsVersions = {
        "1.10.*", "1.11.*", "1.12.*" })
public class AttachmentResource1_10 extends DataDelegatingCrudResource<Attachment> {
	
	protected static final String REASON = "REST web service";
	
	@Override
	public Attachment newDelegate() {
		return new Attachment();
	}
	
	@Override
	public Attachment save(Attachment delegate) {
		Obs obs = Context.getObsService().saveObs(delegate.getObs(), REASON);
		return new Attachment(obs);
	}
	
	@Override
	public Attachment getByUniqueId(String uniqueId) {
		Obs obs = Context.getObsService().getObsByUuid(uniqueId);
		if (!obs.isComplex())
			throw new GenericRestException(uniqueId + " does not identify a complex obs.", null);
		else {
			obs = Context.getObsService().getComplexObs(obs.getId(), AttachmentsConstants.ATT_VIEW_CRUD);
			return new Attachment(obs);
		}
	}
	
	@Override
	protected void delete(Attachment delegate, String reason, RequestContext context) throws ResponseException {
		String encounterUuid = delegate.getObs().getEncounter() != null ? delegate.getObs().getEncounter().getUuid() : null;
		Context.getObsService().voidObs(delegate.getObs(), REASON);
		voidEncounterIfEmpty(Context.getEncounterService(), encounterUuid);
	}
	
	@Override
	public void purge(Attachment delegate, RequestContext context) throws ResponseException {
		String encounterUuid = delegate.getObs().getEncounter() != null ? delegate.getObs().getEncounter().getUuid() : null;
		Context.getObsService().purgeObs(delegate.getObs());
		voidEncounterIfEmpty(Context.getEncounterService(), encounterUuid);
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("comment");
		return description;
	}
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("uuid");
		description.addProperty("comment");
		description.addSelfLink();
		return description;
	}
	
	/**
	 * Voids the encounter if it contains no non-voided obs.
	 * 
	 * @param encounterService
	 * @param encounterUuid
	 */
	public static void voidEncounterIfEmpty(EncounterService encounterService, String encounterUuid) {
		Encounter encounter = encounterService.getEncounterByUuid(encounterUuid);
		if (encounter != null && encounter.getAllObs().size() == 0) {
			encounterService.voidEncounter(encounter, "foo");
		}
	}
}
