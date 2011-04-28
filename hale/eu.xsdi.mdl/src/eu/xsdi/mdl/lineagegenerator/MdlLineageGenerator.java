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

package eu.xsdi.mdl.lineagegenerator;

import java.net.URL;
import java.util.List;

import org.opengis.feature.Feature;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import eu.xsdi.mdl.model.Mismatch;

/**
 * This class uses MDL elements to create an OWL lineage. 
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 * @since 0.1.0
 */
public class MdlLineageGenerator {
	
	/**
	 * @param mismatches the {@link List} of mismatches that should be 
	 * documented in the lineage
	 * @param f the source Feature, used for gathering provenance information
	 */
	public void generateLineage(List<Mismatch> mismatches, Feature f) {
		
		URL provenanceLocation = MdlLineageGenerator.class.getResource(
				"provenance.rdf");
		
		OntModel lineage = ModelFactory.createOntologyModel();
		lineage.read("" + provenanceLocation);
		
		OntClass dataItemClass = lineage.getOntClass("http://purl.org/net/provenance/ns#DataItem");
		
		for (OntProperty op : dataItemClass.listDeclaredProperties().toList()) {
			System.out.println(op.getLocalName());
		}
		
		// first, collect information about the original data item (the source feature).
		Individual name1individual = dataItemClass.createIndividual(
				f.getType().getName().getNamespaceURI() + ":" 
				+ f.getType().getName().getLocalPart() + ":" 
				+ f.getIdentifier().getID());
		
		System.out.println(name1individual.getURI());
		
		// collect information about the transformed feature
		
		// TODO
		
		// attach a precededBy property to the transformed feature, identifying the
		// source feature as an earlier version
		
		// TODO
		
	}

}
