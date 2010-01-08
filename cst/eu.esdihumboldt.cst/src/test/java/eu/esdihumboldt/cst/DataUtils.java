/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.cst;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;


import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.gml.producer.FeatureTransformer;
import org.geotools.gml3.ApplicationSchemaConfiguration;
import org.geotools.referencing.CRS;
import org.geotools.xml.Configuration;
import org.geotools.xml.Parser;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.springframework.util.Assert;
import org.xml.sax.SAXException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class DataUtils {

	public FeatureCollection getFC() {
		return getAreaFC();

	}


	
	public FeatureCollection getAreaFC() {

		URL gml = getClass().getResource("aree_protette_2001_no_gaps_ref.gml");
		URL xsd = getClass().getResource("aree_protette_2001_no_gaps_ref.xsd");
		String namespace = "http://www.gvsig.com/cit";
		return parserGML(gml, xsd, namespace);

	}
	
	

	public FeatureCollection parserGML(URL gml, URL schema, String namespace) {

		Configuration configuration = new ApplicationSchemaConfiguration(
				namespace, schema.getPath());

		FeatureCollection fc = null;
		try {
			InputStream xml = new FileInputStream(gml.getFile());
			Parser parser = new org.geotools.xml.Parser(configuration);
			fc = (FeatureCollection) parser.parse(xml);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Assert.notNull(fc);
		return fc;
	}

	public FeatureCollection getMyFC() {
		FeatureCollection fc = null;
		try {
			SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
			builder.setName("Flag");
			builder.setNamespaceURI("http://localhost/");
			builder.setCRS(CRS.decode("EPSG:4326"));
			builder.add("geom", Point.class);
			builder.add("Name", String.class);

			SimpleFeatureType ftRoad = builder.buildFeatureType();

			GeometryFactory fac = new GeometryFactory();
			SimpleFeature flag1 = SimpleFeatureBuilder.build(ftRoad,
					new Object[] { fac.createPoint(new Coordinate(10, 20)),
							"Here1" }, "flag.1");
			SimpleFeature flag2 = SimpleFeatureBuilder.build(ftRoad,
					new Object[] { fac.createPoint(new Coordinate(12, 20)),
							"Here2" }, "flag.2");
			SimpleFeature flag3 = SimpleFeatureBuilder.build(ftRoad,
					new Object[] { fac.createPoint(new Coordinate(12, 11)),
							"Here3" }, "flag.3");
			fc = new DefaultFeatureCollection("id", ftRoad);
			fc.add(flag1);
			fc.add(flag2);
			fc.add(flag3);

		} catch (NoSuchAuthorityCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FactoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Assert.notNull(fc);
		return fc;
	}
	
/**
 * More generic method to get a feature collection from a List of coorinate[] lists
 * @param geomType
 * @param coords
 * @return
 */
	public static FeatureCollection getMyFC(Class <? extends Geometry> geomType, List<? extends List<Coordinate[]>> coords) {
		FeatureCollection fc = null;
		try {
			SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
			builder.setName("Flag");
			builder.setNamespaceURI("http://localhost/");
			builder.setCRS(CRS.decode("EPSG:4326"));
			builder.add("geom", geomType);
			builder.add("Name", String.class);
			

			SimpleFeatureType ft = builder.buildFeatureType();

			GeometryFactory fac = new GeometryFactory();
		
			
			if (geomType.equals(Point.class)){
					fc = new DefaultFeatureCollection("id", ft);
					for (int i = 0; i<coords.size(); i++){
					SimpleFeature flag1 = SimpleFeatureBuilder.build(ft,
							new Object[] { fac.createPoint(coords.get(i).get(i)[0]), "Here" }, "flag."+i);
					fc.add(flag1);
				}	
			}
			else if (geomType.equals(LineString.class)){
				fc = new DefaultFeatureCollection("id", ft);
				for (int i = 0; i<coords.size(); i++){
					SimpleFeature flag1 = SimpleFeatureBuilder.build(ft,
							new Object[] { fac.createLineString(coords.get(i).get(i)), "Here" }, "flag."+i);
					fc.add(flag1);
				}
			}
			else if (geomType.equals(Polygon.class)){
				//No holes in polygon supported
				fc = new DefaultFeatureCollection("id", ft);
				for (int i = 0; i<coords.size(); i++){
					SimpleFeature flag1 = SimpleFeatureBuilder.build(ft,
							new Object[] { fac.createPolygon(fac.createLinearRing(coords.get(i).get(0)),null), "Here" }, "flag."+i);
					fc.add(flag1);
				}
			}
			else if (geomType.equals(MultiLineString.class)){
				SimpleFeature flag2 = null;
				fc = new DefaultFeatureCollection("id", ft);
				LineString[] ls = null;
				for (int i = 0; i<coords.size(); i++){
					ls = new LineString[coords.get(i).size()];
					for (int z = 0; z<coords.get(i).size(); z++){
						GeometryFactory fac2 = new GeometryFactory();
						LineString l = fac2.createLineString(coords.get(i).get(z));						
						ls[z] = l;					
					}
					flag2 = SimpleFeatureBuilder.build(ft,
							new Object[] { fac.createMultiLineString(ls), "Here" }, "flag."+i);
					fc.add(flag2);
				}
			}
			else if (geomType.equals(MultiPoint.class)){
				SimpleFeature flag2 = null;
				fc = new DefaultFeatureCollection("id", ft);
				Point[] ls = null;
				for (int i = 0; i<coords.size(); i++){
					ls = new Point[coords.get(i).size()];
					for (int z = 0; z<coords.get(i).size(); z++){
						GeometryFactory fac2 = new GeometryFactory();
						Point l = fac2.createPoint(coords.get(i).get(z)[0]);
						ls[z] = l;
					}
					flag2 = SimpleFeatureBuilder.build(ft,
							new Object[] { fac.createMultiPoint(ls), "Here" }, "flag."+i);
					fc.add(flag2);
				}
			}
			else if (geomType.equals(MultiPolygon.class)){
				//Supports no holes
				SimpleFeature flag2 = null;
				fc = new DefaultFeatureCollection("id", ft);
				Polygon[] ls = null;
				for (int i = 0; i<coords.size(); i++){
					ls = new Polygon[coords.get(i).size()];
					for (int z = 0; z<coords.get(i).size(); z++){
						GeometryFactory fac2 = new GeometryFactory();
						Polygon l = fac2.createPolygon(fac.createLinearRing(coords.get(i).get(z)),null);
						ls[z] = l;
					}
					flag2 = SimpleFeatureBuilder.build(ft,
							new Object[] { fac.createMultiPolygon(ls), "Here" }, "flag."+i);
					fc.add(flag2);
				}
			}
			
			

		} catch (NoSuchAuthorityCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FactoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Spatial type: "+geomType.getName()+ " not supported!");
			e.printStackTrace();
		}
		Assert.notNull(fc);
		return fc;
	}
	
	
	public static void featureCollection2GML(FeatureCollection fc, String gmlOutPath){
	
		OutputStream out = null;
		try {
			out = new FileOutputStream(gmlOutPath);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		FeatureTransformer ft = new FeatureTransformer();
		ft.setIndentation(4);
		ft.getFeatureTypeNamespaces().declareDefaultNamespace("xxx","http://somewhere.org");
		try {
			ft.transform(fc, out);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("GML file was written successfully to " + gmlOutPath);
	
	}
	
	
/*	public AlignmentType getAlignment(File file){
		AlignmentType at = null;
		try {
			//System.out.println(AlignmentDocument.Factory.parse(file).getAlignment().getMapArray(0).getCellArray(0));
			
			at = AlignmentDocument.Factory.parse(file).getAlignment(); //.getMapArray(0).getCellArray(0).getOperation());
			
			//System.out.println(AlignmentDocument.Factory.parse(file).getAlignment()
			//Alignment al = AlignmentDocument.Factory.parse(file).getAlignment();
		} catch (XmlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return at;
	}*/

}
