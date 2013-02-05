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

import java.util.Map;
import java.util.UUID;

import net.jcip.annotations.Immutable;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.functions.GenerateUIDFunction;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyValue;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.scripting.transformation.AbstractSingleTargetScriptedPropertyTransformation;

/**
 * Generate unique id function.
 * 
 * @author Andrea Antonello
 */
@Immutable
public class GenerateUID extends
		AbstractSingleTargetScriptedPropertyTransformation<TransformationEngine> implements
		GenerateUIDFunction {

	@Override
	protected Object evaluate(String transformationIdentifier, TransformationEngine engine,
			ListMultimap<String, PropertyValue> variables, String resultName,
			PropertyEntityDefinition resultProperty, Map<String, String> executionParameters,
			TransformationLog log) throws TransformationException {

		TypeDefinition type = resultProperty.getType();
		String typeName = type.getDisplayName();
		String localName = resultProperty.getDefinition().getName().getLocalPart();

		String name = typeName + "_" + localName + "_" + UUID.randomUUID().getMostSignificantBits();

		return name;
	}

}
