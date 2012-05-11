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

import java.util.List;
import java.util.Map;

import net.jcip.annotations.Immutable;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.functions.AssignFunction;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyValue;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.AbstractSingleTargetPropertyTransformation;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.NoResultException;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;

/**
 * Property value assignment function.
 * @author Simon Templer
 */
@Immutable
public class Assign extends AbstractSingleTargetPropertyTransformation<TransformationEngine> implements AssignFunction {
	
	@Override
	protected Object evaluate(
			String transformationIdentifier, TransformationEngine engine,
			ListMultimap<String, PropertyValue> variables,
			String resultName, PropertyEntityDefinition resultProperty,
			Map<String, String> executionParameters, TransformationLog log) 
			throws TransformationException, NoResultException {
		// check anchors
		List<PropertyValue> anchors = variables.get(ENTITY_ANCHOR);
		// ensure that every anchor has a value
		for (PropertyValue anchor : anchors) {
			if (anchor.getValue() == null) {
				// if an anchor without value is found, no result is created
				throw new NoResultException();
			}
		}
		
		// assign the value supplied as parameter
		// conversion will be applied automatically to fit the binding
		return getParameterChecked(PARAMETER_VALUE); 
	}

}
