/*
 * Copyright (c) 2023 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.json.internal.schema;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.core.report.SimpleLog;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Create type definitions from information collected on different object types
 * encountered in Json.
 * 
 * @author Simon Templer
 */
public class JsonTypes {

	private final Map<QName, JsonType> types = new HashMap<>();

	private final SimpleLog log;

	/**
	 * @param log the log to use
	 */
	public JsonTypes(SimpleLog log) {
		super();
		this.log = log;
	}

	/**
	 * Get or create the type of the given name.
	 * 
	 * @param name the type name
	 * @return the representation of the Json type
	 */
	public JsonType getType(QName name) {
		return types.computeIfAbsent(name, n -> new JsonType(n, log));
	}

	/**
	 * Build the type definitions from the collected information.
	 * 
	 * @param context the schema building context
	 * 
	 * @return the type definitions
	 */
	public Iterable<TypeDefinition> buildTypes(SchemaBuilderContext context) {
		return types.values().stream().map(t -> t.buildType(context)).collect(Collectors.toList());
	}

}
