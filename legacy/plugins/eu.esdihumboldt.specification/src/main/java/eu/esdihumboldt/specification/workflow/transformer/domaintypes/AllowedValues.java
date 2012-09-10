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

import java.util.Set;

import eu.esdihumboldt.specification.annotations.spec.ReferenceSpecification;

/**
 * This interface defines the finite set of values and ranges allowed for the
 * input and contains ordered list of all valid values and or ranges
 * 
 * @author Moses Gone
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
@ReferenceSpecification("OGC 05-007r6:1.0.0 9.3.1")
public interface AllowedValues extends LiteralValuesChoice {

	/**
	 * Retrieves the allowed values of a given parameter quantity. This can be
	 * any value- range, single any value
	 * 
	 * @return A set of allowed Values of this quantity
	 */
	public Set<LiteralValuesChoice> getValues();

}
