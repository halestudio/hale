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

package eu.esdihumboldt.hale.gmlwriter.impl.internal.geometry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLStreamWriter;

import org.geotools.feature.NameImpl;
import org.opengis.feature.type.Name;

import com.vividsolutions.jts.geom.Geometry;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;

import eu.esdihumboldt.hale.gmlwriter.impl.internal.geometry.GeometryConverterRegistry.ConversionLadder;
import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;

/**
 * Write geometries for a GML document
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class StreamGeometryWriter {
	
	private static final ALogger log = ALoggerFactory.getLogger(StreamGeometryWriter.class);
	
	/**
	 * Get a geometry writer instance with a default configuration
	 * 
	 * @param gmlNs the GML namespace 
	 * 
	 * @return the geometry writer
	 */
	public static StreamGeometryWriter getDefaultInstance(String gmlNs) {
		StreamGeometryWriter sgm = new StreamGeometryWriter(gmlNs);
		
		//TODO configure
		//XXX testing
		
		return sgm;
	}
	
	private final String gmlNs;
	
	private final Map<Class<? extends Geometry>, Set<Name>> compatibleTypes =
		new HashMap<Class<? extends Geometry>, Set<Name>>(); 

	/**
	 * Constructor
	 * 
	 * @param gmlNs the GML namespace
	 */
	public StreamGeometryWriter(String gmlNs) {
		super();
		
		this.gmlNs = gmlNs;
	}
	
	/**
	 * Register a compatible type for a geometry type
	 *  
	 * @param geomType the geometry type 
	 * @param typeName the compatible type name
	 * @param namespace the compatible type namespace or <code>null</code> for 
	 *   any GML namespace
	 */
	public void registerCompatibleType(Class<? extends Geometry> geomType, 
			String typeName, String namespace) {
		Set<Name> names = compatibleTypes.get(geomType);
		if (names == null) {
			names = new HashSet<Name>();
			compatibleTypes.put(geomType, names);
		}
		
		names.add(new NameImpl(namespace, typeName));
	}

	/**
	 * Write a geometry to a stream for a GML document
	 * 
	 * @param writer the XML stream writer
	 * @param geometry the geometry
	 * @param attributeType the attribute type
	 */
	public void write(XMLStreamWriter writer, Geometry geometry,
			TypeDefinition attributeType) {
		// get candidates
		Class<? extends Geometry> geomType = geometry.getClass();
		List<DefinitionPath> candidates = findCandidates(attributeType, geomType);
		
		// DEBUG
		for (DefinitionPath candidate : candidates) {
			log.info("Geometry structure match: " + geomType.getSimpleName() + " - " + candidate);
		}
		// DEBUG
		
		// if no candidate found, try with compatible geometries
		Class<? extends Geometry> originalType = null;
		ConversionLadder ladder = GeometryConverterRegistry.getInstance().createLadder(geometry);
		while (candidates.isEmpty() && ladder.hasNext()) {
			originalType = geomType;
			
			geometry = ladder.next();
			geomType = geometry.getClass();
			
			log.info("Possible structure for writing " + originalType.getSimpleName() + 
					" not found, trying " + geomType.getSimpleName() + " instead");
			
			candidates = findCandidates(attributeType, geomType);
		}
		
		// DEBUG
		for (DefinitionPath candidate : candidates) {
			log.info("Geometry structure match: " + geomType.getSimpleName() + " - " + candidate);
		}
		// DEBUG
		
		//TODO determine preferred candidate
		
		//TODO remember for later
		
		//TODO write geometry
	}

	/**
	 * Find candidates for a possible path to use for writing the geometry
	 * 
	 * @param attributeType the start attribute type
	 * @param geomType the geometry type
	 * 
	 * @return the path candidates
	 */
	private List<DefinitionPath> findCandidates(TypeDefinition attributeType,
			Class<? extends Geometry> geomType) {
		return findCandidates(attributeType, geomType, new DefinitionPath(),
				new HashSet<TypeDefinition>());
	}
	
	/**
	 * Find candidates for a possible path to use for writing the geometry
	 * 
	 * @param type the type definition
	 * @param geomType the geometry type
	 * @param basePath the base path
	 * @param checkedTypes the type definitions that have already been checked
	 *   (to prevent cycles)
	 * 
	 * @return the path candidates
	 */
	private List<DefinitionPath> findCandidates(TypeDefinition type,
			Class<? extends Geometry> geomType, DefinitionPath basePath,
			Set<TypeDefinition> checkedTypes) {
		if (checkedTypes.contains(type)) {
			return new ArrayList<DefinitionPath>();
		}
		else {
			checkedTypes.add(type);
		}
		
		List<DefinitionPath> candidates = new ArrayList<DefinitionPath>();
		
		// even if there is a direct match we use the paths leading further
		// because we prefer a path as long as possible
		
		// step down sub-types
		for (TypeDefinition subtype : type.getSubTypes()) {
			candidates.addAll(findCandidates(subtype, geomType, 
					new DefinitionPath(basePath).addSubType(subtype),
					new HashSet<TypeDefinition>(checkedTypes)));
		}
		
		// step down properties
		Iterable<AttributeDefinition> properties = (basePath.isEmpty())?(type.getAttributes()):(type.getDeclaredAttributes());
		for (AttributeDefinition att : properties) {
			candidates.addAll(findCandidates(att.getAttributeType(), geomType, 
					new DefinitionPath(basePath).addProperty(att),
					new HashSet<TypeDefinition>(checkedTypes)));
		}
		
		if (candidates.isEmpty()) {
			// check if there is a direct match
			if (matches(type, geomType)) {
				return Collections.singletonList(basePath);
			}
			else {
				return new ArrayList<DefinitionPath>();
			}
		}
		else {
			return candidates;
		}
	}

	/**
	 * Determines if a type definition is compatible to a geometry type
	 *  
	 * @param type the type definition
	 * @param geomType the geometry type
	 * 
	 * @return if the type is compatible to the geometry type
	 */
	protected boolean matches(TypeDefinition type, Class<? extends Geometry> geomType) {
		boolean compatible = false;
		
		// check compatibility list
		Set<Name> names = compatibleTypes.get(geomType);
		if (names != null) {
			if (names.contains(type.getName())) {
				// check type name
				compatible = true;
			}
			
			if (!compatible && type.getName().getNamespaceURI().equals(gmlNs)) {
				// check GML type name
				compatible = names.contains(new NameImpl(null, type.getName().getLocalPart()));
			}
		}
		
		// fall back to binding test
		if (!compatible) {
			// check for equality because we don't want a match for the property types
			//XXX will this really work? e.g. for a point property type? - structure check needed
			compatible = type.getType(null).getBinding().equals(geomType);
		}
		
		//TODO check type def structure?!!
		
		return compatible;
	}

}
