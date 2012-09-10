/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.1.2.1</a>, using an XML
 * Schema.
 * $Id: OrganizationType.java,v 1.7 2007-11-19 10:48:58 pitaeva Exp $
 */

package eu.esdihumboldt.commons.mediator.usermanagement.impl;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import eu.esdihumboldt.commons.mediator.context.impl.OrganizationContextImpl;
import eu.esdihumboldt.specification.mediator.context.Context;
import eu.esdihumboldt.specification.mediator.usermanagement.Organization;
import eu.esdihumboldt.specification.mediator.usermanagement.OrganizationAddress;
import eu.esdihumboldt.specification.mediator.usermanagement.User;
import eu.esdihumboldt.specification.mediator.usermanagement.ValidatyDates;

/**
 * Class OrganizationType.
 * 
 * @version $Revision: 1.7 $ $Date: 2007-11-19 10:48:58 $
 */
public abstract class OrganizationType implements java.io.Serializable,
		Organization {

	// --------------------------/
	// - Class/Member Variables -/
	// --------------------------/

	/**
	 * primary key in the database
	 * 
	 */

	private long id;

	/**
	 * Field _organizationId.
	 */
	private UUID organizationID;

	/**
	 * Field _organizationDisplayName.
	 */
	private java.lang.String organizationDisplayName;

	/**
	 * Field _nameLine1.
	 */
	private java.lang.String nameLine1;

	/**
	 * Field _nameLine2.
	 */
	private java.lang.String nameLine2;

	/**
	 * Field _nameLine3.
	 */
	private java.lang.String nameLine3;

	/**
	 * Field _language.
	 */
	private String language;

	/**
	 * Field _generalNotes.
	 */
	private java.lang.String generalNotes;

	/**
	 * Field _userList.
	 */
	private java.util.List<User> userList;

	/**
	 * Field _organizationAddressList.
	 */
	private java.util.List<OrganizationAddress> organizationAddressList;

	/**
	 * Field _contextList.
	 */
	private java.util.List<Context> contextList;

	// ----------------/
	// - Constructors -/
	// ----------------/

	public OrganizationType() {
		super();
		this.userList = new java.util.ArrayList();
		this.organizationAddressList = new java.util.ArrayList();
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
		((OrganizationContextImpl) vContext).setOrganization(this);
		this.contextList.add(vContext);
	}

	/**
	 * 0
	 * 
	 * @param vOrganizationAddress
	 * @throws java.lang.IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection
	 */
	public void addOrganizationAddress(
			final OrganizationAddress vOrganizationAddress)
			throws java.lang.IndexOutOfBoundsException {
		this.organizationAddressList.add(vOrganizationAddress);
	}

	/**
	 * 
	 * 
	 * @param vUser
	 * @throws java.lang.IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection
	 */
	public void addUser(final User vUser)
			throws java.lang.IndexOutOfBoundsException {
		// set the Organisation id into user
		((UserImpl) vUser).setOrganizationID(this.organizationID);
		this.userList.add(vUser);
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
	 * Method enumerateOrganizationAddress.
	 * 
	 * @return an Enumeration over all possible elements of this collection
	 */
	public java.util.Enumeration enumerateOrganizationAddress() {
		return java.util.Collections.enumeration(this.organizationAddressList);
	}

	/**
	 * Method enumerateUser.
	 * 
	 * @return an Enumeration over all possible elements of this collection
	 */
	public java.util.Enumeration enumerateUser() {
		return java.util.Collections.enumeration(this.userList);
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
	 * Returns the value of field 'generalNotes'.
	 * 
	 * @return the value of field 'GeneralNotes'.
	 */
	public java.lang.String getGeneralNotes() {
		return this.generalNotes;
	}

	/**
	 * Returns the value of field 'language'.
	 * 
	 * @return the value of field 'Language'.
	 */
	public String getLanguage() {
		return this.language;
	}

	/**
	 * Returns the value of field 'nameLine1'.
	 * 
	 * @return the value of field 'NameLine1'.
	 */
	public java.lang.String getNameLine1() {
		return this.nameLine1;
	}

	/**
	 * Returns the value of field 'nameLine2'.
	 * 
	 * @return the value of field 'NameLine2'.
	 */
	public java.lang.String getNameLine2() {
		return this.nameLine2;
	}

	/**
	 * Returns the value of field 'nameLine3'.
	 * 
	 * @return the value of field 'NameLine3'.
	 */
	public java.lang.String getNameLine3() {
		return this.nameLine3;
	}

	/**
	 * Method getOrganizationAddress.
	 * 
	 * @param index
	 * @throws java.lang.IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection
	 * @return the value of the OrganizationAddress at the given index
	 */
	public OrganizationAddress getOrganizationAddress(final int index)
			throws java.lang.IndexOutOfBoundsException {
		// check bounds for index
		if (index < 0 || index >= this.organizationAddressList.size()) {
			throw new IndexOutOfBoundsException(
					"getOrganizationAddress: Index value '" + index
							+ "' not in range [0.."
							+ (this.organizationAddressList.size() - 1) + "]");
		}

		return (OrganizationAddress) organizationAddressList.get(index);
	}

	/**
	 * Method getOrganizationAddress.Returns the contents of the collection in
	 * an Array.
	 * <p>
	 * Note: Just in case the collection contents are changing in another
	 * thread, we pass a 0-length Array of the correct type into the API call.
	 * This way we <i>know</i> that the Array returned is of exactly the correct
	 * length.
	 * 
	 * @return this collection as an Array
	 */
	public List<OrganizationAddress> getOrganizationAddressList() {

		return this.organizationAddressList;
	}

	/**
	 * Method getOrganizationAddressCount.
	 * 
	 * @return the size of this collection
	 */
	public int getOrganizationAddressCount() {
		return this.organizationAddressList.size();
	}

	/**
	 * Returns the value of field 'organizationDisplayName'.
	 * 
	 * @return the value of field 'OrganizationDisplayName'.
	 */
	public java.lang.String getOrganizationDisplayName() {
		return this.organizationDisplayName;
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
	 * Method getUser.
	 * 
	 * @param index
	 * @throws java.lang.IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection
	 * @return the value of the User at the given index
	 */
	public User getUser(final int index)
			throws java.lang.IndexOutOfBoundsException {
		// check bounds for index
		if (index < 0 || index >= this.userList.size()) {
			throw new IndexOutOfBoundsException("getUser: Index value '"
					+ index + "' not in range [0.."
					+ (this.userList.size() - 1) + "]");
		}

		return (User) userList.get(index);
	}

	/**
	 * Method getUser.Returns the contents of the collection in an Array.
	 * <p>
	 * Note: Just in case the collection contents are changing in another
	 * thread, we pass a 0-length Array of the correct type into the API call.
	 * This way we <i>know</i> that the Array returned is of exactly the correct
	 * length.
	 * 
	 * @return this collection as an Array
	 */
	public List<User> getUserList() {

		return this.userList;
	}

	/**
	 * Method getUserCount.
	 * 
	 * @return the size of this collection
	 */
	public int getUserCount() {
		return this.userList.size();
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
	 * Method iterateOrganizationAddress.
	 * 
	 * @return an Iterator over all possible elements in this collection
	 */
	public java.util.Iterator iterateOrganizationAddress() {
		return this.organizationAddressList.iterator();
	}

	/**
	 * Method iterateUser.
	 * 
	 * @return an Iterator over all possible elements in this collection
	 */
	public java.util.Iterator iterateUser() {
		return this.userList.iterator();
	}

	/**
	 */
	public void removeAllContext() {
		this.contextList.clear();
	}

	/**
	 */
	public void removeAllOrganizationAddress() {
		this.organizationAddressList.clear();
	}

	/**
	 */
	public void removeAllUser() {
		this.userList.clear();
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
	 * Method removeOrganizationAddress.
	 * 
	 * @param vOrganizationAddress
	 * @return true if the object was removed from the collection.
	 */
	public boolean removeOrganizationAddress(
			final OrganizationAddress vOrganizationAddress) {
		boolean removed = organizationAddressList.remove(vOrganizationAddress);
		return removed;
	}

	/**
	 * Method removeOrganizationAddressAt.
	 * 
	 * @param index
	 * @return the element removed from the collection
	 */
	public OrganizationAddress removeOrganizationAddressAt(final int index) {
		java.lang.Object obj = this.organizationAddressList.remove(index);
		return (OrganizationAddress) obj;
	}

	/**
	 * Method removeUser.
	 * 
	 * @param vUser
	 * @return true if the object was removed from the collection.
	 */
	public boolean removeUser(final User vUser) {
		boolean removed = userList.remove(vUser);
		return removed;
	}

	/**
	 * Method removeUserAt.
	 * 
	 * @param index
	 * @return the element removed from the collection
	 */
	public User removeUserAt(final int index) {
		java.lang.Object obj = this.userList.remove(index);
		return (User) obj;
	}

	/**
	 * 
	 * 
	 * @param vContextArray
	 */
	@SuppressWarnings("unused")
	private void setContextList(final List<Context> vContextList) {
		this.contextList = vContextList;
	}

	/**
	 * Sets the value of field 'generalNotes'.
	 * 
	 * @param generalNotes
	 *            the value of field 'generalNotes'.
	 */
	public void setGeneralNotes(final java.lang.String generalNotes) {
		this.generalNotes = generalNotes;
	}

	/**
	 * Sets the value of field 'language'.
	 * 
	 * @param language
	 *            the value of field 'language'.
	 */
	public void setLanguage(final String language) {
		this.language = language;
	}

	/**
	 * Sets the value of field 'nameLine1'.
	 * 
	 * @param nameLine1
	 *            the value of field 'nameLine1'.
	 */
	public void setNameLine1(final java.lang.String nameLine1) {
		this.nameLine1 = nameLine1;
	}

	/**
	 * Sets the value of field 'nameLine2'.
	 * 
	 * @param nameLine2
	 *            the value of field 'nameLine2'.
	 */
	public void setNameLine2(final java.lang.String nameLine2) {
		this.nameLine2 = nameLine2;
	}

	/**
	 * Sets the value of field 'nameLine3'.
	 * 
	 * @param nameLine3
	 *            the value of field 'nameLine3'.
	 */
	public void setNameLine3(final java.lang.String nameLine3) {
		this.nameLine3 = nameLine3;
	}

	/**
	 * 
	 * 
	 * @param vOrganizationAddressArray
	 */
	private void setOrganizationAddressList(
			final List<OrganizationAddress> vOrganizationAddressList) {
		this.organizationAddressList = vOrganizationAddressList;
	}

	/**
	 * Sets the value of field 'organizationDisplayName'.
	 * 
	 * @param organizationDisplayName
	 *            the value of field 'organizationDisplayName'.
	 */
	public void setOrganizationDisplayName(
			final java.lang.String organizationDisplayName) {
		this.organizationDisplayName = organizationDisplayName;
	}

	/**
	 * Sets the value of field 'organizationId'.
	 * 
	 * @param organizationId
	 *            the value of field 'organizationId'.
	 */
	public void setOrganizationID(final java.util.UUID organizationId) {
		this.organizationID = organizationId;
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

	public Set<BankDetail> getBankDetail() {
		// TODO Auto-generated method stub
		return null;
	}

	public Currency getCurrency() {
		// TODO Auto-generated method stub
		return null;
	}

	public ValidatyDates getValidatyDates() {
		// TODO Auto-generated method stub
		return null;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Injector for hibbernate
	 * 
	 * @param injectedUserList
	 */
	@SuppressWarnings("unused")
	private void setUserList(List<User> injectedUserList) {
		userList = injectedUserList;
	}

}
