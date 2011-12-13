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

package eu.esdihumboldt.cst.functions.numeric;

import java.util.Map;

import net.jcip.annotations.Immutable;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.AbstractPropertyTransformation;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;

/**
 * Mathematical expression evaluation function.
 * @author Simon Templer
 */
@Immutable
public class MathematicalExpression extends AbstractPropertyTransformation<TransformationEngine> {

	@Override
	protected ListMultimap<String, Object> evaluate(
			String transformationIdentifier, TransformationEngine engine,
			ListMultimap<String, PropertyValue> variables,
			ListMultimap<String, PropertyEntityDefinition> resultNames,
			Map<String, String> executionParameters, TransformationLog log)
			throws TransformationException {
		// TODO Auto-generated method stub
		return null;
	}

}
