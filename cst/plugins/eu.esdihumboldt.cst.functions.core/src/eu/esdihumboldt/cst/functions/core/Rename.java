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

package eu.esdihumboldt.cst.functions.core;

import java.util.Map;

import net.jcip.annotations.Immutable;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyValue;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.AbstractSingleTargetPropertyTransformation;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.Instance;

/**
 * Property rename function.
 * @author Simon Templer
 */
@Immutable
public class Rename extends AbstractSingleTargetPropertyTransformation<TransformationEngine> {

	@Override
	protected Object evaluate(String transformationIdentifier,
			TransformationEngine engine,
			ListMultimap<String, PropertyValue> variables, String resultName,
			PropertyEntityDefinition resultProperty,
			Map<String, String> executionParameters, TransformationLog log)
			throws TransformationException {
		// get the source value
		Object sourceValue = variables.values().iterator().next().getValue();

		// non-structural rename
		if (sourceValue instanceof Group) {
			if (sourceValue instanceof Instance)
				sourceValue = ((Instance) sourceValue).getValue();
			else
				sourceValue = null;
		}
		return sourceValue;
	}

}
