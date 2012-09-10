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

import java.net.URI;

import eu.esdihumboldt.specification.annotations.spec.ReferenceSpecification;

/**
 * This interface describe complex data structure used to represent an input or
 * output
 * 
 * @author Moses Gone
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
@ReferenceSpecification("OGC 05-007r6:1.0.0 9.3")
public interface SupportedComplexData {

	/**
	 * Reference to default Format for process input or output
	 * 
	 * @return
	 */
	public String getDefaultFormat();

	/**
	 * Reference to default encoding for process input or output
	 * 
	 * @return
	 */
	public URI getDefaultEncoding();

	/**
	 * reference to default XML Schema Document for process input or output
	 * 
	 * @return
	 */
	public URI getDefaultSchema();

	/**
	 * Combination of format, encoding, and/or schema supported by process input
	 * or output.
	 * 
	 * @return a Complex Data
	 */
	public InputOutput getComplexData();
}
