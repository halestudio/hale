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

import eu.esdihumboldt.specification.annotations.spec.ReferenceSpecification;

/**
 * An interface to represent a range of a numeric value
 * 
 * @author Moses Gone
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
@ReferenceSpecification("OGC 05-007r7:1.0.0 9.3.1")
public interface Range extends LiteralValuesChoice {

	/**
	 * 
	 * @return Minimum value of this range of this numeric parameter
	 */
	public String MinimumValue();

	/**
	 * 
	 * @return Maximum value of this range of this numeric parameter
	 */
	public String MaximumValue();

	/**
	 * 
	 * @return Regular distance or spacing between allowed values in this range
	 */
	public String Spacing();

	/**
	 * This specifies which of the minimum and maximum values are included in
	 * this range
	 * 
	 */
	public enum rangeClosure {

		/**
         *
         */
		closed,
		/**
         *
         */
		open,
		/**
         *
         */
		closed_open,
		/**
         *
         */
		open_closed;
	}
}
