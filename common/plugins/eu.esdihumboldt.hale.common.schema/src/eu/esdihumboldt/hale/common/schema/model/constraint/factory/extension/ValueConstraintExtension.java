/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.schema.model.constraint.factory.extension;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.simple.IdentifiableExtension;
import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.ValueConstraintFactory;

/**
 * Extension point for {@link ValueConstraintFactory}ies.
 * 
 * @author Simon Templer
 */
public class ValueConstraintExtension
		extends IdentifiableExtension<ValueConstraintFactoryDescriptor> {

	private static final ALogger log = ALoggerFactory.getLogger(ValueConstraintExtension.class);

	/**
	 * Extension point ID.
	 */
	public static final String ID = "eu.esdihumboldt.hale.schema.valueconstraint";

	/**
	 * The extension instance.
	 */
	public static final ValueConstraintExtension INSTANCE = new ValueConstraintExtension();

	/**
	 * Maps constraint types to descriptors.
	 */
	private final Map<Class<?>, ValueConstraintFactoryDescriptor> typeToDescriptor = new HashMap<>();

	/**
	 * Default constructor.
	 */
	private ValueConstraintExtension() {
		super(ID);
	}

	@Override
	protected ValueConstraintFactoryDescriptor create(String id, IConfigurationElement conf) {
		if ("valueconstraint".equals(conf.getName())) {
			try {
				ValueConstraintFactoryDescriptor desc = new ValueConstraintFactoryDescriptor(id,
						conf);
				synchronized (typeToDescriptor) {
					typeToDescriptor.put(desc.getConstraintType(), desc);
				}
				return desc;
			} catch (Exception e) {
				log.error("Could not create value constraint factory with id " + id, e);
				return null;
			}
		}
		else {
			return null;
		}
	}

	@Override
	protected String getIdAttributeName() {
		return "id";
	}

	/**
	 * Get the value constraint factory descriptor applicable for the given
	 * constraint object.
	 * 
	 * @param constraint the constraint object
	 * @return the descriptor or <code>null</code> if none is available
	 */
	public ValueConstraintFactoryDescriptor getForConstraint(Object constraint) {
		if (constraint == null)
			return null;

		Class<?> constraintType = constraint.getClass();
		while (constraintType != null) {
			ValueConstraintFactoryDescriptor desc = getForType(constraintType);
			if (desc != null) {
				return desc;
			}

			// check superclass
			constraintType = constraintType.getSuperclass();
		}

		return null;
	}

	/**
	 * Get the value constraint descriptor directly associated to the given
	 * constraint type.
	 * 
	 * @param constraintType the constraint type
	 * @return the descriptor or <code>null</code> if none is registered for the
	 *         constraint type
	 */
	private ValueConstraintFactoryDescriptor getForType(Class<?> constraintType) {
		synchronized (typeToDescriptor) {
			ValueConstraintFactoryDescriptor desc = typeToDescriptor.get(constraintType);
			if (desc != null)
				return desc;
		}

		// look for eventually not yet loaded definitions with the type
		for (ValueConstraintFactoryDescriptor desc : getElements()) {
			if (constraintType.equals(desc.getConstraintType())) {
				return desc;
			}
		}

		return null;
	}

	@Override
	public ValueConstraintFactoryDescriptor get(String id) {
		ValueConstraintFactoryDescriptor result = super.get(id);
		if (result == null) {
			// try to lookup alias
			Alias alias = AliasExtension.INSTANCE.get(id);
			if (alias != null) {
				result = super.get(alias.getRef());
			}
		}
		return result;
	}

}
