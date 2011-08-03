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

package eu.esdihumboldt.cst.internal;

import java.util.Collection;

import eu.esdihumboldt.hale.align.model.Alignment;
import eu.esdihumboldt.hale.align.model.Type;
import eu.esdihumboldt.hale.align.transformation.report.TransformationReporter;
import eu.esdihumboldt.hale.align.transformation.service.PropertyTransformer;
import eu.esdihumboldt.hale.instance.model.Instance;
import eu.esdihumboldt.hale.instance.model.MutableInstance;

/**
 * TODO Type description
 * @author Simon Templer
 */
public class SimplePropertyTransformer implements PropertyTransformer {
	
	private final Alignment alignment;
	
	private final TransformationReporter reporter;

	/**
	 * Create a simple property transformer
	 * @param alignment the alignment
	 * @param reporter the transformation log to report any transformation 
	 *   messages to
	 */
	public SimplePropertyTransformer(Alignment alignment, TransformationReporter reporter) {
		this.alignment = alignment;
		this.reporter = reporter;
	}

	/**
	 * @see PropertyTransformer#publish(Collection, Instance, MutableInstance)
	 */
	@Override
	public void publish(Collection<? extends Type> sourceTypes,
			Instance source, MutableInstance target) {
		// TODO Auto-generated method stub
		//TODO identify transformations to be executed on given instances
	}

}
