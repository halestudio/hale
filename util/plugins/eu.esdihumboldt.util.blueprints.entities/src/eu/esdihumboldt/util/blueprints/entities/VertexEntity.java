/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.util.blueprints.entities;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.codehaus.groovy.transform.GroovyASTTransformationClass;

/**
 * TODO Type description
 * 
 * @author Simon Templer
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.TYPE })
@GroovyASTTransformationClass({ "eu.esdihumboldt.util.orient.entities.ODocumentEntityTransformation" })
public @interface VertexEntity {

	/**
	 * @return the vertex class name
	 */
	String value();
}
