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

import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ListMultimap;

import de.interactive_instruments.xtraserver.config.util.api.MappingValue;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.Property;
import eu.esdihumboldt.hale.common.align.model.functions.ClassificationMappingFunction;
import eu.esdihumboldt.hale.common.align.model.functions.ClassificationMappingUtil;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.service.ServiceManager;
import eu.esdihumboldt.hale.common.lookup.LookupTable;
import eu.esdihumboldt.hale.io.appschema.writer.AppSchemaMappingUtils;

/**
 * Transforms the {@link ClassificationMappingFunction} to a
 * {@link MappingValue}
 * 
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
class ClassificationMappingHandler extends AbstractPropertyTransformationHandler {

	private final static Pattern NIL_PROPERTY = Pattern
			.compile("(/?(http://www.w3.org/2001/XMLSchema:)?@?nilReason)|(/?@?null)");

	ClassificationMappingHandler(final MappingContext mappingContext) {
		super(mappingContext);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.xtraserver.writer.handler.TransformationHandler#handle(eu.esdihumboldt.hale.common.align.model.Cell)
	 */
	@Override
	public void doHandle(final Cell propertyCell, final Property targetProperty,
			final MappingValue mappingValue) {

		mappingValue.setValue(propertyName(AppSchemaMappingUtils.getSourceProperty(propertyCell)
				.getDefinition().getPropertyPath()));
		final String path = buildPath(targetProperty.getDefinition().getPropertyPath());
		final Matcher matcher = NIL_PROPERTY.matcher(path);

		final ListMultimap<String, ParameterValue> parameters = propertyCell
				.getTransformationParameters();

		if (matcher.find()) {
			mappingValue.setMappingMode("nil");
			mappingValue.setTarget(matcher.replaceAll(""));
		}
		else {
			mappingValue.setTarget(path);
		}

		// Assign DB codes and values from the lookup table
		final LookupTable lookup = ClassificationMappingUtil.getClassificationLookup(parameters,
				new ServiceManager(ServiceManager.SCOPE_PROJECT));
		if (lookup != null) {
			final StringBuilder dbCodeBuilder = new StringBuilder();
			final StringBuilder dbValueBuilder = new StringBuilder();
			final Map<Value, Value> valueMap = lookup.asMap();
			final Iterator<Value> it = valueMap.keySet().iterator();
			while (it.hasNext()) {
				final Value sourceValue = it.next();
				final String targetValueStr = valueMap.get(sourceValue).as(String.class);
				dbCodeBuilder.append(sourceValue.as(String.class));

				dbValueBuilder.append('\'');
				dbValueBuilder.append(targetValueStr);
				dbValueBuilder.append('\'');

				if (it.hasNext()) {
					dbCodeBuilder.append(' ');
					dbValueBuilder.append(' ');
				}
			}
			mappingValue.setDbCodes(dbCodeBuilder.toString());
			mappingValue.setDbValues(dbValueBuilder.toString());
		}
		else {
			mappingValue.setDbCodes("NULL");
		}
	}

}
