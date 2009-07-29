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

package eu.esdihumboldt.goml.oml.io;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.ISchema;
import eu.esdihumboldt.goml.align.Alignment;
import eu.esdihumboldt.goml.align.Formalism;
import eu.esdihumboldt.goml.align.Schema;
import eu.esdihumboldt.goml.generated.AlignmentType;
import eu.esdihumboldt.goml.generated.FormalismType;
import eu.esdihumboldt.goml.generated.OntologyType;
import eu.esdihumboldt.goml.generated.AlignmentType.Map;
import eu.esdihumboldt.goml.generated.AlignmentType.Onto1;
import eu.esdihumboldt.goml.rdf.About;

import java.util.List;
import java.util.UUID;
/**
 * This class reads the OML Rdf Document into Java Object.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class OmlRdfReader {
	/**
	 * Constant defines the path to the alignment jaxb context
	 */
	private static final String ALIGNMENT_CONTEXT = "eu.esdihumboldt.goml.generated";

	/**
	 * Unmarshalls oml-mapping to the HUMBOLDT Alignment.
	 * 
	 * @param rdfFile
	 *            path to the oml-mapping file
	 * @return Alignment object
	 */
	public Alignment read(String rdfFile) {
		// 1. unmarshal rdf
		JAXBContext jc;
		JAXBElement<AlignmentType> root = null;
		try {
			jc = JAXBContext.newInstance(ALIGNMENT_CONTEXT);
            Unmarshaller u = jc.createUnmarshaller();

			// it will debug problems while unmarchalling
            u.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());
            root = u.unmarshal(new StreamSource(new File(rdfFile)),
					AlignmentType.class);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AlignmentType genAlignment = root.getValue();
		System.out.println(genAlignment.getLevel());
		// 2. create humboldt alignment object and fulfill the required fields
		Alignment al = new Alignment();
		//set about
		al.setAbout(new About(UUID.randomUUID()));
		//set level
		al.setLevel(genAlignment.getLevel());
		//set map with cells
		al.setMap(getMap(genAlignment.getMap()));
		//set schema1,2 containing information about ontologies1,2
		al.setSchema1(getSchema(genAlignment.getOnto1().getOntology()));
		al.setSchema2(getSchema(genAlignment.getOnto2().getOntology() ));
		return al;
	}

	/**
	 * converts from JAXB Ontology {@link OntologyType} 
	 * to OML schema   {@link ISchema}
	 * @param onto Ontology
	 * @return schema
	 */
	private ISchema getSchema(OntologyType onto) {
		// create Formalism
		Formalism formalism = getFormalism(onto.getFormalism());
		ISchema schema = new Schema(onto.getLocation(),formalism);
		return schema;
		
	}

	/**
	 * converts from JAXB FromalismType {@link FormalismType}
	 * to OML {@link Formalism} 
	 * @param jaxbFormalism
	 * @return Formalism
	 */
	private Formalism getFormalism(
			eu.esdihumboldt.goml.generated.OntologyType.Formalism jaxbFormalism) {
		FormalismType fType = jaxbFormalism.getFormalism();
		URI uri = null;
		try {
			uri = new URI(fType.getUri());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Formalism formalism = new Formalism(fType.getName(), uri);
		return formalism;
	}

	private List<ICell> getMap(List<Map> map) {
		// TODO Auto-generated method stub
		return null;
	}

}
