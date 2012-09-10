/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the project web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.specification.modelrepository.abstractfc;

import java.util.Set;

import org.opengis.metadata.Identifier;

/**
 * An AccessConstraint explains what access right a party has. A party in this
 * context can be either an individual or a organization or everybody.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface AccessConstraint {

	/**
	 * @return the Identifier for the Party that has the AccessConstraint. If
	 *         this value is null, the {@link AccessConstraint} is valid
	 *         globally.
	 */
	public Identifier getPartyIdentifier();

	/**
	 * @return a Set of AccessRightTypes that the party has to the object to
	 *         which this AccessConstraint is bound.
	 */
	public Set<AccessRightType> getAccessRightTypes();

	/**
	 * @return a String with information on the License or Limitations applying
	 *         to the usage of the object to which this AccessConstraint is
	 *         bound.
	 */
	public String getUsageInformation();

	/**
	 * This enumeration describes the Access Right Types currently required.
	 */
	public enum AccessRightType {
		/** The Party is allowed to read the model. */
		Read,
		/** The Party is allowed to use the model. */
		Use,
		/** The Party is allowed to write to the model. */
		Write,
		/** The Party is allowed to delete the model. */
		Delete
	}

}
