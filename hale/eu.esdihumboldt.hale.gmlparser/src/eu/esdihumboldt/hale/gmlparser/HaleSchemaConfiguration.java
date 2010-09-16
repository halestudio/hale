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

package eu.esdihumboldt.hale.gmlparser;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geotools.gml3.GMLConfiguration;
import org.geotools.xml.Configuration;
import org.geotools.xs.XSConfiguration;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeType;

import eu.esdihumboldt.hale.gmlparser.GmlHelper.ConfigurationType;
import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;
import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;

/**
 * Parser configuration based on {@link TypeDefinition}s
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class HaleSchemaConfiguration extends Configuration {
	
	private static final Log log = LogFactory.getLog(HaleSchemaConfiguration.class);
	
	private final Iterable<SchemaElement> elements;

	/**
	 * Constructor
	 * 
	 * @param type the configuration type 
	 * @param namespace the schema namespace
	 * @param schemaLocation the schema location
	 * @param elements the schema elements
	 */
	public HaleSchemaConfiguration(ConfigurationType type, String namespace, String schemaLocation, Iterable<SchemaElement> elements) {
        super(new HaleSchemaXSD(type, namespace, schemaLocation));
        
        addDependency(new XSConfiguration());
        
        // add GML dependency
        switch (type) {
		case GML2:
			addDependency(new org.geotools.gml2.GMLConfiguration());
			break;
		case GML3_2:
			addDependency(new eu.esdihumboldt.hale.gmlparser.gml3_2.GMLConfiguration());
			break;
		case GML3:
			// fall through
		default:
			addDependency(new GMLConfiguration());
			break;
		}
        
        this.elements = elements;
    }

	/**
	 * @see Configuration#configureBindings(Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void configureBindings(Map bindings) {
		Set<TypeDefinition> defs = new HashSet<TypeDefinition>();
		
		// collect type definitions
    	for (SchemaElement element : elements) {
    		addTypeDefinitions(defs, element.getType());
    	}
    	
    	// add type bindings
    	for (TypeDefinition def : defs) {
    		QName name = new QName(def.getName().getNamespaceURI(), def.getName().getLocalPart());
    		
    		AttributeType type = def.getType(null);
    		
    		// check for existing binding
    		if (bindings.containsKey(name)) {
    			Object binding = bindings.get(name);
    		}
    		else {
    		//TODO wrap GML bindings to support attributes? How? Needs change to addTypeDefinitions to again get GML types
	    		if (type instanceof SimpleFeatureType) {
		    		bindings.put(name, 
		    				new SimpleFeatureTypeBinding(name, (SimpleFeatureType) type));
	    		}
	    		else {
	    			log.warn("No parser binding created for type " + name);
	    		}
    		}
    	}
    	
    	// add GML 3.2 bindings, because Namespace has changed from 3.0
    	// container.registerComponentImplementation(GML.FeatureCollectionType, FeatureCollectionTypeBinding.class);
	}

	/**
	 * Add the given type definition and its referenced type definitions to the
	 * given set
	 * 
	 * @param defs the definition set
	 * @param type the type definition
	 */
	private void addTypeDefinitions(Set<TypeDefinition> defs,
			TypeDefinition type) {
		if (type == null) return;
		
//		if (type.getName().getNamespaceURI().startsWith("http://www.opengis.net/gml")) {
//			// ignore GML types
//			return;
//		}
		
		if (!type.isComplexType()) {
			// ignore simple types
			return;
		}
		
		defs.add(type);
		
		for (AttributeDefinition attribute : type.getAttributes()) {
			TypeDefinition attType = attribute.getAttributeType();
			addTypeDefinitions(defs, attType);
		}
	}
	
}
