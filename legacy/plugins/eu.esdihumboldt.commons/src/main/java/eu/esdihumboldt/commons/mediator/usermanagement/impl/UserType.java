/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.1.2.1</a>, using an XML
 * Schema.
 * $Id: UserType.java,v 1.6 2007-11-19 10:49:38 pitaeva Exp $
 */

package eu.esdihumboldt.commons.mediator.usermanagement.impl;

import java.util.List;
import java.util.UUID;

import eu.esdihumboldt.commons.mediator.context.impl.UserContextImpl;
import eu.esdihumboldt.specification.mediator.context.Context;
import eu.esdihumboldt.specification.mediator.usermanagement.PersonProfile;
import eu.esdihumboldt.specification.mediator.usermanagement.User;
import eu.esdihumboldt.specification.mediator.usermanagement.ValidatyDates;

/**
 * This Type contains all elements required to specify the HUMBOLDT User.
 * 
 * 
 * @version $Revision: 1.6 $ $Date: 2007-11-19 10:49:38 $
 */
public abstract class UserType implements java.io.Serializable, User {

	// --------------------------/
	// - Class/Member Variables -/
	// --------------------------/

	/** database primary key */
	private long id;

	// private OrganizationImpl organization;

	/**
	 * unique user identifier in the HUMBOLDT-SYSTEM.
	 */
	private UUID userID;

	/**
	 * Field _contextList.
	 */
	private java.util.List<Context> contextList;

	/**
	 * Field _internalRole.
	 */
	private InternalRoleImpl internalRole;

	/**
	 * Field _organizationId.
	 */
	private UUID organizationID;

	/**
	 * Field _personProfile.
	 */
	private PersonProfile personProfile;

	/**
	 * position of the user in the organization userList.
	 */

	private int position;

	// ----------------/
	// - Constructors -/
	// ----------------/

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public UserType() {
		super();
		this.contextList = new java.util.ArrayList();
	}

	// -----------/
	// - Methods -/
	// -----------/

	/**
	 * 
	 * 
	 * @param vContext
	 * @throws java.lang.IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection
	 */
	public void addContext(final Context vContext)
			throws java.lang.IndexOutOfBoundsException {
		((UserContextImpl) vContext).setUser(this);
		this.contextList.add(vContext);
	}

	/**
	 * Method enumerateContext.
	 * 
	 * @return an Enumeration over all possible elements of this collection
	 */
	public java.util.Enumeration enumerateContext() {
		return java.util.Collections.enumeration(this.contextList);
	}

	/**
	 * Method getContext.
	 * 
	 * @param index
	 * @throws java.lang.IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection
	 * @return the value of the Context at the given index
	 */
	public Context getContext(final int index)
			throws java.lang.IndexOutOfBoundsException {
		// check bounds for index
		if (index < 0 || index >= this.contextList.size()) {
			throw new IndexOutOfBoundsException("getContext: Index value '"
					+ index + "' not in range [0.."
					+ (this.contextList.size() - 1) + "]");
		}

		return (Context) contextList.get(index);
	}

	/**
	 * Method getContext.Returns the contents of the collection in an Array.
	 * <p>
	 * Note: Just in case the collection contents are changing in another
	 * thread, we pass a 0-length Array of the correct type into the API call.
	 * This way we <i>know</i> that the Array returned is of exactly the correct
	 * length.
	 * 
	 * @return this collection as an Array
	 */
	public List<Context> getContextList() {
		Context[] array = new Context[0];
		return this.contextList;
	}

	/**
	 * Method getContextCount.
	 * 
	 * @return the size of this collection
	 */
	public int getContextCount() {
		return this.contextList.size();
	}

	/**
	 * Returns the value of field 'contextId'.
	 * 
	 * @return the value of field 'ContextId'.
	 */
	public UUID getUserID() {
		return this.userID;
	}

	/**
	 * Returns the value of field 'internalRole'.
	 * 
	 * @return the value of field 'InternalRole'.
	 */
	public InternalRoleImpl getInternalRole() {
		return this.internalRole;
	}

	/**
	 * Returns the value of field 'organizationId'.
	 * 
	 * @return the value of field 'OrganizationId'.
	 */
	public UUID getOrganizationID() {
		return this.organizationID;
	}

	/**
	 * Returns the value of field 'personProfile'.
	 * 
	 * @return the value of field 'PersonProfile'.
	 */
	public PersonProfile getPersonProfile() {
		return this.personProfile;
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
	 * Method iterateContext.
	 * 
	 * @return an Iterator over all possible elements in this collection
	 */
	public java.util.Iterator iterateContext() {
		return this.contextList.iterator();
	}

	/**
     */
	public void removeAllContext() {
		this.contextList.clear();
	}

	/**
	 * Method removeContext.
	 * 
	 * @param vContext
	 * @return true if the object was removed from the collection.
	 */
	public boolean removeContext(final Context vContext) {
		boolean removed = contextList.remove(vContext);
		return removed;
	}

	/**
	 * Method removeContextAt.
	 * 
	 * @param index
	 * @return the element removed from the collection
	 */
	public Context removeContextAt(final int index) {
		java.lang.Object obj = this.contextList.remove(index);
		return (Context) obj;
	}

	/**
	 * 
	 * 
	 * @param index
	 * @param vContext
	 * @throws java.lang.IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection
	 */
	public void setContext(final int index, final Context vContext)
			throws java.lang.IndexOutOfBoundsException {
		// check bounds for index
		if (index < 0 || index >= this.contextList.size()) {
			throw new IndexOutOfBoundsException("setContext: Index value '"
					+ index + "' not in range [0.."
					+ (this.contextList.size() - 1) + "]");
		}

		this.contextList.set(index, vContext);
	}

	/**
	 * 
	 * 
	 * @param vContextArray
	 */
	public void setContextList(final List<Context> vContextList) {
		this.contextList = vContextList;

	}

	/**
	 * Sets the value of field 'contextId'.
	 * 
	 * @param contextId
	 *            the value of field 'contextId'.
	 */
	public void setUserID(UUID uuid) {
		this.userID = uuid;
	}

	/**
	 * Sets the value of field 'internalRole'.
	 * 
	 * @param internalRole
	 *            the value of field 'internalRole'.
	 */
	public void setInternalRole(final InternalRoleImpl internalRole) {
		this.internalRole = internalRole;
	}

	/**
	 * Sets the value of field 'organizationId'.
	 * 
	 * @param organizationId
	 *            the value of field 'organizationId'.
	 */
	public void setOrganizationID(final UUID organizationId) {
		this.organizationID = organizationId;
	}

	/**
	 * Sets the value of field 'personProfile'.
	 * 
	 * @param personProfile
	 *            the value of field 'personProfile'.
	 */
	public void setPersonProfile(final PersonProfile personProfile) {
		this.personProfile = personProfile;
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

	public ValidatyDates getValidityDates() {
		// TODO Auto-generated method stub
		return null;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
