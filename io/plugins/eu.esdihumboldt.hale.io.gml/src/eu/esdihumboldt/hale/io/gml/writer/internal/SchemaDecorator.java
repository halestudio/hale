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
