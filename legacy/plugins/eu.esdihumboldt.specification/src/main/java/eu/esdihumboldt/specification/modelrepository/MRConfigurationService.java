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
package eu.esdihumboldt.specification.modelrepository;

import java.net.URL;
import java.util.Map;
import java.util.Set;

/**
 * This interface allows to define the configuration of a Model Repository node.
 * FIXME: The operations will be massively extended in the upcoming versions.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface MRConfigurationService {

	/**
	 * Retrieve the rights a user can have, as a map of types and rights.
	 * 
	 * @param username
	 *            the username for which to retrieve the access rights.
	 * @return a Map of ObjectTypes and Sets of associated ObjectRights.
	 */
	public Map<ObjectType, Set<ObjectRight>> getTypeUserRights(String username);

	/**
	 * Retrieve the rights a user has to specific resources stored in the local
	 * node.
	 * 
	 * @param username
	 *            the username for which to retrieve the access rights.
	 * @return a Map of {@link URL} object identifiers and {@link Set}s of
	 *         associated {@link ObjectRight}s.
	 */
	public Map<URL, Set<ObjectRight>> getObjectUserRights(String username);

	/**
	 * The types of objects stored in the repository.
	 * 
	 * @author Thorsten Reitz
	 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
	 * @version $Id$
	 */
	public enum ObjectType {
		/** A Mapping. */
		mapping,
		/** A Schema (ConceptualSchema). */
		schema
	}

	/**
	 * The types of rights a user can have in terms of an object type.
	 * 
	 * @author Thorsten Reitz
	 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
	 * @version $Id$
	 */
	public enum ObjectRight {
		/** read object from repository */
		read,
		/** write and update objects to the repository */
		write,
		/** deprecate an object in the repository */
		deprecate,
		/** publish an object for local usage */
		publish_locally,
		/** publish an object for global usage */
		publish_globally
	}

}
