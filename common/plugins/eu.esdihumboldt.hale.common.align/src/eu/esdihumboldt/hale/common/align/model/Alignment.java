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

package eu.esdihumboldt.hale.common.align.model;

import java.util.Collection;

import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * An alignment contains alignment cells
 * 
 * @author Simon Templer
 */
public interface Alignment {

	/**
	 * Get the collection of cells contained in the alignment.
	 * 
	 * @return the alignment cells
	 */
	public Collection<? extends Cell> getCells();

	/**
	 * Get the cells representing a mapping between types
	 * 
	 * @return the type cells
	 */
	public Collection<? extends Cell> getTypeCells();

	/**
	 * Get the cells associated w/ the given entity definition.
	 * 
	 * @param entityDefinition the entity definition
	 * @return the associated cells or an empty collection FIXME what about
	 *         cells defined on super types?
	 */
	public Collection<? extends Cell> getCells(EntityDefinition entityDefinition);

	/**
	 * Get the cells associated with the given type. These may be cells
	 * associated to the type or its properties.
	 * 
	 * @param type the type definition
	 * @param schemaSpace the type schema space
	 * @return the cells associated with the given type
	 */
	public Collection<? extends Cell> getCells(TypeDefinition type, SchemaSpaceID schemaSpace);

	/**
	 * Get the cells representing a mapping between properties that are
	 * associated with the given source and target types. Augmentations for the
	 * given target type will also be returned.
	 * 
	 * @param sourceTypes the source types or <code>null</code> for any source
	 *            type
	 * @param targetType the target types
	 * @return the property cells associated with the relation between the given
	 *         source and target types.
	 */
	public Collection<? extends Cell> getPropertyCells(Iterable<TypeEntityDefinition> sourceTypes,
			TypeEntityDefinition targetType);

}
