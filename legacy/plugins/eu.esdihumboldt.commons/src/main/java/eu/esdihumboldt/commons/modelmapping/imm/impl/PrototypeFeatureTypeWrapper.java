package eu.esdihumboldt.commons.modelmapping.imm.impl;

import java.util.HashSet;
import java.util.Set;

import org.opengis.metadata.identification.Identification;

import eu.esdihumboldt.commons.mediator.contextservice.hibernate.helpers.IdentificationHelper;

public class PrototypeFeatureTypeWrapper {

	private PrototypeFeatureType wrapperdClass;
	private Set identifications;

	PrototypeFeatureTypeWrapper() {
		wrapperdClass = new PrototypeFeatureType();
		identifications = new HashSet<IdentificationHelper>();
	}

	/**
	 * @return the wrapperdClass
	 */
	public PrototypeFeatureType getWrapperdClass() {
		return wrapperdClass;
	}

	/**
	 * @param wrapperdClass
	 *            the wrapperdClass to set
	 */
	public void setWrapperdClass(PrototypeFeatureType wrapperdClass) {
		this.wrapperdClass = wrapperdClass;
	}

	private void setIdentifications(
			HashSet<IdentificationHelper> identifications) {
		this.identifications = identifications;
		IdentificationHelper identification = (IdentificationHelper) identifications
				.toArray()[0];
		// wrapperdClass.setIdentification(identification);
	}

	@SuppressWarnings({ "unchecked", "unused" })
	private Set<Identification> getIdentifications() {
		return this.identifications;
	}

}
