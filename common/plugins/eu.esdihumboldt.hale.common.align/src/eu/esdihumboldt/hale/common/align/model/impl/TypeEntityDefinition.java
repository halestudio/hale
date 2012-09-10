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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.List;

import net.jcip.annotations.Immutable;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Entity definition for a type
 * 
 * @author Simon Templer
 */
@Immutable
public class TypeEntityDefinition implements EntityDefinition {

	private final SchemaSpaceID schemaSpace;

	private final TypeDefinition typeDefinition;

	private final Filter filter;

	/**
	 * Create an entity definition for the given type
	 * 
	 * @param typeDefinition the type definition
	 * @param schemaSpace the schema space identifier
	 * @param filter the entity filter, may be <code>null</code>
	 */
	public TypeEntityDefinition(TypeDefinition typeDefinition, SchemaSpaceID schemaSpace,
			Filter filter) {
		super();

		checkNotNull(typeDefinition, "Null type definition not allowed for type entity definition");

		this.typeDefinition = typeDefinition;
		this.schemaSpace = schemaSpace;
		this.filter = filter;
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
	public TypeDefinition getDefinition() {
		return typeDefinition;
	}

	/**
	 * @see EntityDefinition#getType()
	 */
	@Override
	public TypeDefinition getType() {
		return typeDefinition;
	}

	/**
	 * @see EntityDefinition#getPropertyPath()
	 */
	@Override
	public List<ChildContext> getPropertyPath() {
		return Collections.emptyList();
	}

	/**
	 * @see EntityDefinition#getFilter()
	 */
	@Override
	public Filter getFilter() {
		return filter;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((filter == null) ? 0 : filter.hashCode());
		result = prime * result + ((schemaSpace == null) ? 0 : schemaSpace.hashCode());
		result = prime * result + ((typeDefinition == null) ? 0 : typeDefinition.hashCode());
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
		TypeEntityDefinition other = (TypeEntityDefinition) obj;
		if (filter == null) {
			if (other.filter != null)
				return false;
		}
		else if (!filter.equals(other.filter))
			return false;
		if (schemaSpace != other.schemaSpace)
			return false;
		if (typeDefinition == null) {
			if (other.typeDefinition != null)
				return false;
		}
		else if (!typeDefinition.equals(other.typeDefinition))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return typeDefinition.toString();
	}

}
