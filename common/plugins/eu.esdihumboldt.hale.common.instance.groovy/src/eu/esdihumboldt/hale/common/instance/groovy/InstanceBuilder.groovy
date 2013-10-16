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

package eu.esdihumboldt.hale.common.instance.groovy

import javax.xml.namespace.QName

import eu.esdihumboldt.hale.common.instance.model.DataSet
import eu.esdihumboldt.hale.common.instance.model.Instance
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection
import eu.esdihumboldt.hale.common.instance.model.InstanceFactory
import eu.esdihumboldt.hale.common.instance.model.MutableGroup
import eu.esdihumboldt.hale.common.instance.model.MutableInstance
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultGroup
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstance
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstanceCollection
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.hale.common.schema.model.TypeIndex
import eu.esdihumboldt.util.groovy.builder.BuilderBase
import groovy.transform.CompileStatic


/**
 * Instance builder.
 * 
 * @author Simon Templer
 */
@CompileStatic
class InstanceBuilder extends BuilderBase {

	/**
	 * Specifies if namespace may be ignored.
	 */
	boolean ignoreNamespace = true

	/**
	 * Specifies the data set to associated with the create instances.
	 */
	DataSet dataSet = null

	/**
	 * Custom instance factory.
	 */
	InstanceFactory instanceFactory = null

	/**
	 * The type index to search for types to instantiate.
	 */
	private TypeIndex types = null

	@Override
	public void reset() {
		super.reset()
		types = null
	}

	/**
	 * Build an instance collection, then reset the builder.
	 * 
	 * @param types the underlying type index if the builder should be schema strict
	 * @return the created instance collection
	 */
	InstanceCollection collection(TypeIndex types = null, Closure closure) {
		DefaultInstanceCollection root = new DefaultInstanceCollection()
		this.types = types
		def parent = current
		current = root
		closure = (Closure) closure.clone()
		closure.delegate = this
		closure.call()
		current = parent
		reset()
		return root
	}

	@Override
	def createNode(String name, Map attributes, List params, Object parent,
			boolean subClosure) {
		Object node = null

		if (parent == null) {
			// create instance
			Instance instance = createInstance(name, attributes, params)
			node = instance
		}
		else if (parent instanceof DefaultInstanceCollection) {
			// create instance and add to collection
			Instance instance = createInstance(name, attributes, params)
			((DefaultInstanceCollection) parent).add(instance)
			node = instance
		}
		else if (parent instanceof MutableGroup) {
			// create property value
			// and add property to group
			def value = addProperty((MutableGroup) parent, name, attributes, params, subClosure)
			node = value
		}
		else {
			// TODO throw some kind of exception?
			throw new IllegalStateException()
		}

		node
	}

	private def addProperty(MutableGroup parent, String name, Map attributes, List params, boolean subClosure) {
		QName propertyName = createName(name, attributes)

		if (parent.definition == null) {
			// not schema bound, just add the property
			def value = createPropertyValue(null, attributes, params, subClosure)
			parent.addProperty(propertyName, value)
			return value
		}
		else {
			//TODO find the property path
		}
	}

	private def createPropertyValue(PropertyDefinition property, Map attributes, List params, boolean subClosure) {
		if (property == null) {
			// schemaless mode

			/*
			 * 1. Determine if a type definition or definition group is set explicitly
			 */
			DefinitionGroup definition = null
			if (params && params[0] instanceof DefinitionGroup) {
				definition = (DefinitionGroup) params[0]
			}

			if (definition instanceof TypeDefinition) {
				// create an explicit instance
				return createInstance('', attributes, params)
			}

			if (definition != null) {
				// create a group
				return new DefaultGroup(definition)
			}

			/*
			 * 2. Schemaless defaults
			 */
			if (subClosure) {
				// must be a group or instance
				if (params) {
					// must be an instance as a value is given
					return createInstance(null, params[0])
				}
				else {
					// create a group
					return new DefaultGroup((DefinitionGroup) null)
				}
			}
			else {
				// just a value
				if (params) {
					// only one value supported at once
					//TODO warn if more than one param?
					return params[0]
				}
				else {
					/*
					 * XXX what is the best behavior here?
					 * Use a null value or fail?
					 * For now use a null value
					 */
					return null
				}
			}
		}
		else {
			// schema mode

			//TODO
		}
	}

	private Instance createInstance(String name, Map attributes, List params) {
		/*
		 * 1. determine instance type (if any)
		 */
		TypeDefinition type = null

		int paramIndex = 0
		// type may be explicitly defined as parameter
		// (the given name  basically is ignored)
		if (params && params[paramIndex] instanceof TypeDefinition) {
			type = (TypeDefinition) params[paramIndex++]
		}

		if (!type && types) {
			// try to retrieve type from type index
			QName typeName = createName(name, attributes)
			type = types.getType(typeName)

			if (!type && ignoreNamespace) {
				// try to find type with matching local name
				type = types.types.find { TypeDefinition candidate ->
					// test candidate for matching local name
					typeName.localPart == candidate.name.localPart
				}
			}
		}

		/*
		 * 2. determine instance value (if any)
		 */
		def value = null
		if (params && params.size() >= paramIndex + 1) {
			value = params[paramIndex++]
		}

		/*
		 * 3. build instance
		 */
		createInstance(type, value)
	}

	private MutableInstance createInstance(TypeDefinition type, def value = null) {
		/*
		 * 1. build instance
		 */
		MutableInstance instance
		if (instanceFactory) {
			instance = instanceFactory.createInstance(type)
			instance.setDataSet(dataSet)
		}
		else {
			instance = new DefaultInstance(type, dataSet)
		}

		/*
		 * 2. set instance value
		 */
		//TODO do validation/conversion/sanity check?
		instance.setValue(value)

		instance
	}

	/**
	 * Create a qualified name from a given name and attribute map.
	 * @param name the local name
	 * @param attributes the map that may contain a namespace, string keys
	 *   <code>namespace</code> and <code>ns</code> are supported for providing
	 *   a namespace
	 * @return the qualified name
	 */
	private QName createName(String name, Map attributes) {
		String ns = null
		if (attributes) {
			// use specified namespace
			// empty namespace allowed (to override default namespace)
			if (attributes['namespace'] != null) {
				ns = attributes['namespace']
			}
			else if (attributes['ns'] != null) {
				ns = attributes['ns']
			}
		}

		if (ns) {
			new QName(ns, name)
		}
		else {
			new QName(name)
		}
	}
}
