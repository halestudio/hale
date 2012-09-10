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
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.opengis.metadata.Identifier;

import eu.esdihumboldt.commons.mediator.contextservice.hibernate.helpers.IdentifierHelper;
import eu.esdihumboldt.specification.modelrepository.abstractfc.AccessConstraint;

/**
 * Explanation of this Type.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id: DefaultAccessConstraint.java,v 1.4 2007-12-06 13:20:24 pitaeva
 *          Exp $
 */
public class DefaultAccessConstraint implements AccessConstraint, Serializable {

	// Fields ..................................................................

	private long id;
	private UUID uid;

	private boolean hasAcessRightRead;
	private boolean hasAcessRightWrite;
	private boolean hasAcessRightUse;
	private boolean hasAcessRightDelete;

	private Identifier partyIdentifier;
	private Set<IdentifierHelper> dbPartyIdentifier;

	private String usageInformation;

	// Constructors ............................................................

	/**
	 * Protected no-args Constructor for Hibernate etc.
	 */
	public DefaultAccessConstraint() {
		super();
		this.uid = UUID.randomUUID();
	}

	/**
	 * Default Constructor.
	 * 
	 * @param hasAcessRightRead
	 * @param hasAcessRightWrite
	 * @param hasAcessRightUse
	 * @param hasAcessRightDelete
	 * @param partyIdentifier
	 *            if null, the constraint will be in effect globally.
	 */
	public DefaultAccessConstraint(boolean hasAcessRightRead,
			boolean hasAcessRightWrite, boolean hasAcessRightUse,
			boolean hasAcessRightDelete, Identifier partyIdentifier) {
		this();
		this.hasAcessRightRead = hasAcessRightRead;
		this.hasAcessRightWrite = hasAcessRightWrite;
		this.hasAcessRightUse = hasAcessRightUse;
		this.hasAcessRightDelete = hasAcessRightDelete;
		this.partyIdentifier = partyIdentifier;
	}

	// AccessConstraint Operations .............................................

	/**
	 * @see eu.esdihumboldt.specification.modelrepository.abstractfc.AccessConstraint#getAccessRightTypes()
	 */
	public Set<AccessRightType> getAccessRightTypes() {
		Set<AccessRightType> result = new HashSet<AccessRightType>();
		if (this.hasAcessRightRead) {
			result.add(AccessRightType.Read);
		}
		if (this.hasAcessRightWrite) {
			result.add(AccessRightType.Write);
		}
		if (this.hasAcessRightUse) {
			result.add(AccessRightType.Use);
		}
		if (this.hasAcessRightDelete) {
			result.add(AccessRightType.Delete);
		}
		return result;
	}

	/**
	 * @see eu.esdihumboldt.specification.modelrepository.abstractfc.AccessConstraint#getPartyIdentifier()
	 */
	public Identifier getPartyIdentifier() {
		return this.partyIdentifier;
	}

	/**
	 * @see eu.esdihumboldt.specification.modelrepository.abstractfc.AccessConstraint#getUsageInformation()
	 */
	public String getUsageInformation() {
		return this.usageInformation;
	}

	// DefaultAccessConstraint Operations ......................................

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
	 * @return the hasAcessRightRead
	 */
	public boolean isHasAcessRightRead() {
		return hasAcessRightRead;
	}

	/**
	 * @param hasAcessRightRead
	 *            the hasAcessRightRead to set
	 */
	public void setHasAcessRightRead(boolean hasAcessRightRead) {
		this.hasAcessRightRead = hasAcessRightRead;
	}

	/**
	 * @return the hasAcessRightWrite
	 */
	public boolean isHasAcessRightWrite() {
		return hasAcessRightWrite;
	}

	/**
	 * @param hasAcessRightWrite
	 *            the hasAcessRightWrite to set
	 */
	public void setHasAcessRightWrite(boolean hasAcessRightWrite) {
		this.hasAcessRightWrite = hasAcessRightWrite;
	}

	/**
	 * @return the hasAcessRightUse
	 */
	public boolean isHasAcessRightUse() {
		return hasAcessRightUse;
	}

	/**
	 * @param hasAcessRightUse
	 *            the hasAcessRightUse to set
	 */
	public void setHasAcessRightUse(boolean hasAcessRightUse) {
		this.hasAcessRightUse = hasAcessRightUse;
	}

	/**
	 * @return the hasAcessRightDelete
	 */
	public boolean isHasAcessRightDelete() {
		return hasAcessRightDelete;
	}

	/**
	 * @param hasAcessRightDelete
	 *            the hasAcessRightDelete to set
	 */
	public void setHasAcessRightDelete(boolean hasAcessRightDelete) {
		this.hasAcessRightDelete = hasAcessRightDelete;
	}

	/**
	 * @param uid
	 *            the uid to set
	 */
	private void setUid(UUID uid) {
		this.uid = uid;
	}

	/**
	 * @param partyIdentifier
	 *            the partyIdentifier to set
	 */
	public void setPartyIdentifier(Identifier partyIdentifier) {
		this.partyIdentifier = partyIdentifier;
	}

	/**
	 * @param usageInformation
	 *            the usageInformation to set
	 */
	public void setUsageInformation(String usageInformation) {
		this.usageInformation = usageInformation;
	}

	/**
	 * @return the dbPartyIdentifier
	 */
	public Set<IdentifierHelper> getDbPartyIdentifier() {
		return dbPartyIdentifier;
	}

	/**
	 * @param dbPartyIdentifier
	 *            the dbPartyIdentifier to set
	 */
	public void setDbPartyIdentifier(Set<IdentifierHelper> dbPartyIdentifier) {
		this.dbPartyIdentifier = dbPartyIdentifier;
	}

}
