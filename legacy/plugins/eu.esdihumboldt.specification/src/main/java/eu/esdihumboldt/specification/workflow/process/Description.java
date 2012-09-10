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
package eu.esdihumboldt.specification.workflow.process;

import java.io.Serializable;

import eu.esdihumboldt.specification.annotations.spec.ReferenceSpecification;

/**
 * This is a super interface for describing processes
 * 
 * @author Moses Gone
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
@ReferenceSpecification("OGC 05-007r6:1.0.0 7.2")
public interface Description extends Serializable {

	/**
	 * Title of the process, inputs, or outputs, unique for this server
	 * 
	 * @return
	 */
	public String getTitle();

	/**
	 * Brief narrative description of a process, input, or output, normally
	 * available for display to human
	 * 
	 * @return
	 */
	public String getAbstract();

}
