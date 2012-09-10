/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.commons.modelmapping.imm.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import eu.esdihumboldt.specification.modelrepository.abstractfc.VersionInformation;

/**
 * A Default implementation of the {@link VersionInformation} interface.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id: DefaultVersionInformation.java,v 1.4 2007-12-06 13:20:25
 *          pitaeva Exp $
 */
public class DefaultVersionInformation implements VersionInformation,
		Serializable {

	// Fields ..................................................................

	private long id;
	private UUID uid;

	private int majorVersionNumber;

	private int minorVersionNumber;

	private int buildNumber;

	private Date expirationDate;

	private Date versionDate;

	// Constructors ............................................................

	/**
	 * protected no-args constructor.
	 */
	public DefaultVersionInformation() {
		this.uid = UUID.randomUUID();

	}

	/**
	 * The default constructor, containing just version numbers, but no date
	 * information.
	 * 
	 * @param majorVersionNumber
	 * @param minorVersionNumber
	 * @param buildNumber
	 */
	public DefaultVersionInformation(int majorVersionNumber,
			int minorVersionNumber, int buildNumber) {
		this();
		this.majorVersionNumber = majorVersionNumber;
		this.minorVersionNumber = minorVersionNumber;
		this.buildNumber = buildNumber;
		this.uid = UUID.randomUUID();

	}

	/**
	 * Full constructor.
	 * 
	 * @param majorVersionNumber
	 * @param minorVersionNumber
	 * @param buildNumber
	 * @param versionDate
	 * @param expirationDate
	 */
	public DefaultVersionInformation(int majorVersionNumber,
			int minorVersionNumber, int buildNumber, Date versionDate,
			Date expirationDate) {
		this(majorVersionNumber, minorVersionNumber, buildNumber);
		this.versionDate = versionDate;
		this.expirationDate = expirationDate;
		this.uid = UUID.randomUUID();

	}

	// VersionInformation operations ...........................................

	/**
	 * @see eu.esdihumboldt.specification.modelrepository.abstractfc.VersionInformation#getBuildNumber()
	 */
	public int getBuildNumber() {
		return this.buildNumber;
	}

	/**
	 * @see eu.esdihumboldt.specification.modelrepository.abstractfc.VersionInformation#getExpirationDate()
	 */
	public Date getExpirationDate() {
		return this.expirationDate;
	}

	/**
	 * @see eu.esdihumboldt.specification.modelrepository.abstractfc.VersionInformation#getMajorVersionNumber()
	 */
	public int getMajorVersionNumber() {
		return this.majorVersionNumber;
	}

	/**
	 * @see eu.esdihumboldt.specification.modelrepository.abstractfc.VersionInformation#getMinorVersionNumber()
	 */
	public int getMinorVersionNumber() {
		return this.minorVersionNumber;
	}

	/**
	 * @see eu.esdihumboldt.specification.modelrepository.abstractfc.VersionInformation#getVersionDate()
	 */
	public Date getVersionDate() {
		return this.versionDate;
	}

	// DefaultVersionInformation operations ....................................

	/**
	 * @return the uid
	 */
	public UUID getUid() {
		return this.uid;
	}

	/**
	 * @return unique identifier for the database.
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id
	 *            unique identifier for the database.
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @param uid
	 *            the uid to set
	 */

	@SuppressWarnings("unused")
	private void setUid(UUID uid) {
		this.uid = uid;
	}

	/**
	 * @param majorVersionNumber
	 *            the majorVersionNumber to set
	 */
	public void setMajorVersionNumber(int majorVersionNumber) {
		this.majorVersionNumber = majorVersionNumber;
	}

	/**
	 * @param minorVersionNumber
	 *            the minorVersionNumber to set
	 */
	public void setMinorVersionNumber(int minorVersionNumber) {
		this.minorVersionNumber = minorVersionNumber;
	}

	/**
	 * @param buildNumber
	 *            the buildNumber to set
	 */
	public void setBuildNumber(int buildNumber) {
		this.buildNumber = buildNumber;
	}

	/**
	 * @param expirationDate
	 *            the expirationDate to set
	 */
	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	/**
	 * @param versionDate
	 *            the versionDate to set
	 */
	public void setVersionDate(Date versionDate) {
		this.versionDate = versionDate;
	}

}
