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

package eu.esdihumboldt.util.blueprints.entities;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.codehaus.groovy.transform.GroovyASTTransformationClass;

/**
 * Groovy classes with this annotation are transformed to beans wrapping a
 * blueprints vertex.
 * 
 * @author Simon Templer
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.TYPE })
@GroovyASTTransformationClass({ "eu.esdihumboldt.util.blueprints.entities.VertexEntityTransformation" })
public @interface VertexEntity {

	/**
	 * @return the vertex class name
	 */
	String value();
}
