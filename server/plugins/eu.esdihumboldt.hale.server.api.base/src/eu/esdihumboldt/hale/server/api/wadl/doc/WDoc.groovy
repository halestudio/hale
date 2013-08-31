/*
 * Copyright (c) 2013 Simon Templer
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
 *     Simon Templer - initial version
 */

package eu.esdihumboldt.hale.server.api.wadl.doc;

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * WADL documentation annotation.
 * 
 * @author Simon Templer
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface WDoc {
	/**
	 * The documentation title.
	 */
	String title() default ''
	/**
	 * The closure describing the documentation content.
	 * XXX
	 */
	Class content() default {}
	/**
	 * The documentation language to be used in the xml:lang attribute.
	 */
	String lang() default ''
	/**
	 * The associated documentation scope.
	 */
	DocScope scope() default DocScope.METHOD
	/**
	 * The scope context, e.g. a parameter name or representation type
	 */
	String context() default ''
}
