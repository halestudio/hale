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
package test.eu.esdihumboldt.hale.models.factory;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.gml3.ApplicationSchemaConfiguration;
import org.geotools.xml.Configuration;
import org.geotools.xml.Parser;
import org.geotools.xml.XSISAXHandler;
import org.geotools.xml.gml.GMLComplexTypes;
import org.geotools.xml.schema.Schema;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.io.WKTReader;



/**
 * This class allows to create FeatureCollections from various input structures,
 * using Geotools 2.5 and GeoAPI 2.2 types.
 * 
 * @author Thorsten Reitz, Fraunhofer IGD
 * @version {$Id}
 */
public class FeatureCollectionUtilities {

	public static final String namespace = "http://www.esdihumboldt.eu/test/";
	
	/**
	 * This method allows to load a {@link FeatureCollection} from an Well Known 
	 * Text (WKT) file.
	 * 
	 * @param _filename the {@link String} containing a valid path to the WKT 
	 * file.
	 * @param _ftName the name to use for the created {@link FeatureType}.
	 * @param _featureName the name to use for the created {@link Feature}s.
	 * @return a {@link FeatureCollection} containing {@link Feature}s as
	 * defined in the WKT file.
	 */
	public static FeatureCollection<SimpleFeatureType, SimpleFeature> loadFeatureCollectionFromWKT(
			String _filename, String _ftName, String _featureName) {
		FeatureCollection<SimpleFeatureType, SimpleFeature> fc = FeatureCollections.newCollection();
		try {
			// Load file
			WKTReader wktReader = new WKTReader();
			Geometry geom = wktReader.read(new FileReader(_filename));
			
			// create the builder (since it maintains state, has to be done every time anew)
			SimpleFeatureTypeBuilder ftbuilder = new SimpleFeatureTypeBuilder();
			  
			// set global state
			ftbuilder.setSuperType(null);
			ftbuilder.setName(_ftName);
			ftbuilder.setNamespaceURI(namespace);
			//ftbuilder.setSRS("EPSG:4326");
			  
			// add attributes to FT
			ftbuilder.add( "the_geom", geom.getClass() );
			SimpleFeatureType ft = ftbuilder.buildFeatureType();
			
			// build Feature itself, using the created type and the geometry.
			SimpleFeatureBuilder fbuilder = new SimpleFeatureBuilder(ft);

			// add the attributes to Feature
			fbuilder.add(geom);

			// build the feature and add it to collection
			SimpleFeature feature = fbuilder.buildFeature(_featureName);
			fc.add(feature);
		}
		catch (Exception ex) {
			throw new RuntimeException(
					"An exception occured trying to build a FeatureCollection" +
					" from the given file " + _filename + ".", ex);
		}
		return fc;
	}
	

	/**
	 * 
	 * @param gml
	 * @param schema
	 * @param namespace
	 * @return
	 * @throws Exception
	 */
	public FeatureCollection<SimpleFeatureType, SimpleFeature> loadFeatureCollectionFromGML(
			URL gml, 
			URL schema, 
			String namespace)
			throws Exception {

		Configuration configuration = new ApplicationSchemaConfiguration(
				namespace, schema.getPath());

		InputStream xml = new FileInputStream(gml.getFile());
		Parser parser = new org.geotools.xml.Parser(configuration);
		return (FeatureCollection) parser.parse(xml);
	}

	
	/**
	 * This method provides a shorthand for getting a {@link FeatureType}.
	 * 
	 * @param _geometry_class the {@link Class} of the {@link Geometry} that
	 * is to be used, such as {@link LineString}.class.
	 * @param _geometry_name the name of the {@link FeatureType} to use.
	 * @return a {@link FeatureType} with one geometric attribute.
	 */
	public static FeatureType getFeatureType(
			Class<? extends Geometry> _geometry_class, 
			String _feature_type_name, boolean _abstract) {
	
		FeatureType ft = null;
		try {
			SimpleFeatureTypeBuilder ftbuilder = new SimpleFeatureTypeBuilder();
			ftbuilder.setSuperType(null);
			ftbuilder.setName(_feature_type_name);
			ftbuilder.setNamespaceURI(namespace);
			ftbuilder.setAbstract(_abstract);
			if (_geometry_class != null) {
				ftbuilder.add("the_geom", _geometry_class);
			}
			ft = ftbuilder.buildFeatureType();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return ft;
	}
	
	/**
	 * This method provides a shorthand for getting a {@link FeatureType}.
	 * 
	 * @param _superType the supertype to register for the returned {@link FeatureType}.
	 * @param _geometryClass the {@link Class} of the {@link Geometry} that
	 * is to be used, such as {@link LineString}.class.
	 * @param _geometry_name the name of the {@link FeatureType} to use.
	 * @param _abstract if the returned {@link FeatureType} should be abstract, 
	 * make this parameter true.
	 * @return a {@link FeatureType} with one geometric attribute, a string name 
	 * attribute and a supertype.
	 */
	public static FeatureType getFeatureType(
			FeatureType _superType,
			Class<? extends Geometry> _geometryClass, 
			String _featureTypeName, boolean _abstract) {
	
		FeatureType ft = null;
		try {
			SimpleFeatureTypeBuilder ftbuilder = new SimpleFeatureTypeBuilder();
			ftbuilder.setSuperType((SimpleFeatureType) _superType);
			ftbuilder.setName(_featureTypeName);
			ftbuilder.setNamespaceURI(namespace);
			ftbuilder.setAbstract(_abstract);
			if (_geometryClass != null) {
				ftbuilder.add("the_geom", _geometryClass);
			}
			ftbuilder.add("name", String.class);
			ft = ftbuilder.buildFeatureType();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return ft;
	}
	
	/**
	 * Use this method to read a collection of FeatureTypes from a XSD at any 
	 * URL.
	 * FIXME: compare to SchemaService Solution, integrate!
	 * 
	 * @param xsd
	 * @return
	 * @throws Exception
	 */
	public static List<FeatureType> readFeatureTypes(URL xsd) throws Exception {
		XMLReader reader = XMLReaderFactory.createXMLReader();
		XSISAXHandler schemaHandler = new XSISAXHandler(xsd.toURI());
		reader.setContentHandler(schemaHandler);
		reader.parse(new InputSource(xsd.openConnection().getInputStream()));
		Schema s = schemaHandler.getSchema();
		List<FeatureType> result = new ArrayList<FeatureType>();
		SimpleFeatureType ft = GMLComplexTypes.createFeatureType(schemaHandler
				.getSchema().getElements()[1]);
		return result;
	}
}
