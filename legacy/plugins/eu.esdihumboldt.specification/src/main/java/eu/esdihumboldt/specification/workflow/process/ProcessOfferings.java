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

import java.util.List;

import eu.esdihumboldt.specification.annotations.spec.ReferenceSpecification;

/**
 * This interface is used to provide metadata on each of the processes offered
 * by the service.
 * 
 * @author mgone
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
@ReferenceSpecification("OGC 05-007r7:1.0.0 8.3.1")
public interface ProcessOfferings {

	/**
	 * 
	 * @return Unordered list of brief description of processes offered by this
	 *         WPS server
	 */
	public List<ProcessBrief> getProcessBrief();
}
