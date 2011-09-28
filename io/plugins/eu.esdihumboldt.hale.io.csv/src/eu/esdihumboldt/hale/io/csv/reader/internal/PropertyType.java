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

package eu.esdihumboldt.hale.io.csv.reader.internal;

import javax.xml.namespace.QName;

import net.jcip.annotations.Immutable;
import de.cs3d.util.eclipse.extension.simple.IdentifiableExtension.Identifiable;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition;

/**
 * Type class for the property types
 * 
 * @author Kevin Mais
 */
@Immutable
public class PropertyType implements Identifiable {

	private final String id;
	private final String name;
	private final DefaultTypeDefinition td;

	/**
	 * default constructor
	 * 
	 * @param id the id of the property type
	 * @param name the name of the property type
	 * @param binding the binding class of the property type
	 */
	public PropertyType(String id, String name, Class<?> binding) {
		this.id = id;
		this.name = name;
		this.td = new DefaultTypeDefinition(new QName(id));

		td.setConstraint(HasValueFlag.ENABLED);
		td.setConstraint(Binding.get(binding));
	}

	/**
	 * @see de.cs3d.util.eclipse.extension.simple.IdentifiableExtension.Identifiable#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * Getter for the property type name
	 * 
	 * @return the name of the property type
	 */
	public String getName() {
		return name;
	}

	/**
	 * Getter for the type definition
	 * 
	 * @return the TypeDefinition of the property type
	 */
	public TypeDefinition getTypeDefinition() {
		return td;
	}
}
