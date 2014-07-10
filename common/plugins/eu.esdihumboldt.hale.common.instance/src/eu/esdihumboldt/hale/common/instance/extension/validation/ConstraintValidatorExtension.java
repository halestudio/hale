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

package eu.esdihumboldt.hale.common.instance.extension.validation;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.AbstractExtension;
import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.PropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;

/**
 * Extension for constraint validators.
 * 
 * @author Kai Schwierczek
 */
public class ConstraintValidatorExtension extends
		AbstractExtension<ConstraintValidator, ConstraintValidatorFactory> {

	private static final ALogger log = ALoggerFactory.getLogger(ConstraintValidatorExtension.class);

	/**
	 * The extension point ID.
	 */
	public static final String ID = "eu.esdihumboldt.hale.instance.validation";

	private static ConstraintValidatorExtension instance;

	private final Map<Class<TypeConstraint>, TypeConstraintValidator> typeValidators;
	private final Map<Class<PropertyConstraint>, PropertyConstraintValidator> propertyValidators;
	private final Map<Class<GroupPropertyConstraint>, GroupPropertyConstraintValidator> groupPropertyValidators;

	/**
	 * Get the extension instance
	 * 
	 * @return the constraint validator extension
	 */
	public static ConstraintValidatorExtension getInstance() {
		if (instance == null) {
			instance = new ConstraintValidatorExtension();
		}
		return instance;
	}

	/**
	 * Default constructor.
	 */
	@SuppressWarnings("unchecked")
	private ConstraintValidatorExtension() {
		super(ID);

		// Build maps
		List<ConstraintValidatorFactory> factories = getFactories();
		Map<Class<TypeConstraint>, TypeConstraintValidator> typeValidators = new HashMap<Class<TypeConstraint>, TypeConstraintValidator>();
		Map<Class<PropertyConstraint>, PropertyConstraintValidator> propertyValidators = new HashMap<Class<PropertyConstraint>, PropertyConstraintValidator>();
		Map<Class<GroupPropertyConstraint>, GroupPropertyConstraintValidator> groupPropertyValidators = new HashMap<Class<GroupPropertyConstraint>, GroupPropertyConstraintValidator>();

		for (ConstraintValidatorFactory factory : factories) {
			Class<?> clazz = factory.getConstraintClass();
			if (factory.isTypeConstraintValidator()) {
				if (typeValidators.containsKey(clazz))
					log.warn("Multiple type validators for " + clazz
							+ " declared. This is not supported.");
				else
					try {
						typeValidators.put((Class<TypeConstraint>) clazz,
								(TypeConstraintValidator) factory.createExtensionObject());
					} catch (Exception e) {
						log.warn("Exception creating type constraint validator", e);
					}
			}
			else if (factory.isPropertyConstraintValidator()) {
				if (propertyValidators.containsKey(clazz))
					log.warn("Multiple property validators for " + clazz
							+ " declared. This is not supported.");
				else
					try {
						propertyValidators.put((Class<PropertyConstraint>) clazz,
								(PropertyConstraintValidator) factory.createExtensionObject());
					} catch (Exception e) {
						log.warn("Exception creating property constraint validator", e);
					}
			}
			else if (factory.isGroupPropertyConstraintValidator()) {
				if (groupPropertyValidators.containsKey(clazz))
					log.warn("Multiple group property validators for " + clazz
							+ " declared. This is not supported.");
				else
					try {
						groupPropertyValidators.put((Class<GroupPropertyConstraint>) clazz,
								(GroupPropertyConstraintValidator) factory.createExtensionObject());
					} catch (Exception e) {
						log.warn("Exception creating group property constraint validator", e);
					}
			}
		}

		this.typeValidators = Collections.unmodifiableMap(typeValidators);
		this.propertyValidators = Collections.unmodifiableMap(propertyValidators);
		this.groupPropertyValidators = Collections.unmodifiableMap(groupPropertyValidators);
	}

	/**
	 * @see de.fhg.igd.eclipse.util.extension.AbstractExtension#createFactory(org.eclipse.core.runtime.IConfigurationElement)
	 */
	@Override
	protected ConstraintValidatorFactory createFactory(IConfigurationElement conf) throws Exception {
		return new ConstraintValidatorFactory(conf);
	}

	/**
	 * Returns a map with all registered {@link TypeConstraintValidator}.
	 * 
	 * @return a map with all registered {@link TypeConstraintValidator}
	 */
	public Map<Class<TypeConstraint>, TypeConstraintValidator> getTypeConstraintValidators() {
		return typeValidators;
	}

	/**
	 * Returns a map with all registered {@link PropertyConstraintValidator}.
	 * 
	 * @return a map with all registered {@link PropertyConstraintValidator}
	 */
	public Map<Class<PropertyConstraint>, PropertyConstraintValidator> getPropertyConstraintValidators() {
		return propertyValidators;
	}

	/**
	 * Returns a map with all registered
	 * {@link GroupPropertyConstraintValidator}.
	 * 
	 * @return a map with all registered
	 *         {@link GroupPropertyConstraintValidator}
	 */
	public Map<Class<GroupPropertyConstraint>, GroupPropertyConstraintValidator> getGroupPropertyConstraintValidators() {
		return groupPropertyValidators;
	}
}
