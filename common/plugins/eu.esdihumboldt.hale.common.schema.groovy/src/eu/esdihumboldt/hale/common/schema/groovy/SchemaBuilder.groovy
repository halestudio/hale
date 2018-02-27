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

import eu.esdihumboldt.hale.common.schema.groovy.constraints.AugmentedValueFactory
import eu.esdihumboldt.hale.common.schema.groovy.constraints.BindingFactory
import eu.esdihumboldt.hale.common.schema.groovy.constraints.CardinalityFactory
import eu.esdihumboldt.hale.common.schema.groovy.constraints.ChoiceFactory
import eu.esdihumboldt.hale.common.schema.groovy.constraints.ConstraintFactory
import eu.esdihumboldt.hale.common.schema.groovy.constraints.DisplayNameFactory
import eu.esdihumboldt.hale.common.schema.groovy.constraints.EnumerationFactory
import eu.esdihumboldt.hale.common.schema.groovy.constraints.GeometryFactory
import eu.esdihumboldt.hale.common.schema.groovy.constraints.NillableFactory
import eu.esdihumboldt.hale.common.schema.groovy.constraints.TypeAbstractFactory
import eu.esdihumboldt.hale.common.schema.model.Definition
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition
import eu.esdihumboldt.hale.common.schema.model.Schema
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.hale.common.schema.model.TypeIndex
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappableFlag
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappingRelevantFlag
import eu.esdihumboldt.hale.common.schema.model.impl.AbstractDefinition
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultGroupPropertyDefinition
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultPropertyDefinition
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultSchema
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeIndex
import eu.esdihumboldt.util.groovy.builder.BuilderBase
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode



/**
 * Builder to easily create schemas.
 * 
 * @author Simon Templer
 */
@CompileStatic
class SchemaBuilder extends BuilderBase {

	/**
	 * Maps attribute names to constraint factories.
	 * Initialized with default factories.
	 */
	Map<String, ConstraintFactory> constraints = new HashMap<>()

	/**
	 * The default namespace.
	 */
	String defaultNamespace

	/**
	 * The default namespace for default property types.
	 */
	String defaultPropertyTypeNamespace = ''

	/**
	 * The created default property types.
	 */
	private Map<Class, TypeDefinition> defaultTypes = [:]

	/**
	 * Set containing all used default property type names.
	 */
	private Set<String> defaultTypeNames = new HashSet<>()

	/**
	 * Default constructor registering default constraint factories.
	 */
	SchemaBuilder() {
		super()

		/*
		 * NOTE: In Eclipse in the editor there might be errors shown here,
		 * even if the code actually compiles. 
		 */

		constraints.cardinality = CardinalityFactory.instance
		constraints.nillable = NillableFactory.instance
		constraints.display = DisplayNameFactory.instance
		constraints.choice = ChoiceFactory.instance
		constraints.abstract = TypeAbstractFactory.instance
		constraints.augmented = AugmentedValueFactory.instance
		constraints.binding = BindingFactory.instance
		constraints.enum = EnumerationFactory.instance
		constraints.enumeration = EnumerationFactory.instance
		constraints.geometry = GeometryFactory.instance
	}

	/**
	 * Reset the builder
	 */
	void reset() {
		super.reset()
		defaultTypes = [:]
		defaultTypeNames.clear()
	}

	/**
	 * Build a schema, then reset the builder.
	 * 
	 * @param namespace the schema namespace and default namespace of added
	 *   types and properties
	 * @param location the schema location or <code>null</code>
	 * @return the created schema
	 */
	Schema schema(String namespace = '', URI location = null, Closure closure) {
		def root = new DefaultSchema(namespace, location);
		defaultNamespace = namespace
		def parent = current
		current = root
		closure = (Closure) closure.clone()
		closure.delegate = this
		closure.call()
		current = parent
		reset()
		return root
	}

	/**
	 * Build a type index, then reset the builder.
	 * 
	 * @return the created type index
	 */
	TypeIndex types(Closure closure) {
		def root = new DefaultTypeIndex();
		def parent = current
		current = root
		closure = (Closure) closure.clone()
		closure.delegate = this
		closure.call()
		current = parent
		reset()
		return root
	}

