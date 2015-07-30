/*
 * Copyright (c) 2015 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.appschema.writer.internal;

import static eu.esdihumboldt.hale.common.align.model.functions.JoinFunction.PARAMETER_JOIN;
import static eu.esdihumboldt.hale.io.appschema.writer.internal.AppSchemaMappingUtils.findChildFeatureType;
import static eu.esdihumboldt.hale.io.appschema.writer.internal.AppSchemaMappingUtils.findOwningFeatureType;
import static eu.esdihumboldt.hale.io.appschema.writer.internal.AppSchemaMappingUtils.findOwningFeatureTypePath;
import static eu.esdihumboldt.hale.io.appschema.writer.internal.AppSchemaMappingUtils.getTargetProperty;
import static eu.esdihumboldt.hale.io.appschema.writer.internal.AppSchemaMappingUtils.isHRefAttribute;

import java.util.Collection;
import java.util.List;

import eu.esdihumboldt.cst.functions.core.Join;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.Property;
import eu.esdihumboldt.hale.common.align.model.functions.join.JoinParameter;
import eu.esdihumboldt.hale.common.align.model.functions.join.JoinParameter.JoinCondition;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.AttributeExpressionMappingType;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.AttributeMappingType;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.TypeMappingsPropertyType.FeatureTypeMapping;

/**
 * Translates a type cell specifying a {@link Join} transformation function to
 * two app-schema feature type mappings.
 * 
 * <p>
 * The current implementation has some limitations:
 * <ul>
 * <li>joining more than two tables is not supported</li>
 * <li>specifying more than one join condition is not supported</li>
 * <li>the first join type is considered to be the container feature type; the
 * second join type is considered to be the nested feature type</li>
 * <li>joining non-feature types is not supported</li>
 * </ul>
 * </p>
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class JoinHandler implements TypeTransformationHandler {

	private PropertyEntityDefinition baseProperty;
	private PropertyEntityDefinition joinProperty;

	/**
	 * 
	 * @see eu.esdihumboldt.hale.io.appschema.writer.internal.TypeTransformationHandler#handleTypeTransformation(eu.esdihumboldt.hale.common.align.model.Alignment,
	 *      eu.esdihumboldt.hale.common.align.model.Cell,
	 *      eu.esdihumboldt.hale.io.appschema.writer.internal.AppSchemaMappingWrapper)
	 */
	@Override
	public FeatureTypeMapping handleTypeTransformation(Alignment alignment, Cell typeCell,
			AppSchemaMappingWrapper context) {

		JoinParameter joinParameter = typeCell.getTransformationParameters().get(PARAMETER_JOIN)
				.get(0).as(JoinParameter.class);

		String validation = joinParameter.validate();
		if (validation != null)
			throw new IllegalArgumentException("Join parameter invalid: " + validation);

		if (joinParameter.types.size() > 2) {
			throw new IllegalArgumentException("Only join between 2 types is supported so far");
		}

		if (joinParameter.conditions.size() > 1) {
			throw new IllegalArgumentException("Only single condition joins are supported so far");
		}

		// TODO: I assume the first type is the container type, whereas the
		// second type is nested in the first
		TypeEntityDefinition containerType = joinParameter.types.get(0);
		TypeEntityDefinition nestedType = joinParameter.types.get(1);
		JoinCondition joinCondition = joinParameter.conditions.iterator().next();
		baseProperty = joinCondition.baseProperty;
		joinProperty = joinCondition.joinProperty;

		// build FeatureTypeMapping for container type
		Entity containerTypeTarget = typeCell.getTarget().values().iterator().next();
		TypeDefinition containerTypeTargetType = containerTypeTarget.getDefinition().getType();

		FeatureTypeMapping containerFTMapping = context
				.getOrCreateFeatureTypeMapping(containerTypeTargetType);
		containerFTMapping.setSourceType(containerType.getDefinition().getName().getLocalPart());

		// build FeatureTypeMapping for nested type
		FeatureTypeMapping nestedFTMapping = null;
		TypeDefinition nestedFT = null;
		List<ChildContext> nestedFTPath = null;
		Collection<? extends Cell> propertyCells = alignment.getPropertyCells(typeCell);
		for (Cell propertyCell : propertyCells) {
			Property sourceProperty = AppSchemaMappingUtils.getSourceProperty(propertyCell);
			if (sourceProperty != null) {
				TypeDefinition sourceType = sourceProperty.getDefinition().getDefinition()
						.getParentType();
				if (sourceType.getName().equals(nestedType.getDefinition().getName())) {
					// source property belongs to nested type: determine
					// target type
					Property targetProperty = getTargetProperty(propertyCell);
					nestedFT = findOwningFeatureType(targetProperty.getDefinition());
					if (nestedFT != null
							&& !nestedFT.getName().equals(containerTypeTargetType.getName())) {
						// target property belongs to a feature type different
						// from the already mapped one: build a new mapping
						nestedFTPath = findOwningFeatureTypePath(targetProperty.getDefinition());

						nestedFTMapping = context.getOrCreateFeatureTypeMapping(nestedFT);
						nestedFTMapping.setSourceType(nestedType.getDefinition().getName()
								.getLocalPart());

						// TODO: I assume at most 2 FeatureTypes are involved in
						// the join
						break;
					}
					else if (isHRefAttribute(targetProperty.getDefinition().getDefinition())) {
						// check if target property is a href attribute
						Property hrefProperty = targetProperty;
						List<ChildContext> hrefPropertyPath = hrefProperty.getDefinition()
								.getPropertyPath();
						List<ChildContext> hrefContainerPath = hrefPropertyPath.subList(0,
								hrefPropertyPath.size() - 1);
						TypeDefinition hrefParentType = hrefProperty.getDefinition()
								.getDefinition().getParentType();
						TypeDefinition childFT = findChildFeatureType(hrefParentType);

						if (childFT != null) {
							nestedFTPath = hrefContainerPath;
							nestedFTMapping = context.getOrCreateFeatureTypeMapping(childFT);
							nestedFTMapping.setSourceType(nestedType.getDefinition().getName()
									.getLocalPart());

							// TODO: I assume at most 2 FeatureTypes are
							// involved in the join
							break;
						}
					}
				}
			}
		}

		// build join mapping
		if (nestedFTMapping != null && nestedFTPath != null) {
			AttributeMappingType containerJoinMapping = context.getOrCreateAttributeMapping(
					containerTypeTargetType, nestedFTPath);
			containerJoinMapping.setTargetAttribute(context.buildAttributeXPath(nestedFT,
					nestedFTPath));
			// set isMultiple attribute
			PropertyDefinition targetPropertyDef = nestedFTPath.get(nestedFTPath.size() - 1)
					.getChild().asProperty();
			if (AppSchemaMappingUtils.isMultiple(targetPropertyDef)) {
				containerJoinMapping.setIsMultiple(true);
			}

			AttributeExpressionMappingType containerSourceExpr = new AttributeExpressionMappingType();
			// join column extracted from join condition
			containerSourceExpr.setOCQL(baseProperty.getDefinition().getName().getLocalPart());
			containerSourceExpr.setLinkElement(nestedFTMapping.getTargetElement());
			// TODO: support multiple joins (e.g. FEATURE_LINK[1],
			// FEATURE_LINK[2],
			// ...)
			containerSourceExpr.setLinkField(AppSchemaMappingWrapper.FEATURE_LINK_FIELD);
			containerJoinMapping.setSourceExpression(containerSourceExpr);

			AttributeMappingType nestedJoinMapping = new AttributeMappingType();
			AttributeExpressionMappingType nestedSourceExpr = new AttributeExpressionMappingType();
			// join column extracted from join condition
			nestedSourceExpr.setOCQL(joinProperty.getDefinition().getName().getLocalPart());
			nestedJoinMapping.setSourceExpression(nestedSourceExpr);
			// TODO: support multiple joins (e.g. FEATURE_LINK[1],
			// FEATURE_LINK[2],
			// ...)
			nestedJoinMapping.setTargetAttribute(AppSchemaMappingWrapper.FEATURE_LINK_FIELD);
			nestedFTMapping.getAttributeMappings().getAttributeMapping().add(nestedJoinMapping);
		}

		return containerFTMapping;
	}

}
