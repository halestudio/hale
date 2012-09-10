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

package eu.esdihumboldt.hale.io.csv.propertytype;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition;
import eu.esdihumboldt.hale.io.csv.PropertyType;

/**
 * Abstract class for property types
 * 
 * @author Kevin Mais
 */
public abstract class AbstractPropertyType implements PropertyType {

	private DefaultTypeDefinition td;

	/**
	 * Default Constructor
	 * 
	 * @param binding the given binding
	 */
	public AbstractPropertyType(Class<?> binding) {
		this.td = new DefaultTypeDefinition(new QName(binding.getName()));

		td.setConstraint(HasValueFlag.ENABLED);
		td.setConstraint(Binding.get(binding));
	}

	/**
	 * @see eu.esdihumboldt.hale.io.csv.PropertyType#getTypeDefinition()
	 */
	@Override
	public TypeDefinition getTypeDefinition() {
		return td;
	}

}
