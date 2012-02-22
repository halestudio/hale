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

package eu.esdihumboldt.hale.common.align.transformation.service;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationReport;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;

/**
 * Transformation service
 * @author Simon Templer
 */
public interface TransformationService {
	
	/**
	 * Transform a set of source instances according to the given alignment.
	 * @param alignment the alignment
	 * @param source the source instances
	 * @param target the transformed instance sink
	 * @param progressIndicator the progress indicator
	 * @return the transformation report
	 */
	public TransformationReport transform(Alignment alignment, InstanceCollection source,
			InstanceSink target, ProgressIndicator progressIndicator);

}
