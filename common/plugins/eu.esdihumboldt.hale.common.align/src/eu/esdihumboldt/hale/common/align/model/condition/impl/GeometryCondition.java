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

import java.util.Collection;
import java.util.HashSet;

import com.vividsolutions.jts.geom.Geometry;

import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.align.model.condition.EntityCondition;
import eu.esdihumboldt.hale.common.align.model.condition.TypeCondition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.AugmentedValueFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.GeometryType;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;

/**
 * Type condition that checks if it's geometry and optionally for certain
 * geometry types.
 * 
 * @author Simon Templer
 */
public class GeometryCondition implements TypeCondition {

	private final Collection<Class<? extends Geometry>> bindings;
	private final boolean allowConversion;
	private final boolean allowCollection;

	/**
	 * Default constructor
	 */
	public GeometryCondition() {
		this(null, true, true);
	}

	/**
	 * Create a geometry condition that checks for certain geometry types.
	 * 
	 * @param bindings the allowed geometry bindings, <code>null</code> for any
	 * @param allowConversion if conversion is allowed regarding the geometry
	 *            binding check (only applicable if bindings is not
	 *            <code>null</code>)
	 * @param allowCollection if a collection of geometries is allowed regarding
	 *            the geometry binding check (only applicable if bindings is not
	 *            <code>null</code>)
	 */
	public GeometryCondition(Collection<Class<? extends Geometry>> bindings,
			boolean allowConversion, boolean allowCollection) {
		super();
		this.bindings = (bindings == null) ? (null) : (new HashSet<Class<? extends Geometry>>(
				bindings));
		this.allowConversion = allowConversion;
		this.allowCollection = allowCollection;
	}

	/**
	 * @see EntityCondition#accept(Entity)
	 */
	@Override
	public boolean accept(Type entity) {
		TypeDefinition type = entity.getDefinition().getDefinition();
		if (!type.getConstraint(HasValueFlag.class).isEnabled()
				&& !type.getConstraint(AugmentedValueFlag.class).isEnabled()) {
			// only check binding for types that actually may have a value,
			// whether defined in the schema or augmented
			return false;
		}

		GeometryType geometryType = entity.getDefinition().getDefinition()
				.getConstraint(GeometryType.class);

		if (!geometryType.isGeometry()) {
			// is no geometry type
			return false;
		}

		Collection<Class<? extends Geometry>> tmpBindings = bindings;
		if (tmpBindings == null) {
			// check only if it is a geometry
			tmpBindings = new HashSet<Class<? extends Geometry>>();
			tmpBindings.add(Geometry.class);
		}

		// otherwise check the allowed bindings
		boolean to = true; // default
		switch (entity.getDefinition().getSchemaSpace()) {
		case SOURCE:
			to = false;
			break;
		case TARGET:
			to = true;
			break;
		}

		for (Class<? extends Geometry> compatibleClass : tmpBindings) {
			Binding binding = type.getConstraint(Binding.class);
			boolean isCollection = Collection.class.isAssignableFrom(binding.getBinding());

			// check binding
			if (BindingCondition.isCompatibleClass(geometryType.getBinding(), to, compatibleClass,
					allowConversion) && (!isCollection || allowCollection)) {
				return true;
			}
		}

		// no check succeeded
		return false;
	}

}
