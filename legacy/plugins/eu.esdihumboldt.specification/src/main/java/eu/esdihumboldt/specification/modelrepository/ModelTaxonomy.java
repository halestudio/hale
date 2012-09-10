/*
 * HUMBOLDT: A Framework for Data Harmonistation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this website:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to : http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.specification.modelrepository;

import java.util.List;

import eu.esdihumboldt.specification.modelrepository.abstractfc.ConceptualSchema;

/**
 * Name: eu.esdihumboldt.modelrepository.abstractfc / ModelTaxonomy <br/>
 * Purpose: The InformationModels are structured according to a simple taxonomy
 * in the prototype.<br/>
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface ModelTaxonomy {

	/**
	 * Retrieve ConceptualSchema(s) chained to a specific ModelTaxonomyNode in
	 * the ModelTaxonomy.
	 * 
	 * @param _node
	 *            a ModelTaxonomyNode out of this Service's ModelTaxonomy.
	 * @return a List of InformationModels directly attached to the given node.
	 */
	public List<ConceptualSchema> getInformationModelsUnderNode(
			ModelTaxonomyNode _node);

}
