/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
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

	/**
	 * Get the constraints that are explicitly set on the definition (i.e.
	 * inherited and default constraints are not included). This method is
	 * intended for use when persisting the schema model. To obtain information
	 * on the constraints of a definition, use {@link #getConstraint(Class)}
	 * instead.
	 * 
	 * @return the collection of constraints explicitly set for this definition
	 */
	public Iterable<C> getExplicitConstraints();

}
