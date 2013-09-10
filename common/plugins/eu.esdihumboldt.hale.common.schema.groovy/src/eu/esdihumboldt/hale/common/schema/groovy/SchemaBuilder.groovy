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

package eu.esdihumboldt.hale.common.schema.groovy

import javax.xml.namespace.QName

import org.codehaus.groovy.runtime.InvokerHelper

import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition
import eu.esdihumboldt.hale.common.schema.model.Schema
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultPropertyDefinition
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultSchema
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeIndex
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import groovy.transform.TypeCheckingMode



/**
 * Builder to easily create schemas.
 * 
 * @author Simon Templer
 */
@CompileStatic
class SchemaBuilder {

	/**
	 * The current node.
	 */
	def current

	/**
	 * The default namespace
	 */
	String defaultNamespace

	/**
	 * Build a schema.
	 * 
	 * @param namespace the schema namespace and default namespace of added
	 *   types and properties
	 * @param location the schema location or <code>null</code>
	 * @return
	 */
	Schema schema(String namespace, URI location = null, Closure closure) {
		def root = new DefaultSchema(namespace, location);
		defaultNamespace = namespace
		def parent = current
		current = root
		closure = (Closure) closure.clone()
		closure.delegate = this
		closure.call()
		current = parent
		return root
	}

	// def types

	// def type

	/**
	 * Called on for any missing method.
	 * 
	 * @param name the method name
	 * @param args the arguments
	 * @return something
	 */
	@CompileStatic(TypeCheckingMode.SKIP)
	@TypeChecked
	def methodMissing(String name, def args) {
		List list = InvokerHelper.asList(args)

		// determine named parameters (must be first)
		Map attributes = null
		def start = 0
		if (list && list[0] instanceof Map) {
			attributes = (Map) list[0]
			start = 1
		}

		// determine closure (must be last)
		def end = list.size()
		Closure closure = null
		if (list && list.last() instanceof Closure) {
			closure = (Closure) list.last().clone()
			closure.delegate = this
			end--
		}

		// determine other parameters
		List params = null
		if (start < end) {
			params = list.subList(start, end)
		}

		def parent = current
		def node = createNode(name, attributes, params, parent)
		current = node

		closure?.call()

		current = parent

		// return the node created by the call
		node
	}

	/**
	 * Create a new node.
	 * 
	 * @param name the node name
	 * @param attributes the named parameters, may be <code>null</code>
	 * @param params other parameters, may be <code>null</code>
	 * @param parent the parent node, may be <code>null</code>
	 * @return the created node
	 */
	def createNode(String name, Map attributes, List params, def parent) {
		def node
		switch (parent) {
			case DefaultTypeIndex:
			// create a type as child
				TypeDefinition type = createType(name, attributes, params)
				((DefaultTypeIndex) parent).addType(type)
				node = type
				break
			case TypeDefinition:
			// create property or group as child
				if (name == '_') {
					// TODO group
				}
				else {
					// create a property
					PropertyDefinition property = createProperty(name, attributes, params,
							(DefinitionGroup) parent)
					node = property
				}
				break
		}

		node
	}

	@CompileStatic(TypeCheckingMode.SKIP)
	QName createName(String name, Map attributes) {
		String ns
		if (attributes && attributes.namespace != null) {
			// use specified namespace
			// empty namespace allowed (to override default namespace)
			ns = attributes.namespace
		}
		else {
			ns = defaultNamespace
		}

		if (ns) {
			new QName(ns, name)
		}
		else {
			new QName(name)
		}
	}

	TypeDefinition createType(String name, Map attributes, List params) {
		QName typeName = createName(name, attributes)
		DefaultTypeDefinition type = new DefaultTypeDefinition(typeName)

		type
	}

	PropertyDefinition createProperty(String name, Map attributes, List params,
			DefinitionGroup parent) {

		// create property type
		//TODO possibly a reference
		//FIXME
		TypeDefinition propertyType = createType(name + 'Type', null, null)

		// create property
		QName propertyName = createName(name, attributes)
		DefaultPropertyDefinition property = new DefaultPropertyDefinition(propertyName, parent,
				propertyType);
	}

}
