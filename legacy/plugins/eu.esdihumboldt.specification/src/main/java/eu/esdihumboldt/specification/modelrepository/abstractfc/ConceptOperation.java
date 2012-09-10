/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the project web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.specification.modelrepository.abstractfc;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * A ConceptOperation describes an operation that every instance of an
 * associated Concept has to implement. It is similar to ISO 19110's
 * FC_FeatureOperation.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface ConceptOperation extends ConceptProperty {

	/**
	 * Equal to ISO 19110 FC_FeatureOperation.signature and FC_FeatureOperation.
	 * formalDefintion (the functionalLanguage is Java here).
	 * 
	 * @return the signature of this ConceptOperation, expressed as a
	 *         {@link Method} object.
	 */
	public Method getSignature();

	/**
	 * Equal to ISO 19110 FC_FeatureOperation.observesValuesOf
	 * 
	 * @return a Set of {@link ConceptAttribute} objects that serve as input for
	 *         this ConceptOperation.
	 */
	public Set<ConceptAttribute> getInputAttributes();

	/**
	 * Equal to ISO 19110 FC_FeatureOperation.affectsValuesOf
	 * 
	 * @return a Set of {@link ConceptAttribute} objects that serve as output
	 *         targets for this ConceptOperation.
	 */
	public Set<ConceptAttribute> getOutputAttributes();
}
