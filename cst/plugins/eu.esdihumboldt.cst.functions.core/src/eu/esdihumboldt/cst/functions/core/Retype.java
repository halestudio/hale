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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.functions.RenameFunction;
import eu.esdihumboldt.hale.common.align.model.functions.RetypeFunction;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.AbstractTypeTransformation;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.instance.model.FamilyInstance;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition;
import net.jcip.annotations.Immutable;

/**
 * Simple 1:1 retype. In addition supports structural rename for properties.
 * 
 * @author Simon Templer
 */
@Immutable
public class Retype extends AbstractTypeTransformation<TransformationEngine>
		implements RetypeFunction {

	@Override
	public void execute(String transformationIdentifier, TransformationEngine engine,
			Map<String, String> executionParameters, TransformationLog log, Cell cell) {
		// for each source instance create a target instance
		TypeDefinition targetType = getTarget().values().iterator().next().getDefinition()
				.getDefinition();
		MutableInstance target = null;

		// structural rename
		boolean structuralRename = getOptionalParameter(RenameFunction.PARAMETER_STRUCTURAL_RENAME,
				Value.of(false)).as(Boolean.class, false);
		if (structuralRename) {
			boolean ignoreNamespaces = getOptionalParameter(
					RenameFunction.PARAMETER_IGNORE_NAMESPACES, Value.of(false)).as(Boolean.class,
							false);
			boolean copyGeometries = getOptionalParameter(RenameFunction.PARAMETER_COPY_GEOMETRIES,
					Value.of(true)).as(Boolean.class);
			target = doStructuralRename(getSource(), targetType, ignoreNamespaces, copyGeometries,
					log);
		}
		if (target == null) {
			target = getInstanceFactory().createInstance(targetType);
		}

		getPropertyTransformer().publish(getSource(), target, log, cell);
	}

	private MutableInstance doStructuralRename(FamilyInstance source, TypeDefinition targetType,
			boolean ignoreNamespaces, boolean copyGeometries, TransformationLog log) {
		ListMultimap<String, ParameterValue> params = getParameters();

		Set<QName> skipProperties = new HashSet<>();
		if (params != null && !params.isEmpty()) {
			for (ParameterValue value : params.get(PARAMETER_SKIP_ROOT_PROPERTIES)) {
				skipProperties.add(value.as(QName.class));
			}
		}

		// create a dummy child definition for the structural rename
		PropertyDefinition dummyProp = new DefaultPropertyDefinition(new QName("dummyProp"),
				new DefaultTypeDefinition(new QName("dummyType")), targetType);

		Object result = Rename.structuralRename(source, dummyProp, ignoreNamespaces,
				getInstanceFactory(), copyGeometries, skipProperties);
		if (result instanceof MutableInstance) {
			return ((MutableInstance) result);
		}
		else {
			log.error(log.createMessage("Structural rename in type transformation failed", null));
			return null;
		}
	}
}
