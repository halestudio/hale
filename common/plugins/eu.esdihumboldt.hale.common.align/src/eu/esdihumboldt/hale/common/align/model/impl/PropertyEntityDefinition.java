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

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;

import net.jcip.annotations.Immutable;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Entity definition for a property
 * 
 * @author Simon Templer
 */
@Immutable
public class PropertyEntityDefinition extends ChildEntityDefinition {

	/**
	 * Create an entity definition specified by the given property path. The
	 * property path must contain the property definition as last element.
	 * 
	 * @param type the topmost parent of the property
	 * @param path the property path down from the type
	 * @param schemaSpace the schema space identifier
	 * @param filter the entity filter on the type, may be <code>null</code>
	 */
	public PropertyEntityDefinition(TypeDefinition type, List<ChildContext> path,
			SchemaSpaceID schemaSpace, Filter filter) {
		super(type, path, schemaSpace, filter);

		checkArgument(path != null && !path.isEmpty() && path.size() >= 1
				&& path.get(path.size() - 1).getChild() instanceof PropertyDefinition);
	}

	/**
	 * @see EntityDefinition#getDefinition()
	 */
	@Override
	public PropertyDefinition getDefinition() {
		return (PropertyDefinition) super.getDefinition();
	}

}
