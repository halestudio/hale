/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.1.2.1</a>, using an XML
 * Schema.
 * $Id: UserAddressType.java,v 1.3 2007-10-24 13:42:53 pitaeva Exp $
 */

package eu.esdihumboldt.commons.mediator.usermanagement.impl;

import java.util.List;

import eu.esdihumboldt.specification.mediator.usermanagement.UserAddress;

/**
 * It describes the user-specific address information.
 * 
 * 
 * @version $Revision: 1.3 $ $Date: 2007-10-24 13:42:53 $
 */
public abstract class UserAddressType extends
		eu.esdihumboldt.commons.mediator.usermanagement.impl.AddressType
		implements java.io.Serializable, UserAddress {

	// --------------------------/
	// - Class/Member Variables -/
	// --------------------------/

	private long id;

	/**
	 * Field _building.
	 */
	private java.lang.String building;

	/**
	 * Field _floor.
	 */
	private java.lang.String floor;

	/**
	 * Field _roomNumber.
	 */
	private String roomNumber;

	/**
	 * keeps track of state for field: _roomNumber
	 */
	private boolean has_roomNumber;

	/**
	 * Field _inhousMail.
	 */
	private java.lang.String inhouseMail;

	/**
	 * Field _department.
	 */
	private java.lang.String department;

	/**
	 * Field _communicationDetailsList.
	 */
	private java.util.List communicationDetailsList;

	// ----------------/
	// - Constructors -/
	// ----------------/

	public UserAddressType() {
		super();
		this.communicationDetailsList = new java.util.ArrayList();
	}

	// -----------/
	// - Methods -/
	// -----------/

	/**
	 * 
	 * 
	 * @param vCommunicationDetails
	 * @throws java.lang.IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection
	 */
	public void addCommunicationDetails(
			final eu.esdihumboldt.commons.mediator.usermanagement.impl.CommunicationDetailsImpl vCommunicationDetails)
			throws java.lang.IndexOutOfBoundsException {
		this.communicationDetailsList.add(vCommunicationDetails);
	}

	/**
	 * 
	 * 
	 * @param index
	 * @param vCommunicationDetails
	 * @throws java.lang.IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection
	 */
	public void addCommunicationDetails(
			final int index,
			final eu.esdihumboldt.commons.mediator.usermanagement.impl.CommunicationDetailsImpl vCommunicationDetails)
			throws java.lang.IndexOutOfBoundsException {
		this.communicationDetailsList.add(index, vCommunicationDetails);
	}

	/**
     */
	public void deleteRoomNumber() {
		this.has_roomNumber = false;
	}

	/**
	 * Method enumerateCommunicationDetails.
	 * 
	 * @return an Enumeration over all possible elements of this collection
	 */
	public java.util.Enumeration enumerateCommunicationDetails() {
		return java.util.Collections.enumeration(this.communicationDetailsList);
	}

	/**
	 * Returns the value of field 'building'.
	 * 
	 * @return the value of field 'Building'.
	 */
	public java.lang.String getBuilding() {
		return this.building;
	}

	/**
	 * Method getCommunicationDetails.
	 * 
	 * @param index
	 * @throws java.lang.IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection
	 * @return the value of the
	 *         eu.esdihumboldt.mediator.usermanagement.CommunicationDetails at
	 *         the given index
	 */
	public eu.esdihumboldt.commons.mediator.usermanagement.impl.CommunicationDetailsImpl getCommunicationDetails(
			final int index) throws java.lang.IndexOutOfBoundsException {
		// check bounds for index
		if (index < 0 || index >= this.communicationDetailsList.size()) {
			throw new IndexOutOfBoundsException(
					"getCommunicationDetails: Index value '" + index
							+ "' not in range [0.."
							+ (this.communicationDetailsList.size() - 1) + "]");
		}

		return (eu.esdihumboldt.commons.mediator.usermanagement.impl.CommunicationDetailsImpl) communicationDetailsList
				.get(index);
	}

	/**
	 * Method getCommunicationDetails.Returns the contents of the collection in
	 * an Array.
	 * <p>
	 * Note: Just in case the collection contents are changing in another
	 * thread, we pass a 0-length Array of the correct type into the API call.
	 * This way we <i>know</i> that the Array returned is of exactly the correct
	 * length.
	 * 
	 * @return this collection as an Array
	 */
	public eu.esdihumboldt.commons.mediator.usermanagement.impl.CommunicationDetailsImpl[] getCommunicationDetails() {
		eu.esdihumboldt.commons.mediator.usermanagement.impl.CommunicationDetailsImpl[] array = new eu.esdihumboldt.commons.mediator.usermanagement.impl.CommunicationDetailsImpl[0];
		return (eu.esdihumboldt.commons.mediator.usermanagement.impl.CommunicationDetailsImpl[]) this.communicationDetailsList
				.toArray(array);
	}

	/**
	 * Method getCommunicationDetailsCount.
	 * 
	 * @return the size of this collection
	 */
	public int getCommunicationDetailsCount() {
		return this.communicationDetailsList.size();
	}

	/**
	 * Returns the value of field 'department'.
	 * 
	 * @return the value of field 'Department'.
	 */
	public java.lang.String getDepartment() {
		return this.department;
	}

	/**
	 * Returns the value of field 'floor'.
	 * 
	 * @return the value of field 'Floor'.
	 */
	public java.lang.String getFloor() {
		return this.floor;
	}

	/**
	 * Returns the value of field 'inhousMail'.
	 * 
	 * @return the value of field 'InhousMail'.
	 */
	public java.lang.String getInhouseMail() {
		return this.inhouseMail;
	}

	/**
	 * Returns the value of field 'roomNumber'.
	 * 
	 * @return the value of field 'RoomNumber'.
	 */
	public String getRoomNumber() {
		return this.roomNumber;
	}

	/**
	 * Method hasRoomNumber.
	 * 
	 * @return true if at least one RoomNumber has been added
	 */
	public boolean hasRoomNumber() {
		return this.has_roomNumber;
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
	 * Method iterateCommunicationDetails.
	 * 
	 * @return an Iterator over all possible elements in this collection
	 */
	public java.util.Iterator iterateCommunicationDetails() {
		return this.communicationDetailsList.iterator();
	}

	/**
     */
	public void removeAllCommunicationDetails() {
		this.communicationDetailsList.clear();
	}

	/**
	 * Method removeCommunicationDetails.
	 * 
	 * @param vCommunicationDetails
	 * @return true if the object was removed from the collection.
	 */
	public boolean removeCommunicationDetails(
			final eu.esdihumboldt.commons.mediator.usermanagement.impl.CommunicationDetailsImpl vCommunicationDetails) {
		boolean removed = communicationDetailsList
				.remove(vCommunicationDetails);
		return removed;
	}

	/**
	 * Method removeCommunicationDetailsAt.
	 * 
	 * @param index
	 * @return the element removed from the collection
	 */
	public eu.esdihumboldt.commons.mediator.usermanagement.impl.CommunicationDetailsImpl removeCommunicationDetailsAt(
			final int index) {
		java.lang.Object obj = this.communicationDetailsList.remove(index);
		return (eu.esdihumboldt.commons.mediator.usermanagement.impl.CommunicationDetailsImpl) obj;
	}

	/**
	 * Sets the value of field 'building'.
	 * 
	 * @param building
	 *            the value of field 'building'.
	 */
	public void setBuilding(final java.lang.String building) {
		this.building = building;
	}

	/**
	 * 
	 * 
	 * @param index
	 * @param vCommunicationDetails
	 * @throws java.lang.IndexOutOfBoundsException
	 *             if the index given is outside the bounds of the collection
	 */
	public void setCommunicationDetails(
			final int index,
			final eu.esdihumboldt.commons.mediator.usermanagement.impl.CommunicationDetailsImpl vCommunicationDetails)
			throws java.lang.IndexOutOfBoundsException {
		// check bounds for index
		if (index < 0 || index >= this.communicationDetailsList.size()) {
			throw new IndexOutOfBoundsException(
					"setCommunicationDetails: Index value '" + index
							+ "' not in range [0.."
							+ (this.communicationDetailsList.size() - 1) + "]");
		}

		this.communicationDetailsList.set(index, vCommunicationDetails);
	}

	/**
	 * 
	 * 
	 * @param vCommunicationDetailsArray
	 */
	public void setCommunicationDetails(
			final eu.esdihumboldt.commons.mediator.usermanagement.impl.CommunicationDetailsImpl[] vCommunicationDetailsArray) {
		// -- copy array
		communicationDetailsList.clear();

		for (int i = 0; i < vCommunicationDetailsArray.length; i++) {
			this.communicationDetailsList.add(vCommunicationDetailsArray[i]);
		}
	}

	/**
	 * Sets the value of field 'department'.
	 * 
	 * @param department
	 *            the value of field 'department'.
	 */
	public void setDepartment(final java.lang.String department) {
		this.department = department;
	}

	/**
	 * Sets the value of field 'floor'.
	 * 
	 * @param floor
	 *            the value of field 'floor'.
	 */
	public void setFloor(final java.lang.String floor) {
		this.floor = floor;
	}

	/**
	 * Sets the value of field 'inhousMail'.
	 * 
	 * @param inhousMail
	 *            the value of field 'inhousMail'.
	 */
	public void setInhouseMail(final java.lang.String inhouseMail) {
		this.inhouseMail = inhouseMail;
	}

	/**
	 * Sets the value of field 'roomNumber'.
	 * 
	 * @param roomNumber
	 *            the value of field 'roomNumber'.
	 */
	public void setRoomNumber(final String roomNumber) {
		this.roomNumber = roomNumber;
		this.has_roomNumber = true;
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

	@Deprecated
	public List<CommunicationDetail> getCommunicationDetail() {
		// TODO Auto-generated method stub
		return null;
	}

	@Deprecated
	public CountryCode getCountry() {
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
