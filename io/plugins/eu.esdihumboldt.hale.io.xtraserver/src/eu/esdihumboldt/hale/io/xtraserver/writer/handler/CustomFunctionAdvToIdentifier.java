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

package eu.esdihumboldt.hale.io.xtraserver.writer.handler;

import de.interactive_instruments.xtraserver.config.util.api.MappingValue;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Property;
import eu.esdihumboldt.hale.common.core.io.Value;

/**
 * Transforms the custom function 'custom:alignment:adv.inspire.identifier' to a
 * {@link MappingValue}
 * 
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
class CustomFunctionAdvToIdentifier extends FormattedStringHandler {

	public final static String FUNCTION_ID = "custom:alignment:adv.inspire.identifier";

	CustomFunctionAdvToIdentifier(final MappingContext mappingContext) {
		super(mappingContext);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.xtraserver.writer.handler.AbstractPropertyTransformationHandler#doHandle(eu.esdihumboldt.hale.common.align.model.Cell,
	 *      eu.esdihumboldt.hale.common.align.model.Property,
	 *      de.interactive_instruments.xtraserver.config.util.api.MappingValue)
	 */
	@Override
	public void doHandle(final Cell propertyCell, final Property targetProperty,
			final MappingValue mappingValue) {
		setExpressionType(mappingValue);
		final Value inspireNamespace = mappingContext
				.getTransformationProperty(MappingContext.PROPERTY_INSPIRE_NAMESPACE);
		if (inspireNamespace.isEmpty()) {
			mappingValue.setValue("'" + mappingContext.getFeatureTypeName() + "_' || $T$.id");
		}
		else {
			mappingValue.setValue("'" + inspireNamespace.as(String.class) + "' || $T$.id");
		}
		final String propPath = buildPath(targetProperty.getDefinition().getPropertyPath());
		mappingValue.setTarget(propPath);

		// Add codespace
		final MappingValue codeSpaceValue = MappingValue.create(mappingContext.getNamespaces());
		setConstantType(codeSpaceValue);
		codeSpaceValue.setValue("http://inspire.ec.europa.eu/ids");
		codeSpaceValue.setTarget(propPath.replaceAll("/?@.*", "") + "/@codeSpace");
		final String tableName = ((CellParentWrapper) propertyCell).getTableName();
		mappingContext.addValueMappingToTable(targetProperty, mappingValue, tableName);
	}

}
