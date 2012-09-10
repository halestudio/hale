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

import org.geotools.metadata.iso.IdentifierImpl;

/**
 * This is a a subclass of the IdentifierImpl contains id-field and no-args
 * constractor, to enable Hibernate the persisting of the Identifier-objects.
 * 
 * 
 * @author Anna Pitaev, Logica CMG
 * @version $Id: Identifier.java,v 1.2 2007-12-14 09:24:03 pitaeva Exp $
 */
public class IdentifierHelper {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private IdentifierImpl delegate = new IdentifierImpl();

	/** unique identifier in the database */
	private long id;

	/**
	 * 
	 * @return unique identifier for the database.
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
	 * @see org.geotools.metadata.iso.IdentifierImpl#getCode()
	 */
	public String getCode() {
		return delegate.getCode();
	}

	/**
	 * @return
	 * @see org.geotools.metadata.iso.IdentifierImpl#getVersion()
	 */
	public String getVersion() {
		return delegate.getVersion();
	}

	/**
	 * @param newValue
	 * @see org.geotools.metadata.iso.IdentifierImpl#setCode(java.lang.String)
	 */
	public void setCode(String newValue) {
		delegate.setCode(newValue);
	}

	/**
	 * @param newValue
	 * @see org.geotools.metadata.iso.IdentifierImpl#setVersion(java.lang.String)
	 */
	public void setVersion(String newValue) {
		delegate.setVersion(newValue);
	}

	/**
	 * @return the delegate
	 */
	public IdentifierImpl getDelegate() {
		return delegate;
	}

	/**
	 * @param delegate
	 *            the delegate to set
	 */
	public void setDelegate(IdentifierImpl delegate) {
		this.delegate = delegate;
	}

}
