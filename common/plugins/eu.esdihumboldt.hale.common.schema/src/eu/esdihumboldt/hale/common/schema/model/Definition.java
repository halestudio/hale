/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.common.schema.model;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.core.io.supplier.Locatable;

/**
 * Common interface for type and property definitions
 * 
 * @param <C> the supported constraint type
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public interface Definition<C> extends Locatable, Comparable<Definition<?>> {

	/**
	 * Get the definitions identifier
	 * 
	 * @return the unique name of the definition
	 */
	public String getIdentifier();

	/**
	 * Get the definition's display name
	 * 
	 * @return the display name
	 */
	public String getDisplayName();

	/**
	 * Get the definition's qualified name
	 * 
	 * @return the qualified name
	 */
	public QName getName();

	/**
	 * Get the definition description
	 * 
	 * @return the description string or <code>null</code>
	 */
	public String getDescription();

	/**
	 * Get the constraint with the given constraint type.<br>
	 * Should usually not be called while creating the model, exceptions can be
	 * getting mutable constraints where this is intended.
	 * 
	 * @param <T> the constraint type
	 * 
	 * @param constraintType the constraint type, see {@link Constraint}
	 * @return the constraint with the given type
	 */
	public <T extends C> T getConstraint(Class<T> constraintType);

}
