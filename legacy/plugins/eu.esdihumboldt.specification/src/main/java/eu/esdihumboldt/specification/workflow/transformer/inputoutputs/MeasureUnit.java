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
package eu.esdihumboldt.specification.workflow.transformer.inputoutputs;

import java.io.Serializable;

/**
 * This interface defines the Units of measure supported for an input or output
 * of a process. A specific input or output of a WPS instance will always have
 * just one measure type (length, area, speed, weight etc)
 * 
 * @author Moses Gone
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */

public interface MeasureUnit extends Serializable {

	/**
	 * Reference to default unit of measure
	 * 
	 * @return A unit of measure as a string eg KM, M or Miles
	 */
	public String getUnitOfMeasure();

	/**
	 * Sets the Unit of measure of this numerical output (or input)
	 * 
	 * @param uom
	 *            The unit of meaure
	 */
	public void setUnitOfMeasure(String uom);

}
