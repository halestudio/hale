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
package eu.esdihumboldt.gmlhandler.gt2deegree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.deegree.commons.tom.primitive.PrimitiveType;
import org.deegree.cs.CRS;
import org.deegree.feature.GenericFeatureCollection;
import org.deegree.feature.property.GenericProperty;
import org.deegree.feature.types.GenericFeatureType;
import org.deegree.feature.types.property.GeometryPropertyType;
import org.deegree.feature.types.property.SimplePropertyType;
import org.deegree.feature.types.property.ValueRepresentation;
import org.deegree.feature.types.property.GeometryPropertyType.CoordinateDimension;
import org.deegree.feature.types.property.GeometryPropertyType.GeometryType;
import org.deegree.geometry.Geometry;
import org.deegree.geometry.points.Points;
import org.deegree.geometry.primitive.Ring;
import org.deegree.geometry.standard.multi.DefaultMultiPolygon;
import org.deegree.geometry.standard.primitive.DefaultPolygon;
import org.deegree.gml.GMLVersion;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.Geometries;
import org.geotools.renderer.lite.StreamingRenderer;
import org.opengis.feature.Feature;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.Property;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.ComplexType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.feature.type.PropertyType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import eu.esdihumboldt.gmlhandler.deegree.InternalFeature;
import eu.esdihumboldt.gmlhandler.deegree.SimplePropertyWithAttributes;
import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;
import eu.esdihumboldt.tools.AttributeProperty;
import eu.esdihumboldt.tools.FeatureInspector;


