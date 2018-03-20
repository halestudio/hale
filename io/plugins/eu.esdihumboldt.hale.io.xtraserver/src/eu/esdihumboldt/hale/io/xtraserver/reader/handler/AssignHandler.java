/*
 * Copyright (c) 2017 interactive instruments GmbH
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
 *     interactive instruments GmbH <http://www.interactive-instruments.de>
 */

package eu.esdihumboldt.hale.io.xtraserver.reader.handler;

import java.util.Map;
import java.util.Optional;

import de.interactive_instruments.xtraserver.config.api.MappingValue;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.functions.AssignFunction;

/**
 * Transforms a {@link MappingValue} to a {@link AssignFunction}
 * 
 * @author zahnen
 */
class AssignHandler extends AbstractPropertyTransformationHandler {

	AssignHandler(final TransformationContext transformationContext) {
		super(transformationContext);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.xtraserver.reader.handler.AbstractPropertyTransformationHandler#doHandle(de.interactive_instruments.xtraserver.config.api.MappingValue,
	 *      java.lang.String)
	 */
	@Override
	public String doHandle(final MappingValue mappingValue, final String tableName) {

		Optional<String> bindValue = Optional.<String> empty();

		if (isAttribute(mappingValue.getTargetPath())) {
			final String parentTarget = mappingValue.getTargetPath().substring(0,
					mappingValue.getTargetPath().lastIndexOf('/'));

			bindValue = transformationContext.getCurrentFeatureTypeMapping()
					.getTableValuesForPath(parentTarget).entrySet().stream()
					.filter(entry -> entry.getKey().getName().equals(tableName))
					.map(Map.Entry::getValue).filter(value -> !value.getValueColumns().isEmpty())
					.map(value -> value.getValueColumns().get(0)).findFirst();
		}

		if (bindValue.isPresent()) {
			transformationContext.nextPropertyTransformation(mappingValue.getQualifiedTargetPath(),
					tableName, "anchor", bindValue.get());
		}
		else {
			transformationContext.nextPropertyTransformation(mappingValue.getQualifiedTargetPath());
		}

		transformationContext.getCurrentPropertyParameters().put("value",
				new ParameterValue(mappingValue.getValue()));

		return bindValue.isPresent() ? AssignFunction.ID_BOUND : AssignFunction.ID;
	}

	private boolean isAttribute(String propertyPath) {
		return propertyPath.contains("/@");
	}
}
