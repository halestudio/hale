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

package eu.esdihumboldt.cst;

import eu.esdihumboldt.hale.align.model.Alignment;
import eu.esdihumboldt.hale.align.transformation.report.TransformationReport;
import eu.esdihumboldt.hale.align.transformation.report.TransformationReporter;
import eu.esdihumboldt.hale.align.transformation.report.impl.DefaultTransformationReporter;
import eu.esdihumboldt.hale.align.transformation.service.InstanceSink;
import eu.esdihumboldt.hale.align.transformation.service.TransformationService;
import eu.esdihumboldt.hale.instance.model.InstanceCollection;

/**
 * Transformation service implementation
 * @author Simon Templer
 */
public class ConceptualSchemaTransformer implements TransformationService {

	/**
	 * @see TransformationService#transform(Alignment, InstanceCollection, InstanceSink)
	 */
	@Override
	public TransformationReport transform(Alignment alignment, InstanceCollection source,
			InstanceSink target) {
		TransformationReporter reporter = new DefaultTransformationReporter(
				"Instance transformation", true) {

					@Override
					protected String getFailSummary() {
						return "Transformation failed";
					}

					@Override
					protected String getSuccessSummary() {
						return "Transformation completed";
					}
			
		};
		// TODO Auto-generated method stub
		reporter.setSuccess(false);
		return reporter;
	}

}