/**
 * This class contains methods to create Deegree3 Structures like Feature,
 * FeatureCollection and so using GeoSpatial data retrieved from the according
 * geotools Objects.
 * 
 * @author Jan Jezek, Anna Pitaev, Simon Templer
 * @partner ?, 04 / Logica, 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class GtToDgConvertor {
	
	private static final Logger log = Logger.getLogger(GtToDgConvertor.class);
	
	private final TypeIndex types;
	
	/**
	 * Create a new converter
	 * 
	 * @param types the type index
	 */
	public GtToDgConvertor(TypeIndex types) {
		super();
		this.types = types;
	}

	/**
	 * Convert a feature collection
	 * 
	 * @param fc geotools-based Feature Collection
	 * @return deegree-based Feature Collection
	 */

	public org.deegree.feature.FeatureCollection convertGtToDg(
			org.geotools.feature.FeatureCollection<FeatureType, Feature> fc) {

		Collection<org.deegree.feature.Feature> dgFeatures = new ArrayList<org.deegree.feature.Feature>();
		org.deegree.feature.Feature dgFeature = null;
//		List<org.deegree.feature.property.Property> dgProperties = null;
//		List<org.deegree.feature.types.property.PropertyType> dgPropertyTypes = null;
		for (FeatureIterator<Feature> i = fc.features(); i.hasNext();) {
//			dgPropertyTypes = new ArrayList<org.deegree.feature.types.property.PropertyType>();
			Feature gtFeature = i.next();
			dgFeature = createDgFeature(gtFeature);
			dgFeatures.add(dgFeature);

		}
		org.deegree.feature.FeatureCollection dfFC = new GenericFeatureCollection(
				fc.getID(), dgFeatures);
		return dfFC;
	}

	/**
	 * Convert a feature 
	 * 
	 * @param gtFeature geotools-based Feature
	 * @return deegree-based Feature
	 */
	protected org.deegree.feature.Feature createDgFeature(Feature gtFeature) {
		FeatureType gtFT = gtFeature.getType();
		TypeDefinition type = types.getType(gtFT);
		// convert gtFT to gtFT

		// 1. GenericFeatureType
		//XXX this needn't be done for every single feature
		GenericFeatureType dgFT = createDgFt(type);

		// 2. Feature id
		String fid = gtFeature.getIdentifier().getID();
		
		// 3. properties and attributes
		List<org.deegree.feature.property.Property> dgProps = new ArrayList<org.deegree.feature.property.Property>();
		Collection<Property> properties = FeatureInspector.getProperties(gtFeature);
		for (Property p : properties) {
			AttributeDefinition attribute = type.getAttribute(p.getName().getLocalPart());
			org.deegree.feature.property.Property dgProp = createDgProp(p, attribute);
			dgProps.add(dgProp);
		}
		
		// 4. GMLVersion
		org.deegree.feature.Feature dgFeature = dgFT.newFeature(fid, dgProps,
				GMLVersion.GML_32);
		return dgFeature;

	}

	/**
	 * Create a deegree property from a geotools property
	 * 
	 * @param gtProp geotools-based Property
	 * @param attribute the corresponding attribute definition
	 * @param crs XXX the CRS
	 * @param gp XXX the geometry attribute
	 * 
	 * @return deegree-based Property
	 */
	private static org.deegree.feature.property.Property createDgProp(
			Property gtProp, AttributeDefinition attribute) {
		// 1. declare a Property instance: make decision about implementing
		// class after analyze of the PropertyType
		org.deegree.feature.property.Property dgProp = null;
		// 2. define isNilled isNilled
		boolean isNilled = attribute.isNillable();
		// 3. define property name
		QName dgPropName = new QName(attribute.getNamespace(), attribute.getName());

		// get property type TODO needn't be done for each property/feature
		org.deegree.feature.types.property.PropertyType dgPT = createDgPt(attribute);
		
		Collection<? extends Property> atts = AttributeProperty.getAttributeProperties(gtProp);

		if (dgPT instanceof org.deegree.feature.types.property.SimplePropertyType) {
			// A PropertyType that defines a property with a primitive value,
			// i.e. a value that can be represented as a single String.
			if (isNilled && gtProp.getValue() == null) {
//				TypedObjectNode node = null;
				//XXX why not SimpleProperty here?
//				dgProp = new org.deegree.feature.property.GenericProperty(dgPT,
//						dgPropName, null);
				dgProp = new SimplePropertyWithAttributes(
						(SimplePropertyType) dgPT, null, 
						((SimplePropertyType) dgPT).getPrimitiveType(), atts);
			} else {
				dgProp = new SimplePropertyWithAttributes(
						(SimplePropertyType) dgPT, gtProp.getValue().toString(),
						((SimplePropertyType) dgPT).getPrimitiveType(), atts);
				
			}

		} else if (dgPT instanceof org.deegree.feature.types.property.GeometryPropertyType) {
			// TODO handle case with GeometryReference<Geometry>
			// convert gt Geometry attribute to deegree Geometry
			// TODO improve this! crs and gp shouldnt be parameters here
			org.deegree.geometry.Geometry dgGeometry = createDgGeometry(gtProp, ((org.deegree.feature.types.property.GeometryPropertyType) dgPT));
			dgProp = new GenericProperty(dgPT, dgPropName, dgGeometry);

		} else if (dgPT instanceof org.deegree.feature.types.property.FeaturePropertyType) {
			// we support inline Features mapping only
			// if (gtProp instanceof SimpleFeature ){

			// create deegree generic feature based on gtProp
			GenericFeatureType ft = createDgFt(attribute.getAttributeType());
			
			org.deegree.feature.Feature featureProp = createDgFeature(gtProp, attribute, ft);
	    	dgProp = new org.deegree.feature.property.GenericProperty(dgPT,
					dgPropName, (featureProp == null)?(null):(new InternalFeature(featureProp)));
			//dgProp = createDgFeatureProperty((Attribute)gtProp, ft);
			/*
			 * //TODO find a nicer way to create fid String fid =
			 * java.util.UUID.randomUUID().toString(); GMLVersion version =
			 * GMLVersion.GML_32; List<org.deegree.feature.property.Property>
			 * properties = new
			 * ArrayList<org.deegree.feature.property.Property>();
			 * org.deegree.feature.property.Property property; //create Property
			 * from the gt Attributes List<Object> attrs =
			 * ((SimpleFeature)gtProp).getAttributes(); for (Object attr :
			 * attrs){ property = createProperty(attr); //TODOuse geotools
			 * Feature Attribute Type //properties.add(property,
			 * ((SimpleFeature)gtProp).getAttribute(attr.)); } GenericFeature
			 * dgSubProperty = new GenericFeature(ft, fid, properties, version);
			 */
			//dgProp = new GenericProperty(dgPT, dgPropName, featureProp);
			// }
		} else if (dgPT instanceof org.deegree.feature.types.property.CustomPropertyType) {
			// TODO implement if needed

		} else if (dgPT instanceof org.deegree.feature.types.property.CodePropertyType) {
			// TODO implement it if needed

		} else if (dgPT instanceof org.deegree.feature.types.property.EnvelopePropertyType) {
			// TODO clear how to retrieve envelope data
		}
		return dgProp;
	}

