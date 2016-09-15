/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.cst.functions.core;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.functions.AssignFunction;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyValue;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.NoResultException;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.scripting.transformation.AbstractSingleTargetScriptedPropertyTransformation;
import net.jcip.annotations.Immutable;

/**
 * Property value assignment function.
 * 
 * @author Simon Templer
 */
@Immutable
public class Assign extends AbstractSingleTargetScriptedPropertyTransformation<TransformationEngine>
		implements AssignFunction {

	@Override
	protected Object evaluate(String transformationIdentifier, TransformationEngine engine,
			ListMultimap<String, PropertyValue> variables, String resultName,
			PropertyEntityDefinition resultProperty, Map<String, String> executionParameters,
			TransformationLog log) throws TransformationException, NoResultException {
		// check anchor

		/*
		 * Source node that are not defined will not result in a variable being
		 * set (if the variable is optional), thus the only way we can check if
		 * there should be an anchor, is checking the cell.
		 */
		boolean hasAnchor = false;
		ListMultimap<String, ? extends Entity> source = getCell().getSource();
		if (source != null) {
			hasAnchor = source.containsKey(ENTITY_ANCHOR);
		}

		if (hasAnchor) {
			// check if anchor is present
			List<PropertyValue> anchors = variables.get(ENTITY_ANCHOR);
			if (anchors.isEmpty() || anchors.get(0).getValue() == null) {
				// no or null value for anchor
				throw new NoResultException();
			}
		}

		// assign the value supplied as parameter
		// conversion will be applied automatically to fit the binding
		return getTransformedParameterChecked(PARAMETER_VALUE);
	}

}
