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

import eu.esdihumboldt.specification.annotations.spec.ReferenceSpecification;
import eu.esdihumboldt.specification.workflow.repository.Transformer.ProcessType;

/**
 * Brief description of process, not including input and output parameters In
 * this specification, some of the optional methods such as metadata and
 * processVersion have been deliberately left out
 * 
 * @author M. Gone
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
@ReferenceSpecification("OGC 05-007r6:1.0.0 7.2")
public interface ProcessBrief extends Description {
	/**
	 * This method is used to retrieve the type of the transformer
	 * 
	 * @return
	 */
	public ProcessType getTransformerType();
}
