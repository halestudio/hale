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
 * Name: eu.esdihumboldt.modelrepository.abstractfc / ModelTaxonomyNode<br/>
 * Purpose: A node in the ModelTaxonomy. The general rule should be that the
 * ModelTaxonomyNode represent the inner nodes, while InformationModels are the
 * leaf nodes and the ModelTaxonomy represents the root node.<br/>
 * 
 * The graph produced with these ModelTaxonomyNodes is directed, acyclic and
 * monotonous, i.e. a Tree.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface ModelTaxonomyNode {

	/**
	 * @return all children ModelTaxonomyNodes attached to this
	 *         ModelTaxonomyNode.
	 */
	public List<ModelTaxonomyNode> getChildren();

	/**
	 * @return all InformationModels attached to this ModelTaxonomyNode.
	 */
	public List<ConceptualSchema> getInformationModels();

}
