/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.schemaprovider;

import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;

import eu.esdihumboldt.hale.schemaprovider.model.Definition;
import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;

/**
 * Represents a schema
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
@Deprecated
public class Schema {

	/**
	 * Empty schema instance
	 */
	public static final Schema EMPTY_SCHEMA =
		new Schema(new HashMap<String, SchemaElement>(), "", null, null); //$NON-NLS-1$
	
	/**
	 * The schema elements
	 */
	private final Map<String, SchemaElement> elements;
	
	/**
	 * All schema elements (including imports etc.)
	 */
	private Map<Name, SchemaElement> allElements;
	
	/**
	 * All type definitions (including imports etc.)
	 */
	private Map<Name, TypeDefinition> allTypes;
	
	/**
	 * Maps namespaces to prefixes
	 */
	private final Map<String, String> prefixes;
	
	/**
	 * The namespace
	 */
	private final String namespace;
	
	/**
	 * The schema location
	 */
	private final URL location;

	/**
	 * Constructor
	 * 
	 * @param elements the type definitions
	 * @param namespace the namespace
	 * @param location the location
	 * @param prefixes maps namespaces to prefixes, may be <code>null</code>
	 */
	public Schema(Map<String, SchemaElement> elements, String namespace,
			URL location, Map<String, String> prefixes) {
		super();
		this.elements = elements;
		this.namespace = namespace;
		this.location = location;
		this.prefixes = prefixes;
	}

	/**
	 * @return the featureTypes
	 */
	public Map<String, SchemaElement> getElements() {
		return elements;
	}
	
	/**
	 * Return all types that might be used as mapping target or source. 
	 * Definitions are mapped to feature types. Definitions can be 
	 * {@link SchemaElement}s or {@link TypeDefinition}s
	 * 
	 * @return all types that might be used as mapping target or source
	 */
	public Map<Definition, FeatureType> getTypes() {
		Map<Definition, FeatureType> types = new HashMap<Definition, FeatureType>();
		for (SchemaElement se : getElements().values()) {
			// all types with element declarations
			if (se.getFeatureType() != null) {
				types.put(se, se.getFeatureType());
			}
			
			// ...and all their subtypes without element declarations
			Queue<TypeDefinition> test = new LinkedList<TypeDefinition>();
			test.addAll(se.getType().getSubTypes());
			while (!test.isEmpty()) {
				TypeDefinition subtype = test.poll();
				
				if (subtype.getDeclaringElements().isEmpty()) {
					FeatureType ft = subtype.getFeatureType();
					if (ft != null) {
						types.put(subtype, ft);
					}
				}
				
				test.addAll(subtype.getSubTypes());
			}
		}
		
		return types;
	}

	/**
	 * @return the namespace
	 */
	public String getNamespace() {
		return namespace;
	}

	/**
	 * @return the location
	 */
	public URL getLocation() {
		return location;
	}

	/**
	 * @return namespaces mapped to prefixes, may be <code>null</code>
	 */
	public Map<String, String> getPrefixes() {
		return prefixes;
	}

	/**
	 * @return the allElements
	 */
	public Map<Name, SchemaElement> getAllElements() {
		return allElements;
	}

	/**
	 * @param allElements the allElements to set
	 */
	public void setAllElements(Map<Name, SchemaElement> allElements) {
		this.allElements = allElements;
	}

	/**
	 * @return the allTypes
	 */
	public Map<Name, TypeDefinition> getAllTypes() {
		return allTypes;
	}

	/**
	 * @param allTypes the allTypes to set
	 */
	public void setAllTypes(Map<Name, TypeDefinition> allTypes) {
		this.allTypes = allTypes;
	}

}
