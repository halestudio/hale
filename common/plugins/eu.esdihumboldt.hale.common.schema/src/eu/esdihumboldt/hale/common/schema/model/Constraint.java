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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import eu.esdihumboldt.hale.common.schema.model.constraint.ConstraintUtil;

/**
 * Marks constraint types.<br>
 * <br>
 * Types using this annotation must have a default constructor or a constructor
 * that takes a {@link Definition} as an argument that will create the
 * constraint with its default settings as this will be used when a constraint
 * of that type is requested for a definition where it does not exist. If both a
 * default and a {@link Definition} constructor are available the constructor
 * that takes a definition is preferred if possible.
 * 
 * @see Definition#getConstraint(Class)
 * @see ConstraintUtil#getDefaultConstraint(Class, Definition)
 * 
 * @author Simon Templer
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Constraint {

	/**
	 * States if the default constraint is mutable. If possible the default
	 * constraint should not be mutable if it is based on the default
	 * constructor, as
	 * {@link ConstraintUtil#getDefaultConstraint(Class, Definition)} will only
	 * be able to cache it in this case.
	 * 
	 * @return if the default constraint is mutable, <code>true</code> by
	 *         default
	 */
	boolean mutable() default true;

}
