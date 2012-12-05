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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.gml.writer;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;

/**
 * Augmenting interface for I/O providers writing XML according to the target
 * schema.
 * 
 * @author Simon Templer
 */
public interface XmlWriterBase extends IOProvider {

	/**
	 * The parameter name for the XML root element name
	 */
	public static final String PARAM_ROOT_ELEMENT_NAME = "xml.rootElement.name";

	/**
	 * The parameter name for the XML root element namespace
	 */
	public static final String PARAM_ROOT_ELEMENT_NAMESPACE = "xml.rootElement.namespace";

	/**
	 * Get the target schema.
	 * 
	 * @return the target schema
	 */
	public SchemaSpace getTargetSchema();

}
