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
 * This type of {@link Relation} is used to describe is-a relationships
 * (subsumption). A {@link InheritanceRelation} is identical to the ISO 19110
 * FC_InheritanceRelation.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface InheritanceRelation extends Relation {

	/**
	 * Similar to ISO 19110 FC_InheritanceRelation.uniqueInstance.
	 * 
	 * @return true if an instance of the supertype can be an instance of at
	 *         most one of its subtypes.
	 */
	public boolean isUniqueInstance();

	/**
	 * Similar to ISO 19110 FC_InheritanceRelation.subtype.
	 * 
	 * @return the {@link Concept} that inherits from the supertype concept.
	 */
	public Concept getSubtype();

	/**
	 * Similar to ISO 19110 FC_InheritanceRelation.supertype.
	 * 
	 * @return the {@link Concept} from which the subtype inherits properties.
	 */
	public Concept getSupertype();

}
