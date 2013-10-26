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

package eu.esdihumboldt.hale.ui.util.groovy.internal;

/**
 * TODO Type description
 * 
 * @author simon
 */
public interface GroovyPartitions {

	/**
	 * The identifier of the Java partitioning.
	 */
	String JAVA_PARTITIONING = "___java_partitioning";

	/**
	 * The identifier of the single-line (JLS2: EndOfLineComment) end comment
	 * partition content type.
	 */
	String JAVA_SINGLE_LINE_COMMENT = "__java_singleline_comment";

	/**
	 * The identifier multi-line (JLS2: TraditionalComment) comment partition
	 * content type.
	 */
	String JAVA_MULTI_LINE_COMMENT = "__java_multiline_comment";

	/**
	 * The identifier of the Javadoc (JLS2: DocumentationComment) partition
	 * content type.
	 */
	String JAVA_DOC = "__java_javadoc";

	/**
	 * The identifier of the Java string partition content type.
	 */
	String JAVA_STRING = "__java_string";

	/**
	 * The identifier of the Java character partition content type.
	 */
	String JAVA_CHARACTER = "__java_character";

}
