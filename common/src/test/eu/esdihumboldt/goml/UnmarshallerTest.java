package test.eu.esdihumboldt.goml;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;


import java.io.File;

import javax.xml.transform.stream.StreamSource;

import eu.esdihumboldt.goml.generated.AlignmentType;



/*
 * HUMBOLDT: A Framework for Data Harmonistation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this website:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to : http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2008 to 2010.
 */

/**
 * @author Anna Pitaev, Logica
 *
 */
public class UnmarshallerTest {
public static void main(String args []){
	//unamarchall xmltransformer to workflow
	try{
	JAXBContext jc = JAXBContext.newInstance( "eu.esdihumboldt.goml.generated" );
		//System.out.println(jc.toString());
	
       Unmarshaller u = jc.createUnmarshaller();
       //it will debug problems while unmarchalling
       u.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());
       JAXBElement<AlignmentType> root = u.unmarshal(new StreamSource(new File("res/schema/WatercoursesBY2Inspire.xml")),AlignmentType.class);
       AlignmentType genAlignment = root.getValue();
       System.out.println(genAlignment.getLevel());
	}catch (Exception e){
		e.printStackTrace();
	}
	
	
}
}	



