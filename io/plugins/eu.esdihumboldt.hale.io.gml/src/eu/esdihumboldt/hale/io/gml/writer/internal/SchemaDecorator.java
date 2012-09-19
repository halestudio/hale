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

package eu.esdihumboldt.hale.io.gml.writer.internal;

import java.net.URI;
import java.util.Collection;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Schema decorator.
 * 
 * @author Simon Templer
 */
public abstract class SchemaDecorator implements Schema {

	private final Schema schema;

	/**
	 * Create a schema decorator.
	 * 
	 * @param schema the decoratee
	 */
	public SchemaDecorator(Schema schema) {
		super();
		this.schema = schema;
	}

	@Override
	public URI getLocation() {
		return schema.getLocation();
	}

	@Override
	public String getNamespace() {
		return schema.getNamespace();
	}

	@Override
	public Collection<? extends TypeDefinition> getTypes() {
		return schema.getTypes();
	}

	@Override
	public TypeDefinition getType(QName name) {
		return schema.getType(name);
	}

	@Override
	public Collection<? extends TypeDefinition> getMappingRelevantTypes() {
		return schema.getMappingRelevantTypes();
	}

	@Override
	public void toggleMappingRelevant(Collection<? extends TypeDefinition> types) {
		schema.toggleMappingRelevant(types);
	}
}
