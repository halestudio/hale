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
 * that takes a {@link Definition} as an argument that will create
 * the constraint with its default settings as this will be used when a
 * constraint of that type is requested for a definition where it does not
 * exist. If both a default and a {@link Definition} constructor are available
 * the constructor that takes a definition is preferred if possible.
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
	 * constructor, as {@link ConstraintUtil#getDefaultConstraint(Class, Definition)}
	 * will only be able to cache it in this case.
	 * 
	 * @return if the default constraint is mutable, 
	 *   <code>true</code> by default
	 */
	boolean mutable() default true;
	
}
