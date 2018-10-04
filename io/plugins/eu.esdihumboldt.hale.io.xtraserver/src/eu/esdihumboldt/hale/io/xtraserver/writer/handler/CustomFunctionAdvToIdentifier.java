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

import java.util.List;
import java.util.Optional;

import javax.xml.namespace.QName;

import com.google.common.collect.ImmutableList;

import de.interactive_instruments.xtraserver.config.api.MappingValue;
import de.interactive_instruments.xtraserver.config.api.MappingValueBuilder;
import de.interactive_instruments.xtraserver.config.api.MappingValueBuilder.ValueDefault;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Property;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.io.xtraserver.writer.XtraServerMappingUtils;

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
	 *      eu.esdihumboldt.hale.common.align.model.Property)
	 */
	@Override
	public Optional<MappingValue> doHandle(final Cell propertyCell, final Property targetProperty) {

		final Value inspireNamespace = mappingContext
				.getTransformationProperty(MappingContext.PROPERTY_INSPIRE_NAMESPACE);

		final String propertyName = propertyName(XtraServerMappingUtils
				.getSourceProperty(propertyCell).getDefinition().getPropertyPath());

		String value = "'";
		if (!inspireNamespace.isEmpty()) {
			value += inspireNamespace.as(String.class)
					+ (inspireNamespace.as(String.class).endsWith("/") ? "" : "/");
		}
		value += mappingContext.getFeatureTypeName() + "_' || $T$." + propertyName;

		final List<QName> propPath = buildPath(targetProperty.getDefinition().getPropertyPath());

		final ValueDefault mappingValue = new MappingValueBuilder().expression()
				.qualifiedTargetPath(propPath).value(value);

		// Add codespace
		final List<QName> codespacePath = ImmutableList.<QName> builder().addAll(propPath)
				.add(new QName("@codeSpace")).build();
		final MappingValue codeSpaceValue = new MappingValueBuilder().constant()
				.qualifiedTargetPath(codespacePath).value("http://inspire.ec.europa.eu/ids")
				.build();
		final String tableName = ((CellParentWrapper) propertyCell).getTableName();
		mappingContext.addValueMappingToTable(targetProperty, codeSpaceValue, tableName);

		return Optional.of(mappingValue.build());
	}

}
