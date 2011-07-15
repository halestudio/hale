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

package eu.esdihumboldt.hale.align.model.impl;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Collections;
import java.util.List;

import net.jcip.annotations.Immutable;
import eu.esdihumboldt.hale.align.model.EntityDefinition;
import eu.esdihumboldt.hale.schema.model.Definition;
import eu.esdihumboldt.hale.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.schema.model.TypeDefinition;

/**
 * Entity definition for a property
 * @author Simon Templer
 */
@Immutable
public class PropertyEntityDefinition implements EntityDefinition {

	private final List<Definition<?>> path;
	
	/**
	 * Create an entity definition specified by the given property path. The 
	 * property path must contain the property definition as last element,
	 * the first element must be a type definition
	 * @param path the property path
	 */
	public PropertyEntityDefinition(List<Definition<?>> path) {
		super();
		
		checkArgument(path != null && !path.isEmpty() && path.size() >= 2 && 
				path.get(path.size() - 1) instanceof PropertyDefinition &&
				path.get(0) instanceof TypeDefinition);
		
		this.path = Collections.unmodifiableList(path);
	}

	/**
	 * @see EntityDefinition#getDefinition()
	 */
	@Override
	public PropertyDefinition getDefinition() {
		return (PropertyDefinition) path.get(path.size() -1);
	}

	/**
	 * @see EntityDefinition#getPath()
	 */
	@Override
	public List<? extends Definition<?>> getPath() {
		return path;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PropertyEntityDefinition other = (PropertyEntityDefinition) obj;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}

}
