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
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.namespace.QName;

import com.google.common.collect.ListMultimap;

import de.interactive_instruments.xtraserver.config.api.MappingValue;
import de.interactive_instruments.xtraserver.config.api.MappingValueBuilder;
import de.interactive_instruments.xtraserver.config.api.MappingValueBuilder.ValueClassification;
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

	private final static String NIL_REASON = "@nilReason";

	ClassificationMappingHandler(final MappingContext mappingContext) {
		super(mappingContext);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.xtraserver.writer.handler.TransformationHandler#handle(eu.esdihumboldt.hale.common.align.model.Cell)
	 */
	@Override
	public Optional<MappingValue> doHandle(final Cell propertyCell, final Property targetProperty) {

		final ValueClassification mappingValue;

		final List<QName> path = buildPath(targetProperty.getDefinition().getPropertyPath());

		if (path.get(path.size() - 1).getLocalPart().equals(NIL_REASON)) {
			mappingValue = new MappingValueBuilder().nil();
			mappingValue.qualifiedTargetPath(path.subList(0, path.size() - 1));
		}
		else {
			mappingValue = new MappingValueBuilder().classification();
			mappingValue.qualifiedTargetPath(path);
		}

		mappingValue.value(propertyName(AppSchemaMappingUtils.getSourceProperty(propertyCell)
				.getDefinition().getPropertyPath()));

		final ListMultimap<String, ParameterValue> parameters = propertyCell
				.getTransformationParameters();

		// Assign DB codes and values from the lookup table
		final LookupTable lookup = ClassificationMappingUtil.getClassificationLookup(parameters,
				new ServiceManager(ServiceManager.SCOPE_PROJECT));
		if (lookup != null) {
			final Map<Value, Value> valueMap = lookup.asMap();
			final Iterator<Value> it = valueMap.keySet().iterator();
			while (it.hasNext()) {
				final Value sourceValue = it.next();
				final String targetValueStr = '\'' + valueMap.get(sourceValue).as(String.class)
						+ '\'';

				mappingValue.keyValue(sourceValue.as(String.class), targetValueStr);
			}
		}
		else {
			mappingValue.keyValue("NULL", "");
		}

		return Optional.of(mappingValue.build());
	}

}
