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

import java.util.Collection;

import org.geotools.metadata.iso.identification.IdentificationImpl;
import org.opengis.util.InternationalString;

/**
 * This is a a subclass of the IdentificationImpl contains id-field and no-args
 * constractor, to enable Hibernate the persisting of Identification-objects.
 * 
 * 
 * @author Anna Pitaev, Logica CMG
 * @version $Id: PrototypeIdentification.java,v 1.1 2007-11-23 08:51:46 pitaeva
 *          Exp $
 */
public class IdentificationHelper {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private IdentificationImpl delegate = new IdentificationImpl();

	private long identificationid;

	/**
	 * default constructor
	 */
	public IdentificationHelper() {
		super();
	}

	/**
	 * @return unique identifier for the database.
	 */
	public long getIdentificationID() {
		return identificationid;
	}

	/**
	 * 
	 * @param id
	 *            unique identifier for the database.
	 */
	public void setIdentificationID(long id) {
		this.identificationid = id;
	}

	/**
	 * @return
	 * @see org.geotools.metadata.iso.identification.IdentificationImpl#getAbstract()
	 */
	public InternationalString getAbstract() {
		return delegate.getAbstract();
	}

	/**
	 * @return
	 * @see org.geotools.metadata.iso.identification.IdentificationImpl#getCredits()
	 */
	public Collection getCredits() {
		return delegate.getCredits();
	}

	/**
	 * @return
	 * @see org.geotools.metadata.iso.identification.IdentificationImpl#getPurpose()
	 */
	public InternationalString getPurpose() {
		return delegate.getPurpose();
	}

	/**
	 * @param newValue
	 * @see org.geotools.metadata.iso.identification.IdentificationImpl#setAbstract(org.opengis.util.InternationalString)
	 */
	public void setAbstract(InternationalString newValue) {
		delegate.setAbstract(newValue);
	}

	/**
	 * @param newValues
	 * @see org.geotools.metadata.iso.identification.IdentificationImpl#setCredits(java.util.Collection)
	 */
	public void setCredits(Collection newValues) {
		delegate.setCredits(newValues);
	}

	/**
	 * @param newValue
	 * @see org.geotools.metadata.iso.identification.IdentificationImpl#setPurpose(org.opengis.util.InternationalString)
	 */
	public void setPurpose(InternationalString newValue) {
		delegate.setPurpose(newValue);
	}

	/**
	 * @return the delegate
	 */
	public IdentificationImpl getDelegate() {
		return delegate;
	}

	/**
	 * @param delegate
	 *            the delegate to set
	 */
	public void setDelegate(IdentificationImpl delegate) {
		this.delegate = delegate;
	}

}
