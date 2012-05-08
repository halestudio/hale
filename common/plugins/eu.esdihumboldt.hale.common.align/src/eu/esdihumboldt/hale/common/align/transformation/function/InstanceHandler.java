/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.common.align.transformation.function;

import java.util.Map;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;

/**
 * Partitions multiple instances.
 * 
 * @param <E> the transformation engine type
 * @author Kai Schwierczek
 */
public interface InstanceHandler<E extends TransformationEngine> {
	/**
	 * Partition the given instances.
	 * 
	 * @param instances the instances
	 * @param transformationIdentifier the transformation function identifier
	 * @param engine the transformation engine that may be used for the
	 *   function execution
	 * @param transformationParameters the transformation parameters,
	 *   may be <code>null</code> 
	 * @param executionParameters additional parameters for the execution, 
	 *   may be <code>null</code>
	 * @param log the transformation log to report any information about the
	 *   execution of the transformation to
	 * @return a resource iterator over partitioned instances instances
	 * @throws TransformationException if an unrecoverable error occurs during
	 *   transformation
	 */
	public ResourceIterator<FamilyInstance> partitionInstances(InstanceCollection instances,
			String transformationIdentifier, E engine,
			ListMultimap<String, String> transformationParameters, 
			Map<String, String> executionParameters, TransformationLog log) throws TransformationException;
}
