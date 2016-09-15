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

package eu.esdihumboldt.hale.common.align.extension.function;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import com.vividsolutions.jts.geom.Geometry;

import de.fhg.igd.eclipse.util.extension.ExtensionUtil;
import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.align.model.condition.PropertyCondition;
import eu.esdihumboldt.hale.common.align.model.condition.PropertyOrChildrenTypeCondition;
import eu.esdihumboldt.hale.common.align.model.condition.PropertyTypeCondition;
import eu.esdihumboldt.hale.common.align.model.condition.TypeCondition;
import eu.esdihumboldt.hale.common.align.model.condition.impl.BindingCondition;
import eu.esdihumboldt.hale.common.align.model.condition.impl.GeometryCondition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.AugmentedValueFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;

/**
 * Represents a source or target property as parameter to a
 * {@link PropertyFunction}
 * 
 * @author Simon Templer
 */
public final class PropertyParameter extends AbstractParameter implements PropertyParameterDefinition {

	private static final ALogger log = ALoggerFactory.getLogger(PropertyParameter.class);

	private static final PropertyCondition VALUE_CONDITION = new PropertyTypeCondition(
			new TypeCondition() {

				@Override
				public boolean accept(Type entity) {
					TypeDefinition type = entity.getDefinition().getDefinition();
					return type.getConstraint(HasValueFlag.class).isEnabled()
							|| type.getConstraint(AugmentedValueFlag.class).isEnabled();
				}
			});

	private static final PropertyCondition VALUE_CONDITION_STRICT = new PropertyTypeCondition(
			new TypeCondition() {

				@Override
				public boolean accept(Type entity) {
					TypeDefinition type = entity.getDefinition().getDefinition();
					return type.getConstraint(HasValueFlag.class).isEnabled();
				}
			});

	private final List<PropertyCondition> conditions;

	private final boolean eager;

	/**
	 * @see AbstractParameter#AbstractParameter(IConfigurationElement)
	 */
	public PropertyParameter(IConfigurationElement conf) {
		super(conf);

		String eagerFlag = conf.getAttribute("eager");
		// defaults to false
		eager = Boolean.parseBoolean(eagerFlag);

		conditions = createConditions(conf);
	}

	/**
	 * Create a parameter definition.
	 * 
	 * @param name the parameter name
	 * @param minOccurrence min occurrences
	 * @param maxOccurrence max occurrences
	 * @param label human readable label
	 * @param description human readable description
	 * @param conditions a list of property conditions
	 * @param eager {@link #isEager()}
	 */
	public PropertyParameter(String name, int minOccurrence, int maxOccurrence, String label,
			String description, List<PropertyCondition> conditions, boolean eager) {
		super(name, minOccurrence, maxOccurrence, label, description);
		this.conditions = conditions;
		this.eager = eager;
	}

	private static List<PropertyCondition> createConditions(IConfigurationElement conf) {
		List<PropertyCondition> result = new ArrayList<PropertyCondition>();

		IConfigurationElement[] children = conf.getChildren();
		if (children != null) {
			for (IConfigurationElement child : children) {
				String name = child.getName();
				if (name.equals("propertyCondition")) {
					try {
						PropertyCondition condition = (PropertyCondition) child
								.createExecutableExtension("class");
						result.add(condition);
					} catch (CoreException e) {
						log.error("Error creating property condition from extension", e);
					}
				}
				else if (name.equals("bindingCondition")) {
					BindingCondition bc = createBindingCondition(child);
					if (bc != null) {
						result.add(new PropertyTypeCondition(bc));
					}
				}
				else if (name.equals("geometryCondition")) {
					GeometryCondition gc = createGeometryCondition(child);
					if (gc != null) {
						result.add(new PropertyTypeCondition(gc));
					}
				}
				else if (name.equals("geometryOrParentCondition")) {
					GeometryCondition gc = createGeometryCondition(child);
					if (gc != null) {
						result.add(new PropertyOrChildrenTypeCondition(gc));
					}
				}
				else if (name.equals("valueCondition")) {
					String attr = child.getAttribute("allowAugmented");
					boolean allowAugmented;
					if (attr == null || attr.isEmpty()) {
						allowAugmented = true; // default value
					}
					else {
						allowAugmented = Boolean.parseBoolean(attr);
					}
					if (allowAugmented) {
						result.add(VALUE_CONDITION);
					}
					else {
						result.add(VALUE_CONDITION_STRICT);
					}
				}
				else {
					// ignore
					log.warn("Unrecognized property condition");
				}
			}
		}

		return result;
	}

	private static GeometryCondition createGeometryCondition(IConfigurationElement child) {
		Collection<Class<? extends Geometry>> bindings = null;

		IConfigurationElement[] types = child.getChildren("geometryType");
		if (types != null) {
			for (IConfigurationElement type : types) {
				try {
					@SuppressWarnings("unchecked")
					Class<? extends Geometry> geometryType = (Class<? extends Geometry>) ExtensionUtil
							.loadClass(type, "type");
					if (bindings == null) {
						bindings = new HashSet<Class<? extends Geometry>>();
					}
					bindings.add(geometryType);
				} catch (Throwable e) {
					log.error("Could not load geometry class defined in extension", e);
				}
			}
		}

		boolean allowCollection = true; // TODO configurable?
		boolean allowConversion = true; // TODO configurable?
		return new GeometryCondition(bindings, allowConversion, allowCollection);
	}

	private static BindingCondition createBindingCondition(IConfigurationElement child) {
		Class<?> bindingClass = ExtensionUtil.loadClass(child, "compatibleClass");
		if (bindingClass != null) {
			boolean allowConversion = Boolean.parseBoolean(child.getAttribute("allowConversion"));
			// defaults to false

			boolean allowCollection = Boolean.parseBoolean(child.getAttribute("allowCollection"));
			// defaults to false

			return new BindingCondition(bindingClass, allowConversion, allowCollection);
		}
		else {
			log.error("Could not load class for binding condition: "
					+ child.getAttribute("compatibleClass"));
		}

		return null;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.extension.function.PropertyParameterDefinition#getConditions()
	 */
	@Override
	public List<PropertyCondition> getConditions() {
		return Collections.unmodifiableList(conditions);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.extension.function.PropertyParameterDefinition#isEager()
	 */
	@Override
	public boolean isEager() {
		return eager;
	}

}
