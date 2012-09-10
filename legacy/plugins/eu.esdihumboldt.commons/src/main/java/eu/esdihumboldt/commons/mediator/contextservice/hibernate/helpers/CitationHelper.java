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

import org.geotools.util.SimpleInternationalString;

/**
 * This is a a subclass of the CitationImpl contains id-field and no-args
 * constractor, to enable Hibernate the persisting of Citation-objects.
 * 
 * 
 * @author Anna Pitaev, Logica CMG
 * @version $Id: Citation.java,v 1.3 2007-12-04 11:35:44 pitaeva Exp $
 */
public class CitationHelper {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** unique identifier in the database */
	private long id;

	private String ISBN;
	private String ISSN;
	private SimpleInternationalString title;
	private SimpleInternationalString otherCitationDetails;

	/**
	 * @return the iSBN
	 */
	public String getISBN() {
		return ISBN;
	}

	/**
	 * @param isbn
	 *            the iSBN to set
	 */
	public void setISBN(String isbn) {
		ISBN = isbn;
	}

	/**
	 * @return the iSSN
	 */
	public String getISSN() {
		return ISSN;
	}

	/**
	 * @param issn
	 *            the iSSN to set
	 */
	public void setISSN(String issn) {
		ISSN = issn;
	}

	/**
	 * @return the title
	 */
	public SimpleInternationalString getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(SimpleInternationalString title) {
		this.title = title;
	}

	/**
	 * @return the otherCitationDetails
	 */
	public SimpleInternationalString getOtherCitationDetails() {
		return otherCitationDetails;
	}

	/**
	 * @param otherCitationDetails
	 *            the otherCitationDetails to set
	 */
	public void setOtherCitationDetails(
			SimpleInternationalString otherCitationDetails) {
		this.otherCitationDetails = otherCitationDetails;
	}

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

}
