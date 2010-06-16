package eu.esdihumboldt.hale.eap2uml.xsltprocess;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import java.net.*;
import java.io.*;


public class xsltprocess {
	public static void main(String[] args)
	{
	try {

	    TransformerFactory tFactory = TransformerFactory.newInstance();

	    Transformer transformer;
	    StreamSource source;
	    StreamResult result;
	    
	    // WORKING SIMPLE XSLT --> howto.xslt
	   // transformer=tFactory.newTransformer(new javax.xml.transform.stream.StreamSource("src/main/java/eu/esdihumboldt/hale/eap2uml/xsltprocess/howto.xslt"));
	    
	    // FAILING XSLT --> xmi2gml
	    transformer =tFactory.newTransformer(new javax.xml.transform.stream.StreamSource("src/main/java/eu/esdihumboldt/hale/eap2uml/xsltprocess/xmi2gml.xslt"));

	    // WORKING SIMPLE SOURCE -->howto.xml
	    //source = new javax.xml.transform.stream.StreamSource("src/main/java/eu/esdihumboldt/hale/eap2uml/xsltprocess/howto.xml");
	    
	    // SOURCE FOR FAILING XSLT --> model2.xmi
	    source = new javax.xml.transform.stream.StreamSource("src/main/java/eu/esdihumboldt/hale/eap2uml/xsltprocess/model2.xmi");
	    
	    // RESULT FILE --> howto.html
	    result = new javax.xml.transform.stream.StreamResult( new FileOutputStream("src/main/java/eu/esdihumboldt/hale/eap2uml/xsltprocess/howto.html"));
	    
	    transformer.transform(source, result);
	    }
	  catch (Exception e) {
	    e.printStackTrace( );
	    }
	}
}
