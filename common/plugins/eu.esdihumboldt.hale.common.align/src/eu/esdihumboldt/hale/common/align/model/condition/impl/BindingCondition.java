/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.common.align.model.condition.impl;

import net.jcip.annotations.Immutable;

import org.springframework.core.convert.ConversionService;

import de.fhg.igd.osgi.util.OsgiUtils;
import edu.umd.cs.findbugs.annotations.SuppressWarnings;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.align.model.condition.EntityCondition;
import eu.esdihumboldt.hale.common.align.model.condition.TypeCondition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.AugmentedValueFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.ElementType;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;

/**
 * Type condition that checks its binding and element type
 * 
 * @author Simon Templer
 */
@Immutable
@SuppressWarnings(value = "JCIP_FIELD_ISNT_FINAL_IN_IMMUTABLE_CLASS", justification = "FindBugs presents a warning about a switch table not being final")
public class BindingCondition implements TypeCondition {

	private final boolean allowCollection;
	private final boolean allowConversion;
	private final Class<?> compatibleClass;

	/**
	 * Create a binding condition
	 * 
	 * @param compatibleClass the class the binding should be compatible to
	 * @param allowConversion specifies if a binding is classified as compatible
	 *            if conversion to the compatible class is possible
	 * @param allowCollection specifies if a binding is classified as compatible
	 *            if it is a collection of the compatible class
	 */
	public BindingCondition(Class<?> compatibleClass, boolean allowConversion,
			boolean allowCollection) {
		this.compatibleClass = compatibleClass;
		this.allowConversion = allowConversion;
		this.allowCollection = allowCollection;
	}

	/**
	 * @see EntityCondition#accept(Entity)
	 */
	@Override
	public boolean accept(Type entity) {
		boolean to = true; // default
		switch (entity.getDefinition().getSchemaSpace()) {
		case SOURCE:
			to = false;
			break;
		case TARGET:
			to = true;
			break;
		}

		TypeDefinition type = entity.getDefinition().getDefinition();
		if (!type.getConstraint(HasValueFlag.class).isEnabled()
				&& !type.getConstraint(AugmentedValueFlag.class).isEnabled()) {
			// only check binding for types that actually may have a value,
			// whether defined in the schema or augmented
			return false;
		}

		// check binding
		Binding binding = type.getConstraint(Binding.class);
		if (isCompatibleClass(binding.getBinding(), to)) {
			return true;
		}

		// check element type
		if (allowCollection) {
			ElementType elementType = type.getConstraint(ElementType.class);
			if (isCompatibleClass(elementType.getBinding(), to)) {
				return true;
			}
		}

		// no check succeeded
		return false;
	}

	/**
	 * Check if the given binding is compatible to the configured compatible
	 * class
	 * 
	 * @param binding the binding
	 * @param to if a value of {@link #compatibleClass} shall be assigned to the
	 *            binding or vice versa
	 * @return if the binding is compatible
	 */
	protected boolean isCompatibleClass(Class<?> binding, boolean to) {
		return isCompatibleClass(binding, to, compatibleClass, allowConversion);
	}

	/**
	 * Check if the given binding is compatible to the given compatible class
	 * 
	 * @param binding the binding
	 * @param to if a value of the compatible class shall be assigned to the
	 *            binding or vice versa
	 * @param compatibleClass the compatible class
	 * @param allowConversion if conversion is allowed
	 * @return if the binding is compatible
	 */
	public static boolean isCompatibleClass(Class<?> binding, boolean to, Class<?> compatibleClass,
			boolean allowConversion) {
		// check if the classes are compatible by assignment
		if (to) {
			if (binding.isAssignableFrom(compatibleClass)) {
				return true;
			}
		}
		else {
			if (compatibleClass.isAssignableFrom(binding)) {
				return true;
			}
		}

		if (allowConversion) {
			// check if a corresponding conversion is possible
			ConversionService conversionService = OsgiUtils.getService(ConversionService.class);

			if (to) {
				if (conversionService.canConvert(compatibleClass, binding)) {
					return true;
				}
			}
			else {
				if (conversionService.canConvert(binding, compatibleClass)) {
					return true;
				}
			}
		}

		return false;
	}

}
