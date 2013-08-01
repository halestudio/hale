/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.align.io.impl.internal;

import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ChildContextType;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ClassType;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ConditionType;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.PropertyType;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.QNameType;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.Condition;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.instance.extension.filter.FilterDefinitionManager;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;
import eu.esdihumboldt.util.Pair;

/**
 * Static methods for conversion from JAXB to {@link EntityDefinition}s.
 * 
 * @author Kai Schwierczek
 */
public class JaxbToEntityDefinition {

	private JaxbToEntityDefinition() {
	}

	/**
	 * Converts the given class to a type entity definition.
	 * 
	 * @param classType the class to convert
	 * @param types the type index to use
	 * @param schemaSpace the schema space to assign
	 * @return the type entity definition
	 */
	public static TypeEntityDefinition convert(ClassType classType, TypeIndex types,
			SchemaSpaceID schemaSpace) {
		TypeDefinition typeDef = types.getType(asName(classType.getType()));

		Filter filter = getTypeFilter(classType);

		return new TypeEntityDefinition(typeDef, schemaSpace, filter);
	}

	private static Filter getTypeFilter(ClassType classType) {
		if (classType.getType() != null && classType.getType().getCondition() != null) {
			return FilterDefinitionManager.getInstance().from(
					classType.getType().getCondition().getLang(),
					classType.getType().getCondition().getValue());
		}
		return null;
	}

	/**
	 * Converts the given property to a property entity definition.
	 * 
	 * @param property the property to convert
	 * @param types the type index to use
	 * @param schemaSpace the schema space to assign
	 * @return the property entity definition
	 */
	public static PropertyEntityDefinition convert(PropertyType property, TypeIndex types,
			SchemaSpaceID schemaSpace) {
		TypeDefinition typeDef = types.getType(asName(property.getType()));

		Filter filter = getTypeFilter(property);

		List<ChildContext> path = new ArrayList<ChildContext>();

		DefinitionGroup parent = typeDef;
		for (ChildContextType childContext : property.getChild()) {
			if (parent == null) {
				throw new IllegalStateException(
						"Could not resolve property entity definition: child not present");
			}

			Pair<ChildDefinition<?>, List<ChildDefinition<?>>> childs = PropertyBean.findChild(
					parent, asName(childContext));

			// if the child is still null throw an exception
			if (childs == null || childs.getFirst() == null) {
				String childName = asName(childContext).getLocalPart();
				String parentName;
				if (parent instanceof Definition<?>) {
					parentName = ((Definition<?>) parent).getName().getLocalPart();
				}
				else {
					parentName = parent.getIdentifier();
				}
				throw new IllegalStateException(
						MessageFormat
								.format("Could not resolve property entity definition: child {0} not found in parent {1}",
										childName, parentName));
			}

			ChildDefinition<?> child = childs.getFirst();

			if (childs.getSecond() != null) {
				for (ChildDefinition<?> pathElems : childs.getSecond()) {
					path.add(new ChildContext(contextName(childContext.getContext()),
							contextIndex(childContext.getIndex()), createCondition(childContext
									.getCondition()), pathElems));
				}
			}

			path.add(new ChildContext(contextName(childContext.getContext()),
					contextIndex(childContext.getIndex()), createCondition(childContext
							.getCondition()), child));

			if (child instanceof DefinitionGroup) {
				parent = (DefinitionGroup) child;
			}
			else if (child.asProperty() != null) {
				parent = child.asProperty().getPropertyType();
			}
			else {
				parent = null;
			}
		}

		return new PropertyEntityDefinition(typeDef, path, schemaSpace, filter);
	}

	/**
	 * Create a condition.
	 * 
	 * @param conditionFilter the condition filter
	 * @return the condition or <code>null</code>
	 */
	private static Condition createCondition(ConditionType conditionFilter) {
		if (conditionFilter == null)
			return null;

		Filter filter = FilterDefinitionManager.getInstance().from(conditionFilter.getLang(),
				conditionFilter.getValue());
		if (filter != null) {
			return new Condition(filter);
		}
		return null;
	}

	private static Integer contextName(BigInteger name) {
		if (name == null)
			return null;

//		return Integer.valueOf(name);
		return name.intValue();
	}

	private static Integer contextIndex(BigInteger index) {
		if (index == null)
			return null;

		return index.intValue();
	}

	private static QName asName(QNameType qname) {
		if (qname.getNs() == null || qname.getNs().isEmpty()) {
			return new QName(qname.getName());
		}
		return new QName(qname.getNs(), qname.getName());
	}
}
