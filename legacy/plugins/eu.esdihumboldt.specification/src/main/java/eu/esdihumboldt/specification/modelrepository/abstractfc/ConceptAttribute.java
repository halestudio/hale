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

import java.net.URI;
import java.util.Enumeration;

import org.opengis.metadata.identification.Identification;

/**
 * A ConceptAttribute is just that - a field that each instance of a
 * {@link Concept} has and that is a defining characteristic for it. It is
 * conceptually similar to ISO 19110 FC_FeatureAttribute.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface ConceptAttribute extends ConceptProperty {

	/**
	 * Equal to ISO 19110 FC_FeatureAttribute.code.
	 * 
	 * @return the Identification uniquely identifying this Attribute within
	 *         it's {@link ConceptualSchema}.
	 */
	public Identification getIdentification();

	/**
	 * Equal to ISO 19110 FC_FeatureAttribute.valueMeasurementUnit.
	 * 
	 * @return the commonly agreed shortcode for the unit of measure that this
	 *         ConceptAttribute uses. Examples are m, kg, s, A, K, mol or cd.
	 *         This method may return null in case of unitless values.
	 */
	public String getUnitOfMeasure();

	/**
	 * Equal to ISO 19110 FC_FeatureAttribute.listedValue.
	 * 
	 * @return an Enumeration of allowed values for this ConceptAttribute. This
	 *         operation may return <code>null</code>, in which case the values
	 *         range is unbounded.
	 */
	public Enumeration getPermissibleValues();

	/**
	 * Equal to ISO 19110 FC_FeatureAttribute.valueType.
	 * 
	 * @return the type of the value of this ConceptAttribute, expressed as a
	 *         {@link URI}. Where possible, the type codes from XML Schema have
	 *         to be used, such as xsd:string, xsd:decimal or xsd:date.
	 */
	public URI getValueType();

}
