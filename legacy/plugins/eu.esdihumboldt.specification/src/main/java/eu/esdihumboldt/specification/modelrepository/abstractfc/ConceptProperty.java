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

/**
 * A property is used to describe a characteristic of a Concept, such as an
 * Attribute or an Association Role. It is essentially an ISO 19110
 * FC_PropertyType.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface ConceptProperty {

	/**
	 * This interface describes the cardinality of a property. Note that for a
	 * full relation, two cardinalities are required.
	 */
	public interface Cardinality {

		/**
		 * @return the minimum number of occurences allowed. A negative number
		 *         means that the cardinality is 0.
		 */
		public int getMinimum();

		/**
		 * @return the maximum number of occurences allowed. The value
		 *         Integer.MAX_VALUE means an unbounded number of maximum
		 *         occurencies.
		 */
		public int getMaximum();

		/**
		 * @return a String describing this cardinality as defined in UML 2.0,
		 *         i.e. "[1..*]"
		 */
		public String getUMLCardinalityNotation();
	}

	/**
	 * Equal to ISO 19110 FC_PropertyType.memberName.
	 * 
	 * @return the local name of the ConceptProperty, i.e. unique to the Concept
	 *         that this ConceptProperty belongs to.
	 */
	public String getLocalName();

	/**
	 * Equal to ISO 19110 FC_PropertyType.definition.
	 * 
	 * @return a String with a definition of the member in a natural language.
	 */
	public String getDefinition();

	/**
	 * Equal to ISO 19110 FC_PropertyType.cardinality.
	 * 
	 * @return the Cardinality object describing the multiplicity of this
	 *         ConceptProperty. 1/1 will translate to a simple attribute or
	 *         operation, 0..* to a Collection.
	 */
	public Cardinality getCardinality();

}
