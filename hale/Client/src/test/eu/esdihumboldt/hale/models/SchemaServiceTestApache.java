package test.eu.esdihumboldt.hale.models;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.xml.SchemaFactory;
import org.geotools.xml.schema.Attribute;
import org.geotools.xml.schema.ComplexType;
import org.geotools.xml.schema.Element;
import org.geotools.xml.schema.Schema;
import org.geotools.xml.xLink.XLinkSchema;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;

import org.xml.sax.SAXException;

import eu.esdihumboldt.hale.models.impl.SchemaParser;
import eu.esdihumboldt.hale.models.impl.SchemaServiceImpl;
import eu.esdihumboldt.hale.models.impl.SchemaServiceImplApache;

/**
 * Unit tests which covers the SchemaService implementation class.
 */
public class SchemaServiceTestApache {

	
	//@Test
	public void testLoadSourceSchema() throws URISyntaxException, SAXException, FileNotFoundException {
		
		InputStream is = new FileInputStream("resources/schema/inheritance/roadsGe.xsd");
		Schema schema = SchemaFactory.getInstance(null, is);
		Schema[] schemas = schema.getImports();
		Schema[] schemas2 = schemas[0].getImports();
		
		URI uri =  ((XLinkSchema)schemas[0].getImports()[0]).getURI();
		
		for (Element element : schema.getElements())
		{
			System.out.println(element.getName());
			
		
			SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
			builder.setName(element.getName());
			builder.setNamespaceURI(element.getNamespace());
			
		}
		for (ComplexType type : schema.getComplexTypes() )
		{
			System.out.println(type.getName() + " " + type.getChildElements().length );
			for (Element element : type.getChildElements()) {
				System.out.println("\te " + element.getName());
			}
			for (Attribute attribue : type.getAttributes()) {
				System.out.println("\ta " + attribue.getName());
			}
		}
	
		
		
		SchemaServiceImpl service = (SchemaServiceImpl) SchemaServiceImpl.getInstance();
//		URI file = new URI("schema/target/Roadlink.xsd");
		URI file = new URI("resources/schema/inheritance/rise_hydrography.xsd");
//		URI file = new URI("schema/source/roadsGermany212.xsd");
		
//		SchemaParser parser = new SchemaParser();
//		List<String> schemaList = parser.parse("resources/schema/source/roadsGermany212.xsd");
//		for (String s : schemaList) {
//			System.out.println("schemaLocation: " + s);
//		}
		
//		service.findImports(file);
		
		service.loadSourceSchema(file);
	}

	//@Test
	public void testLoadTargetSchema() {
	}
	@Test
	public void testLoadSourceSchemawithImport(){
		
        
//    	String pathToSourceSchema = "resources/schema/inheritance/rise_hydrography.xsd";
    	String pathToSourceSchema = "D:/Humboldt/workspace/HALE/resources/schema/inheritance/rise_hydrography.xsd";
//    	String pathToSourceSchema = "D:/Humboldt/workspace/HALE/resources/D2.8-I_GML-Application-Schemas_v2.0-GML3.1.1/HY/Hydrography.xsd";
    	SchemaServiceImplApache service = (SchemaServiceImplApache) SchemaServiceImplApache.getInstance();
    	SchemaServiceImpl service2 = (SchemaServiceImpl) SchemaServiceImpl.getInstance();


		//load schema 
		try {
			service.loadSourceSchema(new URI(pathToSourceSchema));
//			service2.loadSourceSchema(new URI(pathToSourceSchema));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//getting service schema
		Collection<FeatureType> featureTypes = service.getSourceSchema();
//		Collection<FeatureType> featureTypes2 = service2.getSourceSchema();
		//check size
		assertEquals(featureTypes.size(), 11);
		
		
		
		//check if countains RiverBasinDistrictType
		boolean containsType = false;
		Iterator iterator = featureTypes.iterator();
		while(iterator.hasNext()){
			FeatureType type = (FeatureType) iterator.next();
			if (type.getName().getLocalPart().equals("RiverBasinDistrictType"))containsType = true;
		} 
		assertEquals(true, containsType);
	}
	

}
