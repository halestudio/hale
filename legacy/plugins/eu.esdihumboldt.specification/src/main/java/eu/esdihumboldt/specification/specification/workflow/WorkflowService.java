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

package eu.esdihumboldt.specification.specification.workflow;

import eu.esdihumboldt.specification.workflow.repository.RepositoryManager;

/**
 * The workflow service is a super interface that provides methods for
 * generation management and supply of workflows and transformers using the
 * WorkflowGenerator and repository manager interfaces.
 * 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 * @author Moses Gone
 */
public interface WorkflowService extends WorkflowGenerator, RepositoryManager {

}
