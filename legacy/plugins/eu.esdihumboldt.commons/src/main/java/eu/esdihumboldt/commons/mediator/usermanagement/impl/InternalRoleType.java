/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.1.2.1</a>, using an XML
 * Schema.
 * $Id: InternalRoleType.java,v 1.3 2007-10-24 13:42:54 pitaeva Exp $
 */

package eu.esdihumboldt.commons.mediator.usermanagement.impl;

import java.util.UUID;

import eu.esdihumboldt.specification.mediator.usermanagement.Role;

/**
 * This Type represents the role that the actual user has within the HUMBOLDT
 * system, not his role to the outside world. It is therefore only of concern
 * when internal permissions are checked.
 * 
 * 
 * @version $Revision: 1.3 $ $Date: 2007-10-24 13:42:54 $
 */
public abstract class InternalRoleType implements java.io.Serializable, Role {

	// --------------------------/
	// - Class/Member Variables -/
	// --------------------------/

	private long id;

	/**
	 * Field _roleName.
	 */
	private java.lang.String roleName;

	/**
	 * Field _roleId.
	 */
	private UUID roleID;

	// ----------------/
	// - Constructors -/
	// ----------------/

	public InternalRoleType() {
		super();
	}

	// -----------/
	// - Methods -/
	// -----------/

	/**
	 * Returns the value of field 'roleId'.
	 * 
	 * @return the value of field 'RoleId'.
	 */
	public UUID getRoleID() {
		return this.roleID;
	}

	/**
	 * Returns the value of field 'roleName'.
	 * 
	 * @return the value of field 'RoleName'.
	 */
	public java.lang.String getRoleName() {
		return this.roleName;
	}

	/**
	 * Method isValid.
	 * 
	 * @return true if this object is valid according to the schema
	 */
	public boolean isValid() {
		try {
			validate();
		} catch (org.exolab.castor.xml.ValidationException vex) {
			return false;
		}
		return true;
	}

	/**
	 * Sets the value of field 'roleId'.
	 * 
	 * @param roleId
	 *            the value of field 'roleId'.
	 */
	public void setRoleID(final java.util.UUID roleId) {
		this.roleID = roleId;
	}

	/**
	 * Sets the value of field 'roleName'.
	 * 
	 * @param roleName
	 *            the value of field 'roleName'.
	 */
	public void setRoleName(final java.lang.String roleName) {
		this.roleName = roleName;
	}

	/**
	 * 
	 * 
	 * @throws org.exolab.castor.xml.ValidationException
	 *             if this object is an invalid instance according to the schema
	 */
	public void validate() throws org.exolab.castor.xml.ValidationException {
		org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
		validator.validate(this);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}