//	private static org.deegree.feature.property.Property createDgFeatureProperty(
//			Attribute gtProp, GenericFeatureType ft) {
//		List<org.deegree.feature.property.Property> dgProps = new ArrayList<org.deegree.feature.property.Property>();
//		Collection attributesCollestion = ((Collection)gtProp.getValue());
//		if (attributesCollestion != null){
//		Iterator<Property> gtPropsIter = attributesCollestion.iterator();
//		org.deegree.feature.property.Property dgProp;
//		while (gtPropsIter.hasNext()) {
//			 gtProp = gtPropsIter.next();
//			 dgProp = createDgProp(gtProp, null, null);
//			dgProps.add(dgProp);
//		}
//		return dgProp;
////	}

	/**
	 * Generates a feature in case the geotools attribute contains a collection
	 * of properties
	 * 
	 * @param property 
	 * @param complexAttribute
	 * @param attribute 
	 * @param ft
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected static org.deegree.feature.Feature createDgFeature(
			Property property, AttributeDefinition attribute, 
			GenericFeatureType ft) {
		
		// 2. Feature id
		String fid = java.util.UUID.randomUUID().toString();
		
		// 3. List<Property>
		Object value = property.getValue();
		List<org.deegree.feature.property.Property> dgProps = new ArrayList<org.deegree.feature.property.Property>();
		
		if (value == null) {
			return null;
		}
		else if (value instanceof Collection<?>) {
			Collection<Property> propertyCollection = ((Collection<Property>) value);
			
			if (propertyCollection.size() > 1) throw new UnsupportedOperationException("Multiple feature in property not really supported");
			
			for (Property feature : propertyCollection) {
				
				Collection<Property> properties = FeatureInspector.getProperties(feature);
				for (Property p : properties) {
					AttributeDefinition childAttribute = attribute.getAttributeType().getAttribute(p.getName().getLocalPart());
					if (childAttribute != null) {
						org.deegree.feature.property.Property dgProp = createDgProp(p, childAttribute);
						dgProps.add(dgProp);
					}
					else {
						log.error("Attribute " + p.getName().getLocalPart() + " not found in type " + attribute.getAttributeType());
					}
				}
			}
		}
		else if (value instanceof com.vividsolutions.jts.geom.Geometry) {
			// special handling for geometries
			// geometry must be forwarded downward for property types
//			for (AttributeDefinition childAttribute : attribute.getAttributeType().getAttributes()) {
//				if (com.vividsolutions.jts.geom.Geometry.class.isAssignableFrom(childAttribute.getAttributeType().getType(null).getBinding())) {
//					org.deegree.feature.property.Property dgProp = createDgProp(property, childAttribute);
//					dgProps.add(dgProp);
//					break;
//				}
//			}
		}
		
		// 4. GMLVersion
		org.deegree.feature.Feature dgFeature = ft.newFeature(fid, dgProps,
				GMLVersion.GML_32);
		return dgFeature;
	}

	/**
	 * 
	 * <p>
	 * This method provides mapping for the following geometries:
	 * <ul>
	 * <li>POINT</li>
	 * <li>MULTIPOINT</li>
	 * <li>POLIGON</li>
	 * <li>MULTIPOLIGON</li>
	 * <li>LINESTRING</li>
	 * <li>MULTILINESTRING</li>
	 * </ul>
	 * 
	 * @param gtProp
	 *            GeometryAttribute
	 * @param gp 
	 * @return Geometry
	 */
	private static Geometry createDgGeometry(Property gtProp, GeometryPropertyType gt) {
		
		Geometry dgGeometry = null;
		//String geometryName = gtProp.getDescriptor().getType().getBinding()
		//		.getSimpleName();
		//if (geometryName.equals("Geometry")){
		//	geometryName = gp.getDescriptor().getType().getBinding().getSimpleName();
			//if (geometryName.equals("Geometry")){
		String geometryName;
		Geometries geomType;
		Object jtsGeom = gtProp.getValue();
		if (jtsGeom != null && jtsGeom instanceof com.vividsolutions.jts.geom.Geometry ){
			geometryName = jtsGeom.getClass().getSimpleName();
			geomType = Geometries.getForBinding((Class<? extends com.vividsolutions.jts.geom.Geometry>) jtsGeom.getClass());
		}
		else {
			return null;
		}
			
//		gtProp = gp;
		
		// test for the CRS
		CoordinateReferenceSystem crs = null;
		if (gtProp instanceof GeometryAttribute) {
			crs = ((GeometryAttribute) gtProp).getDescriptor().getCoordinateReferenceSystem();
		}
		if (crs == null){
			//next try - user data of value
			Object value = jtsGeom;
			if (value instanceof com.vividsolutions.jts.geom.Geometry){
				Object userData = ((com.vividsolutions.jts.geom.Geometry)value).getUserData();
				if (userData instanceof CoordinateReferenceSystem){
					crs = (CoordinateReferenceSystem) userData;
				}
			}
		}
		
			
		// we provide mapping for
		/*Geometries geomType = Geometries
				.getForBinding((Class<? extends com.vividsolutions.jts.geom.Geometry>) gtProp
						.getDescriptor().getType().getBinding());
		// map common attributtes
*/		// 1. id
		// TODO test it
		String id = UUID.randomUUID().toString();
		// 2.TODO figure out CRS
		 /*CoordinateReferenceSystem crs = ((org.opengis.feature.type.GeometryType)gtProp.getType()).getCoordinateReferenceSystem();*/
		 org.deegree.cs.CRS dgCRS = createCRS(crs);
		
		// 3. precision model
		// TODO find a nicer way to define it
		org.deegree.geometry.precision.PrecisionModel pm = org.deegree.geometry.precision.PrecisionModel.DEFAULT_PRECISION_MODEL;

		switch (geomType) {
		case POLYGON:
			Polygon gtPoligon = (Polygon) gtProp.getValue();
			Ring exteriorRing = createRing(gtPoligon.getExteriorRing());
			int numOfInterRings = gtPoligon.getNumInteriorRing();
			List<Ring> interiorRings = new ArrayList<Ring>(numOfInterRings);
			Ring interiorRing = null;
			for (int i = 0; i < numOfInterRings; i++) {
				interiorRing = createRing(gtPoligon.getInteriorRingN(i));
				interiorRings.add(interiorRing);

			}
			dgGeometry = new DefaultPolygon(id, dgCRS, pm, exteriorRing,
					interiorRings);
			dgGeometry = ((org.deegree.geometry.standard.AbstractDefaultGeometry) dgGeometry)
					.createFromJTS(gtPoligon, dgCRS);
			break;
		case MULTIPOLYGON:
			MultiPolygon gtMultiPolygon = (MultiPolygon) gtProp.getValue();
			int numOfPolygs = gtMultiPolygon.getNumGeometries();
			List<org.deegree.geometry.primitive.Polygon> dgPolygons = new ArrayList<org.deegree.geometry.primitive.Polygon>(
					numOfPolygs);
			org.deegree.geometry.primitive.Polygon dgPolygon;
			for (int i = 0; i < numOfPolygs; i++) {
				dgPolygon = createDefaultPolygon((Polygon) gtMultiPolygon
						.getGeometryN(i));
				dgPolygons.add(dgPolygon);
			}
			dgGeometry = new DefaultMultiPolygon(id, dgCRS, pm, dgPolygons);
			dgGeometry = ((org.deegree.geometry.standard.AbstractDefaultGeometry) dgGeometry)
					.createFromJTS(gtMultiPolygon, dgCRS);
			break;

		case LINESTRING:
			LineString gtLineString = (LineString) gtProp.getValue();
			Points dgLineStringPoints = createDGPoints(gtLineString
					.getCoordinates());
			dgGeometry = new org.deegree.geometry.standard.primitive.DefaultLineString(
					id, dgCRS, pm, dgLineStringPoints);
			dgGeometry = ((org.deegree.geometry.standard.AbstractDefaultGeometry) dgGeometry)
					.createFromJTS(gtLineString, dgCRS);
			break;
		case MULTILINESTRING:
			MultiLineString gtMultiLineString = (MultiLineString) gtProp.getValue();
			int numOfLineStrings = gtMultiLineString.getNumGeometries();
			List<org.deegree.geometry.primitive.LineString> dgLineStrings = new ArrayList<org.deegree.geometry.primitive.LineString>(
					numOfLineStrings);
			org.deegree.geometry.primitive.LineString dgLineString;
			for (int i = 0; i < numOfLineStrings; i++) {
				dgLineString = createLineString(gtMultiLineString
						.getGeometryN(i));
				dgLineStrings.add(dgLineString);
			}
			dgGeometry = new org.deegree.geometry.standard.multi.DefaultMultiLineString(
					id, dgCRS, pm, dgLineStrings);
			dgGeometry = ((org.deegree.geometry.standard.AbstractDefaultGeometry) dgGeometry)
					.createFromJTS(gtMultiLineString, dgCRS);
			break;

		case POINT:
			Point gtPoint = (Point) (gtProp.getValue());

			double[] dgCoordinates = createCoordinates(gtPoint.getCoordinates());
			dgGeometry = new org.deegree.geometry.standard.primitive.DefaultPoint(
					id, dgCRS, pm, dgCoordinates);
			dgGeometry = ((org.deegree.geometry.standard.AbstractDefaultGeometry) dgGeometry)
					.createFromJTS(gtPoint, dgCRS);
			break;
		case MULTIPOINT:
			MultiPoint gtMultiPoint = (MultiPoint) gtProp.getValue();
			int numOfPoints = gtMultiPoint.getNumGeometries();
			List<org.deegree.geometry.primitive.Point> dgPoints = new ArrayList<org.deegree.geometry.primitive.Point>(
					numOfPoints);
			org.deegree.geometry.primitive.Point dgPoint;
			for (int i = 0; i < numOfPoints; i++) {
				dgPoint = createPoint(gtMultiPoint.getGeometryN(i));
				dgPoints.add(dgPoint);

			}
			dgGeometry = new org.deegree.geometry.standard.multi.DefaultMultiPoint(
					id, dgCRS, pm, dgPoints);
			dgGeometry = ((org.deegree.geometry.standard.AbstractDefaultGeometry) dgGeometry)
					.createFromJTS(gtMultiPoint, dgCRS);
			

			break;
			
		default:
			
			 


			break;
		}
		//set id
		dgGeometry.setId(id);
		//set srs
		dgGeometry.setCoordinateSystem(dgCRS);
		//set precision model
		dgGeometry.setPrecision(pm);
		return dgGeometry;
	}

	
	private static org.deegree.geometry.primitive.Point createPoint(
			com.vividsolutions.jts.geom.Geometry geometryN) {
		// TODO Auto-generated method stub
		return null;
	}

	private static double[] createCoordinates(Coordinate[] coordinates) {
		// TODO Auto-generated method stub
		return null;
	}

	private static org.deegree.geometry.primitive.LineString createLineString(
			com.vividsolutions.jts.geom.Geometry geometryN) {
		// TODO Auto-generated method stub
		return null;
	}

	private static Points createDGPoints(Coordinate[] coordinates) {
		// TODO Auto-generated method stub
		return null;
	}

	private static org.deegree.geometry.primitive.Polygon createDefaultPolygon(
			Polygon geometryN) {
		// TODO Auto-generated method stub
		return null;
	}

	private static Ring createRing(LineString exteriorRing) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 * @param geotools coordinateReferenceSystem
	 * @return deegree-based Coordinate Reference System
	 */
	private static CRS createCRS(
			CoordinateReferenceSystem coordinateReferenceSystem) {
		CRS dgCrs = new CRS(org.geotools.referencing.CRS.toSRS(coordinateReferenceSystem));
		//if crs null set default crs
		if (dgCrs.getName() == null){
			dgCrs = CRS.EPSG_4326;
		}
		return dgCrs;
	}

	/**
	 * 
	 * @param hasXMLAttrs
	 * @param geotools
	 *            -based propertyType
	 * @return deegree-based PropertyType
	 */
	private static org.deegree.feature.types.property.PropertyType createDgPt(
			PropertyDescriptor gtPD, boolean hasXMLAttrs) {

		org.deegree.feature.types.property.PropertyType dgPT = null;
		// TODO define a better way for the value representation
		List<org.deegree.feature.types.property.PropertyType> substitutions = new ArrayList<org.deegree.feature.types.property.PropertyType>();
		PropertyType gtPT = gtPD.getType();
		Property prop = null;
		String namespace = gtPD.getName().getNamespaceURI();

		// define commons attributes
		QName dgName = new QName(gtPD.getName().getNamespaceURI(), gtPD
				.getName().getLocalPart());
		QName dgFTName = new QName(gtPT.getName().getNamespaceURI(), gtPT
				.getName().getLocalPart());
		int minOccurs = gtPD.getMinOccurs();
		int maxOccurs = gtPD.getMaxOccurs();
		boolean isAbstract = gtPT.isAbstract();
		boolean isNillable = gtPD.isNillable();
		if (gtPT instanceof org.opengis.feature.type.GeometryType) {
			org.deegree.feature.types.property.GeometryPropertyType.GeometryType dgGeomType = createGeometryType((GeometryDescriptor) gtPD);
			org.deegree.feature.types.property.GeometryPropertyType.CoordinateDimension dgCoordDim = createCoordDim((GeometryDescriptor) gtPD);
			dgPT = new org.deegree.feature.types.property.GeometryPropertyType(
					dgName, minOccurs, maxOccurs, isAbstract, isNillable, 
					substitutions, dgGeomType, dgCoordDim, ValueRepresentation.BOTH);
		} else if (gtPT instanceof org.opengis.feature.type.AttributeType
				&& !(gtPT instanceof org.opengis.feature.type.ComplexType)) {
			// TODO find a nicer way to define this binding
			PrimitiveType propPrimType = PrimitiveType
					.determinePrimitiveType(gtPT.getBinding().getName());
			dgPT = new org.deegree.feature.types.property.SimplePropertyType(
					dgName, minOccurs, maxOccurs, propPrimType, isAbstract, 
					isNillable, substitutions);
		}else if (gtPT instanceof org.opengis.feature.type.AttributeType
				&& gtPT instanceof org.opengis.feature.type.FeatureType){
			
			//dgPT = new org.deegree.feature.types.property.CustomPropertyType(dgName, maxOccurs, maxOccurs, null, isAbstract, substitutions); 
			dgPT = new org.deegree.feature.types.property.FeaturePropertyType(
					dgName, minOccurs, maxOccurs, isAbstract, isNillable,
					substitutions, dgFTName, ValueRepresentation.BOTH);
			
	}else {
			if (hasXMLAttrs) {

				// create CustomPropertyType to handle xml attrs
				// TODO XSTypeDefinition we need
				// org.apache.xerces.xs.XSElementDeclaration xs = null;
				// this.appSchema.getXSModel().getAbstractCurveSegmentElementDeclaration().getEnclosingCTDefinition().
				// List<org.apache.xerces.xs.XSElementDeclaration> xsDecl =
				// this.appSchema.getXSModel().
				// 2. Qname
				// 3. namespace - only element declarations in this namespace
				// are returned, set to null for all namespaces
				// 4. transitive - if true, also substitutions for substitutions
				// (and so one) are included
				// 5. onlyConcrete - if true, only concrete (non-abstract)
				// declarations are returned
				// dgPT = new
				// org.deegree.feature.types.property.CustomPropertyType(dgFTName,
				// maxOccurs, maxOccurs, null, isAbstract, substitutions);

			} else {
				// create Feature Property Type
				dgPT = new org.deegree.feature.types.property.FeaturePropertyType(
						dgName, minOccurs, maxOccurs, isAbstract, isNillable,
						substitutions, dgFTName, ValueRepresentation.BOTH);
			}
		}

		return dgPT;
	}

	/**
	 * 
	 * @param geotools
	 *            Geometry Descriptor
	 * @return CoordinateDimension
	 */
	private static CoordinateDimension createCoordDim(
			GeometryDescriptor descriptor) {
		if (descriptor.getCoordinateReferenceSystem() != null
				&& descriptor.getCoordinateReferenceSystem()
						.getCoordinateSystem() != null) {
			if (descriptor.getCoordinateReferenceSystem().getCoordinateSystem()
					.getDimension() == 2)
				return CoordinateDimension.DIM_2;
			if (descriptor.getCoordinateReferenceSystem().getCoordinateSystem()
					.getDimension() == 3)
				return CoordinateDimension.DIM_3;
		}
		return CoordinateDimension.DIM_2_OR_3;

	}

	/**
	 * 
	 * @param geotools
	 *            Geometry Descriptor
	 * @return Geometry Type
	 */
	private static GeometryType createGeometryType(GeometryDescriptor descriptor) {
		// 1. retrieve the Geometry type name
		String geometry = descriptor.getType().getBinding().getSimpleName();
		// 2. assign a string value to the GeometryType
		return GeometryType.fromGMLTypeName(geometry);
	}

	/**
	 * Create a deegree feature type from a geotools feature type 
	 * 
	 * @param attributeType geotools-based FeatureType
	 * @param dgName feature type name
	 * @return deegree-based FeatureType
	 * 
	 * @deprecated because namespaces for properties cannot be stored in the
	 *   geotools feature type because {@link StreamingRenderer} chokes on that
	 */
	@Deprecated
	private static org.deegree.feature.types.GenericFeatureType createDgFt(
			AttributeType attributeType, QName dgName) {
		QName ftName = null;
		if (dgName == null){
		// 1.0 QName
		Name gtFTName = attributeType.getName();
        ftName = new QName(gtFTName.getNamespaceURI(),
		gtFTName.getLocalPart());
		}else{
			ftName = dgName;
		}
		List<org.deegree.feature.types.property.PropertyType> propDecls = new ArrayList<org.deegree.feature.types.property.PropertyType>();
		// 1.1 List<PropertyType>
		if (attributeType instanceof ComplexType){
		for (PropertyDescriptor gtPD : ((ComplexType)attributeType).getDescriptors()) {
			// create deegree PropertyType
			org.deegree.feature.types.property.PropertyType dgPT = createDgPt(
					gtPD, false);
			propDecls.add(dgPT);
		}
		}
		// 1.2 boolean isAbstract
		boolean isAbstract = attributeType.isAbstract();

		org.deegree.feature.types.GenericFeatureType dgFT = new org.deegree.feature.types.GenericFeatureType(
				ftName, propDecls, isAbstract);
		return dgFT;
	}
	
	/**
	 * Create a deegree feature type from a {@link TypeDefinition}
	 * 
	 * @param type the type definition
	 * @return deegree-based FeatureType
	 */
	protected static org.deegree.feature.types.GenericFeatureType createDgFt(
			TypeDefinition type) {
		AttributeType ft = type.getType(null);
		QName ftName = new QName(ft.getName().getNamespaceURI(), ft.getName().getLocalPart());
		
		List<org.deegree.feature.types.property.PropertyType> propDecls = new ArrayList<org.deegree.feature.types.property.PropertyType>();
		// 1.1 List<PropertyType>
		for (AttributeDefinition attribute : type.getAttributes()) {
			// create deegree PropertyType
			org.deegree.feature.types.property.PropertyType dgPT = createDgPt(
					attribute);
			propDecls.add(dgPT);
		}
		// 1.2 boolean isAbstract
		boolean isAbstract = type.isAbstract();

		org.deegree.feature.types.GenericFeatureType dgFT = new org.deegree.feature.types.GenericFeatureType(
				ftName, propDecls, isAbstract);
		return dgFT;
	}
	
	/**
	 * Create a deegree property type from an {@link AttributeDefinition}
	 * 
	 * @param attribute the attribute definition
	 * @return deegree-based PropertyType
	 */
	protected static org.deegree.feature.types.property.PropertyType createDgPt(
			AttributeDefinition attribute) {

		// TODO define a better way for the value representation
		List<org.deegree.feature.types.property.PropertyType> substitutions = new ArrayList<org.deegree.feature.types.property.PropertyType>();

		// define commons attributes
		QName dgName = new QName(attribute.getNamespace(), attribute.getName());
		QName dgFTName = new QName(attribute.getTypeName().getNamespaceURI(), 
				attribute.getTypeName().getLocalPart());
		
		int minOccurs = (int) attribute.getMinOccurs();
		int maxOccurs = (int) attribute.getMaxOccurs();
		boolean isAbstract = attribute.getAttributeType().isAbstract();
		boolean isNillable = attribute.isNillable();
		
		PropertyType gtPT = attribute.getAttributeType().getType(null);
		org.deegree.feature.types.property.PropertyType dgPT;
		if (com.vividsolutions.jts.geom.Geometry.class.isAssignableFrom(gtPT.getBinding())
				/*&& attribute.getAttributeType().getAttributes().isEmpty()*/) {
			// create deegree geometry type
			org.deegree.feature.types.property.GeometryPropertyType.GeometryType dgGeomType = createGeometryType(attribute);
			org.deegree.feature.types.property.GeometryPropertyType.CoordinateDimension dgCoordDim = createCoordDim(attribute);
			dgPT = new org.deegree.feature.types.property.GeometryPropertyType(
					dgName, minOccurs, maxOccurs, isAbstract, isNillable, 
					substitutions, dgGeomType, dgCoordDim, ValueRepresentation.BOTH);
		} else if (!attribute.getAttributeType().isComplexType()) {
			// primitive type
			// TODO find a nicer way to define this binding
			PrimitiveType propPrimType = PrimitiveType
					.determinePrimitiveType(gtPT.getBinding().getName());
			dgPT = new org.deegree.feature.types.property.SimplePropertyType(
					dgName, minOccurs, maxOccurs, propPrimType, isAbstract, 
					isNillable, substitutions);
		} else {
			// complex or feature type
			//dgPT = new org.deegree.feature.types.property.CustomPropertyType(dgName, maxOccurs, maxOccurs, null, isAbstract, substitutions); 
			dgPT = new org.deegree.feature.types.property.FeaturePropertyType(
					dgName, minOccurs, maxOccurs, isAbstract, isNillable,
					substitutions, dgFTName, ValueRepresentation.BOTH);
		}		

		return dgPT;
	}
	
	/**
	 * Get the CRS dimension for the given geometry attribute definition
	 * 
	 * @param attribute the geometry attribute definition
	 * @return the CRS dimension
	 */
	private static CoordinateDimension createCoordDim(
			AttributeDefinition attribute) {
		//TODO implement
//		if (descriptor.getCoordinateReferenceSystem() != null
//				&& descriptor.getCoordinateReferenceSystem()
//						.getCoordinateSystem() != null) {
//			if (descriptor.getCoordinateReferenceSystem().getCoordinateSystem()
//					.getDimension() == 2)
//				return CoordinateDimension.DIM_2;
//			if (descriptor.getCoordinateReferenceSystem().getCoordinateSystem()
//					.getDimension() == 3)
//				return CoordinateDimension.DIM_3;
//		}
		return CoordinateDimension.DIM_2_OR_3;
	}
	
	/**
	 * Create the deegree geometry type for the given geometry attribute
	 * definition
	 * 
	 * @param attribute the geometry attribute definition
	 * @return Geometry Type
	 */
	private static GeometryType createGeometryType(
			AttributeDefinition attribute) {
		// 1. retrieve the Geometry type name
		String geometry = attribute.getAttributeType().getType(null).getBinding().getSimpleName();
		// 2. assign a string value to the GeometryType
		return GeometryType.fromGMLTypeName(geometry);
	}

}
