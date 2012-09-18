/*
 * LICENSE: This program is being made available under the LGPL 3.0 license.
 * For more information on the license, please read the following:
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * 
 * For additional information on the Model behind Mismatches, please refer to
 * the following publication(s):
 * Thorsten Reitz (2010): A Mismatch Description Language for Conceptual Schema 
 * Mapping and Its Cartographic Representation, Geographic Information Science,
 * http://www.springerlink.com/content/um2082120r51232u/
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
