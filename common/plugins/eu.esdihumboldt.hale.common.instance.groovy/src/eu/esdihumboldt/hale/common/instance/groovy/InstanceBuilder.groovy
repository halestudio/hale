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

import org.springframework.core.convert.ConversionException

import de.fhg.igd.slf4jplus.ALogger
import de.fhg.igd.slf4jplus.ALoggerFactory
import eu.esdihumboldt.hale.common.convert.ConversionUtil
import eu.esdihumboldt.hale.common.instance.model.DataSet
import eu.esdihumboldt.hale.common.instance.model.Instance
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection
import eu.esdihumboldt.hale.common.instance.model.InstanceFactory
import eu.esdihumboldt.hale.common.instance.model.MutableGroup
import eu.esdihumboldt.hale.common.instance.model.MutableInstance
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultGroup
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstance
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstanceCollection
import eu.esdihumboldt.hale.common.schema.groovy.DefinitionAccessor
import eu.esdihumboldt.hale.common.schema.model.Definition
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.hale.common.schema.model.TypeIndex
import eu.esdihumboldt.hale.common.schema.model.constraint.type.AugmentedValueFlag
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag
import eu.esdihumboldt.util.groovy.builder.BuilderBase
import eu.esdihumboldt.util.groovy.paths.Path
import groovy.transform.CompileStatic


/**
 * Instance builder.
 * 
 * @author Simon Templer
 */
@CompileStatic
class InstanceBuilder extends BuilderBase {

	private static final ALogger log = ALoggerFactory.getLogger(InstanceBuilder)

	/**
	 * Specifies if namespace may be ignored.
	 */
	boolean ignoreNamespace = true

	/**
	 * If in schema bound mode the builder should fail if it detects violated
	 * {@link HasValueFlag} or {@link AugmentedValueFlag} constraints.
	 */
	boolean strictValueFlags = true

	/**
	 * If in schema bound mode the builder should fail if it detects a violated
	 * {@link Binding} constraint. If {@link #autoConvert} is set to
	 * <code>true</code> the builder may try to perform a conversion first.
	 */
	boolean strictBinding = true

	/**
	 * If in schema bound mode the builder should try to automatically convert
	 * property and instance values. 
	 */
	boolean autoConvert = true

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
	TypeIndex types = null

	public InstanceBuilder(boolean cloneClosures = true) {
		super(cloneClosures)
	}

	@Override
	public void reset() {
		super.reset()
	}

	/**
	 * Build an instance collection, then reset the builder.
	 * 
	 * @param types the underlying type index if the builder should be schema strict
	 * @return the created instance collection
	 */
	InstanceCollection createCollection(Closure closure) {
		DefaultInstanceCollection root = new DefaultInstanceCollection()
		def parent = current
		current = root
		if (cloneClosures)
			closure = (Closure) closure.clone()
		closure.delegate = this
		closure.call()
		reset()
		return root
	}

	/**
	 * Create an instance. Type safe alternative to generic builder call.
	 *
	 * @param type the type to associate to the instance
	 * @return the created instance
	 */
	Instance createInstance(TypeDefinition type, def value = null, Closure closure) {
		createInstance(type.getName().getLocalPart(), null, value, type, closure)
	}

	/**
	 * Create an instance. Type safe alternative to generic builder call.
	 * 
	 * @param typeLocalName the local name of the instance type
	 * @param namespace the namespace of the instance type
	 * @return the created instance
	 */
	Instance createInstance(String typeLocalName = null, String namespace = null, def value = null, TypeDefinition type = null, Closure closure) {
		def args = []
		if (type != null) {
			args << type
		}
		if (value) {
			args << value
		}
		if (namespace) {
			args << [namespace: namespace]
		}
		if (closure != null) {
			args << closure
		}
		return (Instance) createNode(typeLocalName, args)
	}

	/**
	 * Create an property. Type safe alternative to generic builder call.
	 *
	 * @param name the local name of the property
	 * @param namespace the namespace of the property
	 * @param value the property value
	 * @return the created property
	 */
	def createProperty(String name, def value, Closure closure) {
		createProperty(name, null, value, closure)
	}

	/**
	 * Create an property. Type safe alternative to generic builder call.
	 *
	 * @param name the local name of the property
	 * @param namespace the namespace of the property
	 * @param value the property value
	 * @return the created property
	 */
	def createProperty(String name, String namespace = null, def value, Closure closure = null) {
		def args = []
		if (namespace) {
			args << [namespace: namespace]
		}
		args << value
		if (closure != null) {
			args << closure
		}
		return createNode(name, args)
	}

