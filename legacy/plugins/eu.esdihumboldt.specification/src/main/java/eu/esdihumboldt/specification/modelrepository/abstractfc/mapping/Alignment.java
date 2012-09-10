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
package eu.esdihumboldt.specification.modelrepository.abstractfc.mapping;

import java.util.Set;

import org.opengis.metadata.MetaData;

import eu.esdihumboldt.specification.modelrepository.abstractfc.ConceptualSchema;
import eu.esdihumboldt.specification.modelrepository.abstractfc.SchemaElement;

/**
 * An {@link Alignment} collects all {@link Mapping}s between two given
 * {@link ConceptualSchema}s.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface Alignment extends SchemaElement {

	/**
	 * @return this {@link Alignment}'s name as a String.
	 */
	public String getName();

	/**
	 * @return this {@link Alignment}'s {@link MetaData} structure according to
	 *         ISO19115.
	 */
	public MetaData getMetaData();

	/**
	 * @return the {@link Set} of {@link Mapping}s that have been created
	 *         between the two {@link ConceptualSchema}s connected by this
	 *         {@link Alignment}.
	 */
	public Set<Mapping> getMappings();

	/**
	 * @return the source {@link ConceptualSchema} that is being mapped.
	 */
	public ConceptualSchema getSourceSchema();

	/**
	 * @return the target {@link ConceptualSchema} that is being mapped.
	 */
	public ConceptualSchema getTargetSchema();

}
