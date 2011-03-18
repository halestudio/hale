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
import org.geotools.gml3.GML;
import org.geotools.gml3.GMLConfiguration;
import org.geotools.gml3.bindings.SurfacePatchArrayPropertyTypeBinding;
import org.geotools.xml.Binding;
import org.geotools.xml.Configuration;
import org.geotools.xml.SimpleBinding;
import org.geotools.xs.XS;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.ComplexType;

import eu.esdihumboldt.hale.gmlparser.GmlHelper.ConfigurationType;
import eu.esdihumboldt.hale.gmlparser.binding.MaskPropertyBinding;
import eu.esdihumboldt.hale.gmlparser.binding.SimpleBindingWrapper;
import eu.esdihumboldt.hale.gmlparser.binding.SimpleFeatureTypeBinding;
import eu.esdihumboldt.hale.gmlparser.gml3.HaleGMLConfiguration;
import eu.esdihumboldt.hale.gmlparser.xs.HaleXSConfiguration;
import eu.esdihumboldt.hale.gmlparser.xs.HaleXSUnsignedByteBinding;
import eu.esdihumboldt.hale.gmlparser.xs.HaleXSUnsignedIntBinding;
import eu.esdihumboldt.hale.gmlparser.xs.HaleXSUnsignedLongBinding;
import eu.esdihumboldt.hale.gmlparser.xs.HaleXSUnsignedShortBinding;
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
        
        addDependency(new HaleXSConfiguration());
        
        // add GML dependency
        switch (type) {
		case GML2:
			addDependency(new org.geotools.gml2.GMLConfiguration());
			break;
		case GML3_2:
			addDependency(new eu.esdihumboldt.hale.gmlparser.gml3_2.HaleGMLConfiguration());
			break;
		case GML3:
			// fall through
		default:
			addDependency(new HaleGMLConfiguration());
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
		// add fixed XS bindings, because configuring them in HaleXSConfiguration doesn't work
		bindings.put(XS.UNSIGNEDLONG, HaleXSUnsignedLongBinding.class);
		bindings.put(XS.UNSIGNEDINT, HaleXSUnsignedIntBinding.class);
		bindings.put(XS.UNSIGNEDSHORT, HaleXSUnsignedShortBinding.class);
		bindings.put(XS.UNSIGNEDBYTE, HaleXSUnsignedByteBinding.class);
		
		//XXX
		bindings.put(GML.SurfacePatchArrayPropertyType, SurfacePatchArrayPropertyTypeBinding.class);
		
		// bindings based on type definitions
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
//    			Object binding = bindings.get(name);
    			//TODO wrap GML bindings to support attributes? How?
    		}
    		else {
	    		if (type instanceof SimpleFeatureType) {
	    			if (type.isAbstract()) {
//	    				log.warn("Creating no parser binding for abstract type " + name);
	    			}
	    			else {
			    		bindings.put(name, 
			    				new SimpleFeatureTypeBinding(name, (SimpleFeatureType) type));
	    			}
	    		}
	    		else if (type instanceof ComplexType) {
	    			//TODO ?
	    			log.warn("No parser binding created for complex type " + name); //$NON-NLS-1$
	    		}
	    		else if (hasElements(def)) {
	    			// simple type binding that masks a complex type, e.g. geometry property types
	    			bindings.put(name, new MaskPropertyBinding(name, type.getBinding()));
	    		}
	    		else {
	    			// simple type
	    			
	    			// try to find binding for type name
	    			QName aname = new QName(type.getName().getNamespaceURI(), type.getName().getLocalPart());
	    			Object binding = bindings.get(aname);
	    			
	    			if (binding != null) {
	    				Binding bind = null;
	    				
	    				if (binding instanceof Class<?> && Binding.class.isAssignableFrom((Class<?>) binding)) {
	    					// try to create binding
	    					try {
								bind = ((Class<? extends Binding>) binding).newInstance();
							} catch (Exception e) {
								log.debug("Could not create binding for simple type " + name, e); //$NON-NLS-1$
							}
	    				}
	    				else if (binding instanceof Binding) {
	    					bind = (Binding) binding;
	    				}
	    				
	    				if (bind instanceof SimpleBinding) {
	    					// wrap simple binding
	    					bind = new SimpleBindingWrapper(name, (SimpleBinding) bind);
	    				}
	    				
	    				if (bind != null) {
	    					bindings.put(name, bind);
	    				}
	    				else {
	    					log.warn("No parser binding created for type " + name); //$NON-NLS-1$
	    				}
	    			}
	    			else {
	    				log.warn("No parser binding created for type " + name); //$NON-NLS-1$
	    			}
	    		}
    		}
    	}
	}

	/**
	 * Determines if a type definition has any elements
	 * 
	 * @param def the type definitions
	 * @return if the definition has any elements
	 */
	private boolean hasElements(TypeDefinition def) {
		for (AttributeDefinition att : def.getAttributes()) {
			if (att.isElement()) {
				return true;
			}
		}
		
		return false;
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
		
		if (!type.isComplexType()) {
			// ignore simple types
			return;
		}
		
		defs.add(type);
		
		for (AttributeDefinition attribute : type.getAttributes()) {
			TypeDefinition attType = attribute.getAttributeType();
			if (attType != null && !defs.contains(attType)) {
				addTypeDefinitions(defs, attType);
			}
		}
	}
	
}