	@Override
	protected def internalCreateNode(String name, Map attributes, List params, Object parent,
			boolean subClosure) {
		Object node = null

		if (parent == null) {
			// create instance
			Instance instance = internalCreateInstance(name, attributes, params)
			node = instance
		}
		else if (parent instanceof DefaultInstanceCollection) {
			// create instance and add to collection
			Instance instance = internalCreateInstance(name, attributes, params)
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

	/**
	 * Add a property to the given parent group.
	 * 
	 * @param parent the parent group or instance
	 * @param name the local name of the property
	 * @param attributes the supplied named parameters
	 * @param params the supplied unnamed parameters
	 * @param subClosure if the property has a closure defining sub-properties
	 * @return the property value, may be a {@link MutableGroup}, {@link MutableInstance} or other object
	 */
	private def addProperty(MutableGroup parent, String name, Map attributes, List params, boolean subClosure) {
		QName propertyName = createName(name, attributes)

		if (parent.definition == null) {
			// not schema bound, just add the property
			def value = createPropertyValue(null, attributes, params, subClosure)
			parent.addProperty(propertyName, value)
			return value
		}
		else {
			// find the property path
			if (!(parent.definition instanceof Definition)) {
				throw new IllegalStateException('Unsupported definition in parent group/instance')
			}
			Definition parentDef = (Definition) parent.definition
			Path<Definition> defs = findPath(parentDef, propertyName)

			// create group/property structure
			MutableGroup parentGroup = parent
			def result = null
			boolean resultSet = false
			defs.elements.eachWithIndex { Definition element, int index ->
				if (index > 0 && element instanceof GroupPropertyDefinition) {
					// add group to parent
					MutableGroup childGroup = new DefaultGroup((GroupPropertyDefinition) element)
					parentGroup.addProperty(element.getName(), childGroup);
					parentGroup = childGroup

					if (index == defs.elements.size() - 1) {
						// group is last element in path
						result = childGroup
						resultSet = true
					}
				}
				else if (element instanceof PropertyDefinition) {
					// check position - must be last
					if (index != defs.elements.size() - 1) {
						throw new IllegalStateException('Property definitions may only be present at the end of the definition path')
					}

					// add property value to parent group
					def value = createPropertyValue((PropertyDefinition) element, attributes, params, subClosure)
					parentGroup.addProperty(element.getName(), value)
					result = value
					resultSet = true
				}
				else {
					if (index != 0) {
						throw new IllegalStateException('Illegal element encountered in definition path')
					}
					// parent type/group at first index is ignored
				}
			}

			if (!resultSet) {
				throw new IllegalStateException("Property could not be added: $propertyName")
			}

			return result
		}
	}

	/**
	 * Try to find the unique path to a given property.
	 * 
	 * @param parentDef the parent definition
	 * @param propertyName the qualified name of the property 
	 * @return the definition path
	 * @throws IllegalStateException if the path cannot be found or if it is not unique
	 */
	private Path<Definition> findPath(Definition parentDef, QName propertyName) {
		/*
		 * 1. try to find exact match (including namespace)
		 */
		Path<Definition> result =  new DefinitionAccessor(parentDef).findChildren("$propertyName.localPart", propertyName.namespaceURI).eval()

		if (ignoreNamespace && result == null) {
			/*
			 * 2. try w/o namespace
			 */
			result = new DefinitionAccessor(parentDef).findChildren("$propertyName.localPart").eval()
		}

		if (result == null) {
			throw new IllegalStateException("Property $propertyName not found in $parentDef.name")
		}

		result
	}

	/**
	 * Create a property value.
	 * 
	 * @param property the property definition or <code>null</code> if schema less
	 * @param attributes the supplied named parameters
	 * @param params the supplied unnamed parameters
	 * @param subClosure if the property has a closure defining sub-properties
	 * @return the property value
	 */
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
				return internalCreateInstance('', attributes, params)
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
					return createInstanceAndValue(null, params[0])
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

			if (subClosure || property.propertyType.children) {
				// force instance if there is a sub-closure
				// must be an instance if the property type defines children

				// determine instance value (if any)
				def value = null
				if (params) {
					value = params[0]
				}

				if (value instanceof Instance) {
					// allow providing an instance as value
					return value
				}
				else {
					// create instance
					return createInstanceAndValue(property.propertyType, value)
				}
			}
			else {
				// normal property value
				if (params) {
					// only one value supported at once
					//TODO warn if more than one param?
					return processValue(params[0], property.propertyType)
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
	}

	/**
	 * Process a value that is to be used either as an instance value or a 
	 * direct property value. If a type definition is given the value may
	 * be checked and converted accordingly.
	 * 
	 * @param value the value
	 * @param type the type of the property/instance, may be <code>null</code>
	 * @return the value to use
	 */
	private def processValue(def value, TypeDefinition type) {
		if (value == null) {
			// can't do anything with null values
			return null
		}

		if (type != null) {
			// check value flag constraints
			if (strictValueFlags) {
				if (!type.getConstraint(HasValueFlag).enabled && !type.getConstraint(AugmentedValueFlag).enabled) {
					throw new IllegalStateException("No value allowed for type $type.name")
				}
			}

			// auto conversion and binding check
			if (type.getConstraint(HasValueFlag).enabled) {
				// binding only relevant with HasValue
				Class binding = type.getConstraint(Binding).binding

				if (autoConvert) {
					// auto-conversion
					try {
						value = ConversionUtil.getAs(value, binding)
					} catch (ConversionException e) {
						log.warn('Failed to auto-convert property or instance value', e)
					}
				}

				// binding check
				if (strictBinding && value != null) {
					if (!binding.isAssignableFrom(value.class)) {
						throw new IllegalStateException("Value of type ${value.class} not compatible to binding for type $type.name")
					}
				}
			}
		}

		value
	}

	private Instance internalCreateInstance(String name, Map attributes, List params) {
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
		createInstanceAndValue(type, value)
	}

	private MutableInstance createInstanceAndValue(TypeDefinition type, def value = null) {
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
		instance.setValue(processValue(value, type))

		instance
	}

	/**
	 * Create a qualified name from a given name and attribute map.
	 * 
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
