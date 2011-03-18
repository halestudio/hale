package test.eu.esdihumboldt.hale.models;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;
import org.geotools.xml.XSISAXHandler;
import org.geotools.xml.gml.GMLComplexTypes;
import org.geotools.xml.schema.ComplexType;
import org.geotools.xml.schema.Element;
import org.geotools.xml.schema.Schema;
import org.opengis.feature.simple.SimpleFeatureType;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class TestFTFromXSD {



	public static void getFeatureType2( String xsd) throws Exception { 
		XMLReader reader = XMLReaderFactory.createXMLReader();
		XSISAXHandler schemaHandler = new XSISAXHandler(new URI(xsd));
		reader.setContentHandler(schemaHandler);
		reader.parse(new InputSource(xsd));
		Schema s = schemaHandler.getSchema();
		System.out.println("FeatureTypes for xsd: " + xsd); //$NON-NLS-1$
		if (s!=null){ 
			if (s.getComplexTypes()!=null) System.out.println(s.getComplexTypes().length); 
			if (s.getAttributes()!=null) System.out.println(s.getAttributes().length); 
		}
		for (ComplexType compType :s.getComplexTypes()){
			System.out.println( compType +  " Type has "); //$NON-NLS-1$
			for (Element child : compType.getChildElements()){
				System.out.println( " Element name: " + child.getName() + " type :" + child.getType()); //$NON-NLS-1$ //$NON-NLS-2$
				
			}
		}
		
		// =schemaHandler.getSchema().getElements();
		//for (int i=0; i<count;i++){
		/*SimpleFeatureType ft =
	   GMLComplexTypes.createFeatureType(schemaHandler.getSchema().getElements()[0]);		
		System.out.println("FeatureType : " + ft.getName() + " GeometryDescriptor " + ft.getGeometryDescriptor());*/
		//}
	}
	public static void main(String [] args){
		try {
			String pathToSourceSchema = "resources/INSPIRE_Conf_Data/Watercourse/BY/SourceSchema/Watercourses_BY.xml" ; //$NON-NLS-1$
	    	String pathToSecondSourceSchema = "resources/INSPIRE_Conf_Data/Watercourse/VA/SourceSchema/Watercourses_VA.xml" ; //$NON-NLS-1$
			//getFeatureType2("file:///D:/HUMBOLDT/HALE/workspace/HALE-Client/resources/schema/source/roadsGermany212.xsd");
	    	getFeatureType2(pathToSourceSchema);
	    	getFeatureType2(pathToSourceSchema);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
