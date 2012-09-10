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
package eu.esdihumboldt.specification.dataaccess.abstractionmodel;

import java.util.Set;

import org.opengis.metadata.MetaData;

import eu.esdihumboldt.specification.modelrepository.abstractfc.Concept;
import eu.esdihumboldt.specification.modelrepository.abstractfc.ConceptProperty;

/**
 * A {@link BasicElement} is a representation of single data element that is
 * part of a {@link AbstractedDataSet}. This can range from a simple primitive
 * value to a full Feature, as described in OGC GML.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface BasicElement extends DAMObject {

	/**
	 * @return the {@link Concept} that gives this {@link BasicElement}
	 *         Properties and defines it's classification.
	 */
	public Concept getElementConcept();

	/**
	 * @return the {@link MetaData} describing the source and quality of this
	 *         {@link BasicElement}. MetaData returned by the object will
	 *         overwrite similar entries in the MetaData returned for the
	 *         {@link AbstractedDataSet}.
	 */
	public MetaData getMetaData();

	/**
	 * @return the defined Property fields and values (i.e. attached data) of
	 *         this {@link BasicElement}.
	 */
	public Set<ConceptProperty> getProperties();
}
