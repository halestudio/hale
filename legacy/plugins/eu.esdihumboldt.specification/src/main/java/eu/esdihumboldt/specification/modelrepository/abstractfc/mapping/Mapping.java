/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this website:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to : http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.specification.modelrepository.abstractfc.mapping;

import eu.esdihumboldt.specification.modelrepository.abstractfc.ConceptualSchema;
import eu.esdihumboldt.specification.modelrepository.abstractfc.SchemaElement;

/**
 * A {@link Mapping} is a rule that can be used by a {@link ConceptualSchema}
 * translator to transform data available in one {@link ConceptualSchema} into
 * another {@link ConceptualSchema}. It contains rules for processing the
 * structure and the semantic elements.<br/>
 * Concrete Subclasses (Implementations) of this interface are of different
 * types, such as standard relations.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface Mapping extends SchemaElement {

	/**
	 * @return this Mapping's name as a String.
	 */
	public String getName();

	/**
	 * @return true if the provided {@link Mapping} can be used to transform not
	 *         only from source to target but also vice versa.
	 */
	public boolean isBidirectional();

	/**
	 * @return the source {@link SchemaElement} that is being mapped.
	 */
	public SchemaElement getSourceSchemaElement();

	/**
	 * @return the target {@link SchemaElement} that is being mapped.
	 */
	public SchemaElement getTargetSchemaElement();

}
