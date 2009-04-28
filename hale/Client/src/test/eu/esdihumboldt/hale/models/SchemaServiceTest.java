package test.eu.esdihumboldt.hale.models;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.xml.SchemaFactory;
import org.geotools.xml.schema.Attribute;
import org.geotools.xml.schema.ComplexType;
import org.geotools.xml.schema.Element;
import org.geotools.xml.schema.Schema;
import org.geotools.xml.xLink.XLinkSchema;
import org.junit.Test;
import org.xml.sax.SAXException;

import eu.esdihumboldt.hale.models.impl.SchemaParser;
import eu.esdihumboldt.hale.models.impl.SchemaServiceImpl;

/**
 * Unit tests which covers the SchemaService implementation class.
 */
public class SchemaServiceTest {

//	private Element[] printElements(Element element, int level) {
//		
//		for (int i = 0; i < 0; i++) {
//			System.out.print("\t");
//		}
//		System.out.println(element.getName());
//		if (element.get)
//	}
	
	//@Test
	public void testLoadSourceSchema() throws URISyntaxException, SAXException, FileNotFoundException {
		
		InputStream is = new FileInputStream("resources/schema/source/roadsGermany212.xsd");
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
		//test download subschema 
//    	String pathToSourceSchema = "resources/schema/source/roadsGermany212.xsd";
    	String pathToSourceSchema = "resources/schema/inheritance/rise_hydrography.xsd";
		SchemaServiceImpl service = (SchemaServiceImpl) SchemaServiceImpl.getInstance();
//		service.loadSourceSchema(pathToSourceSchema);
//		String pathToSourceSchema = "resources/schema/inheritance/rise_hydrography.xsd";
		service.loadSourceSchema(pathToSourceSchema);
		
		
	}
	
	
	
	

}
