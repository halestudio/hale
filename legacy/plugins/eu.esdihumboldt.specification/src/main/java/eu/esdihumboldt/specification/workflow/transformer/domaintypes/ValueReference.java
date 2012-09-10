/*
 * HUMBOLDT: A Framework for Data Harmonistation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this website:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to : http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.specification.workflow.transformer.domaintypes;

import java.net.URI;

import eu.esdihumboldt.specification.annotations.spec.ReferenceSpecification;

/**
 * Reference to a listof all valid values and/or ranges of values of values for
 * this quantitity
 * 
 * @author Moses Gone
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
@ReferenceSpecification("OGC 05-007r7:1.0.0 9.3.1")
public interface ValueReference extends LiteralValuesChoice {

	/**
	 * Human readable name of domain metadata described by associated referenced
	 * document
	 * 
	 * @return String
	 */
	public String getName();

	/**
	 * Reference to metadata about this domain
	 * 
	 * @return URI
	 */
	public URI getReference();

}
