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

package eu.esdihumboldt.hale.io.oml.internal.model.rdf;

import java.util.UUID;

import eu.esdihumboldt.hale.io.oml.internal.model.align.IEntity;

/**
 * A {@link IAbout} contains the identifier for OML objects such as
 * {@link IEntity}s.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
@SuppressWarnings("javadoc")
public interface IAbout {

	/**
	 * @return the uid
	 */
	public UUID getUid();

	public String getAbout();
}
