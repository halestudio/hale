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

package eu.esdihumboldt.hale.common.align.model.impl;

import java.util.Collections;
import java.util.List;

import net.jcip.annotations.Immutable;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Entity definition for a property or group property
 * @author Simon Templer
 */
@Immutable
public class ChildEntityDefinition implements EntityDefinition {

	private final TypeDefinition type;
	
	private final List<ChildContext> path;
	
	private final SchemaSpaceID schemaSpace;
	
	/**
	 * Create an entity definition specified by the given child path.
	 * @param type the topmost parent of the property
	 * @param path the child path down from the type
	 * @param schemaSpace the schema space identifier
	 */
	public ChildEntityDefinition(TypeDefinition type,
			List<ChildContext> path, SchemaSpaceID schemaSpace) {
		super();
		
		this.type = type;
		this.path = Collections.unmodifiableList(path);
		this.schemaSpace = schemaSpace;
	}

	/**
	 * @see EntityDefinition#getSchemaSpace()
	 */
	@Override
	public SchemaSpaceID getSchemaSpace() {
		return schemaSpace;
	}

	/**
	 * @see EntityDefinition#getDefinition()
	 */
	@Override
	public ChildDefinition<?> getDefinition() {
		return path.get(path.size() -1).getChild();
	}

	/**
	 * @see EntityDefinition#getType()
	 */
	@Override
	public TypeDefinition getType() {
		return type;
	}

	/**
	 * @see EntityDefinition#getPropertyPath()
	 */
	@Override
	public List<ChildContext> getPropertyPath() {
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
		result = prime * result
				+ ((schemaSpace == null) ? 0 : schemaSpace.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		ChildEntityDefinition other = (ChildEntityDefinition) obj;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (schemaSpace != other.schemaSpace)
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer(getType().toString());
		for (ChildContext childContext : getPropertyPath()) {
			result.append('/');
			result.append(childContext.getChild().getName().getLocalPart());
		}
		return result.toString();
	}

}
