/*
 * HUMBOLDT: A Framework for Data Harmonization and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this website:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to : http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.commons.mediator.contextservice.hibernate.helpers;

import org.geotools.metadata.iso.citation.ResponsiblePartyImpl;
import org.opengis.metadata.citation.Contact;
import org.opengis.metadata.citation.Role;
import org.opengis.util.InternationalString;

/**
 * This is a a subclass of the ResponsiblePartyImpl contains id-field and
 * no-args constractor, to enable Hibernate the persisting of Citation-objects.
 * 
 * 
 * @author Anna Pitaev, Logica CMG
 * @version $Id: ResponsibleParty.java,v 1.2 2007-11-15 13:32:09 pitaeva Exp $
 */
public class ResponsiblePartyHelper {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ResponsiblePartyImpl delegate = new ResponsiblePartyImpl();
	private long id;

	/**
	 * @return id unique identifier for the database.
	 */
	public long getId() {
		return id;
	}

	/**
	 * 
	 * @param id
	 *            unique identifier for the database.
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return
	 * @see org.geotools.metadata.iso.citation.ResponsiblePartyImpl#getIndividualName()
	 */
	public String getIndividualName() {
		return delegate.getIndividualName();
	}

	/**
	 * @return
	 * @see org.geotools.metadata.iso.citation.ResponsiblePartyImpl#getOrganisationName()
	 */
	public InternationalString getOrganisationName() {
		return delegate.getOrganisationName();
	}

	/**
	 * @return
	 * @see org.geotools.metadata.iso.citation.ResponsiblePartyImpl#getPositionName()
	 */
	public InternationalString getPositionName() {
		return delegate.getPositionName();
	}

	/**
	 * @return
	 * @see org.geotools.metadata.iso.citation.ResponsiblePartyImpl#getRole()
	 */
	public Role getRole() {
		return delegate.getRole();
	}

	/**
	 * @param newValue
	 * @see org.geotools.metadata.iso.citation.ResponsiblePartyImpl#setContactInfo(org.opengis.metadata.citation.Contact)
	 */
	public void setContactInfo(Contact newValue) {
		delegate.setContactInfo(newValue);
	}

	/**
	 * @param newValue
	 * @see org.geotools.metadata.iso.citation.ResponsiblePartyImpl#setIndividualName(java.lang.String)
	 */
	public void setIndividualName(String newValue) {
		delegate.setIndividualName(newValue);
	}

	/**
	 * @param newValue
	 * @see org.geotools.metadata.iso.citation.ResponsiblePartyImpl#setOrganisationName(org.opengis.util.InternationalString)
	 */
	public void setOrganisationName(InternationalString newValue) {
		delegate.setOrganisationName(newValue);
	}

	/**
	 * @param newValue
	 * @see org.geotools.metadata.iso.citation.ResponsiblePartyImpl#setPositionName(org.opengis.util.InternationalString)
	 */
	public void setPositionName(InternationalString newValue) {
		delegate.setPositionName(newValue);
	}

	/**
	 * @param newValue
	 * @see org.geotools.metadata.iso.citation.ResponsiblePartyImpl#setRole(org.opengis.metadata.citation.Role)
	 */
	public void setRole(Role newValue) {
		delegate.setRole(newValue);
	}

	/**
	 * @return the delegate
	 */
	public ResponsiblePartyImpl getDelegate() {
		return delegate;
	}

	/**
	 * @param delegate
	 *            the delegate to set
	 */
	public void setDelegate(ResponsiblePartyImpl delegate) {
		this.delegate = delegate;
	}

}