	/**
	 * Create a new node.
	 * 
	 * @param name the node name
	 * @param attributes the named parameters, may be <code>null</code>
	 * @param params other parameters, may be <code>null</code>
	 * @param parent the parent node, may be <code>null</code>
	 * @param subClosure states if there is a sub-closure for this node
	 * @return the created node
	 */
	protected def internalCreateNode(String name, Map attributes, List params, def parent, boolean subClosure) {
		def node
		if (parent == null) {
			// create stand-alone type
			TypeDefinition type = createType(name, attributes, params)
			node = type
		}
		else if (parent instanceof DefaultTypeIndex) {
			// create a type as child
			TypeDefinition type = createType(name, attributes, params)
			((DefaultTypeIndex) parent).addType(type)
			node = type
		}
		else if (parent instanceof Definition) {
			DefinitionGroup parentGroup = DefinitionUtil.getDefinitionGroup(parent)

			// create property or group as child
			if (name == '_') {
				// create a group
				GroupPropertyDefinition group = createGroup(attributes, params, parentGroup)
				node = group
			}
			else {
				// create a property
				PropertyDefinition property = createProperty(name, attributes, params,
						parentGroup, subClosure)
				node = property
			}
		}
		else {
			throw new IllegalStateException("${parent.class.name} as parent not supported")
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

		addConstraints(type, attributes, params)

		// named types are by default mappable
		type.setConstraintIfNotSet(MappableFlag.ENABLED)
		// and mapping relevant TODO configure?
		type.setConstraintIfNotSet(MappingRelevantFlag.ENABLED);

		type
	}

	GroupPropertyDefinition createGroup(Map attributes, List params,
			DefinitionGroup parent) {
		QName name = new QName(defaultPropertyTypeNamespace,
				newDefaultPropertyTypeName('group'))
		DefaultGroupPropertyDefinition group = new DefaultGroupPropertyDefinition(
				name, parent, false)

		addConstraints(group, attributes, params)

		group
	}

	PropertyDefinition createProperty(String name, Map attributes, List params,
			DefinitionGroup parent, boolean subClosure) {

		// create property type
		TypeDefinition propertyType

		if (subClosure) {
			// the sub-closure defines an anonymous property type

			// a class specifying the value type may be given as parameter
			Class type = null
			if (params) {
				if (params[0] instanceof Class) {
					type = params[0]
				}
				else {
					//TODO error?
				}
			}
			propertyType = createDefaultNestingPropertyType(type, name)
		}
		else {
			/*
			 * The first parameter must be either
			 * - a class specifying the type of a simple default property
			 * - a TypeDefinition instance
			 * - a QName or string specifying the name of a type that may not be yet defined
			 */
			def type = String // default if nothing given
			if (params) {
				type = params[0]
			}

			if (type instanceof Class) {
				propertyType = getOrCreateDefaultPropertyType((Class) type)
			}
			else if (type instanceof TypeDefinition) {
				propertyType = (TypeDefinition) type
			}
			else if (type instanceof QName) {
				//TODO
			}
			else {
				String typeRef = type.toString()
				//TODO
			}
		}

		// create property
		QName propertyName = createName(name, attributes)
		DefaultPropertyDefinition property = new DefaultPropertyDefinition(propertyName, parent,
				propertyType);

		addConstraints(property, attributes, params)

		property
	}

	/**
	 * Add constraints based on the given attributes and parameters.
	 * 
	 * @param definition the definition to add constraints to 
	 * @param attributes the map that will be checked for keys matching entries
	 *   of the {@link #constraints} map to create constraints with the
	 *   associated factories
	 * @param params the additional parameters
	 */
	@CompileStatic(TypeCheckingMode.SKIP)
	protected void addConstraints(AbstractDefinition definition, Map attributes, List params) {
		// add constraints from attributes
		attributes?.each { key, value ->
			ConstraintFactory fact = (ConstraintFactory) constraints[key]
			if (fact) {
				def constraint = fact.createConstraint(value, definition)
				if (constraint != null) {
					definition.setConstraint(constraint)
				}
			}
			else {
				// inbuilt "constraints"
				if (key in ['description', 'desc']) {
					definition.setDescription(value as String)
				}
			}
		}

		// add additional custom attributes
		// last param must be a list
		if (params && params.last() instanceof List) {
			List constraintList = (List) params.last()
			constraintList.each {
				if (it instanceof Closure) {
					// closure creating a constraint
					def constraint = it.call(definition)
					if (constraint) {
						definition.setConstraint(constraint)
					}
				}
				else {
					// else assume the parameter is the constraint
					definition.setConstraint(it)
				}
			}
		}
	}

	/**
	 * Get the existing or create a new default property type for the given
	 * class.
	 * 	
	 * @param type the binding for the default property type
	 * @return the default property type definition
	 */
	protected TypeDefinition getOrCreateDefaultPropertyType(Class type) {
		TypeDefinition typeDef = defaultTypes.get(type)
		if (!typeDef) {
			typeDef = createDefaultPropertyType(type)
			defaultTypes.put(type, typeDef)
		}

		typeDef
	}

	/**
	 * Create a new default property type for the given class.
	 *
	 * @param type the binding for the default property type
	 * @return the default property type definition
	 */
	protected TypeDefinition createDefaultPropertyType(Class type) {
		QName name = new QName(defaultPropertyTypeNamespace,
				newDefaultPropertyTypeName(type.name.toLowerCase()))
		DefaultTypeDefinition typeDef = new DefaultTypeDefinition(name)

		// set binding & hasValue
		typeDef.setConstraint(Binding.get(type))
		typeDef.setConstraint(HasValueFlag.ENABLED)

		//TODO any others?

		typeDef
	}

	/**
	 * Create a new default property type for a nested property.
	 *
	 * @param type the binding for the property value or <code>null</code>
	 * @return the type definition
	 */
	protected TypeDefinition createDefaultNestingPropertyType(Class type, String propertyName) {
		QName name = new QName(defaultPropertyTypeNamespace,
				newDefaultPropertyTypeName(propertyName))
		DefaultTypeDefinition typeDef = new DefaultTypeDefinition(name)

		// set binding & hasValue
		if (type) {
			typeDef.setConstraint(Binding.get(type))
			typeDef.setConstraint(HasValueFlag.ENABLED)
		}

		//TODO any others?

		typeDef
	}

	/**
	 * Determine an unused local name for a default property type.
	 * 
	 * @param preferred the preferred name
	 * @return the local name to use
	 */
	protected String newDefaultPropertyTypeName(String preferred) {
		String name = preferred
		int count = 1
		while (defaultTypeNames.contains(name)) {
			count++
			name = preferred + count
		}

		name
	}

}
