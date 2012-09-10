/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.specification.dataaccess.abstractionmodel;

import java.util.UUID;

/**
 * This is the superinterface for all objects in the Data Abstraction Model.
 * Currently it only defines that each one needs a unique identifier.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface DAMObject {

	/**
	 * @return the {@link UUID} of this {@link DAMObject}.
	 */
	public UUID getIdentifier();

}
