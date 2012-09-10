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

/**
 * A MappingConstraint is a rule that a client can specify which a mapping then
 * has to satisfy.<br/>
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface MappingConstraint {

	/**
	 * There are several types of Mappings, each addressing different aspects of
	 * mapping. These are identified in this enumeration.
	 */
	public enum MappingType {
		/**
		 * data_abstraction: this mapping type is responsible for transforming
		 * from the data abstraction model to the physical schema of a specific
		 * AccessCartridge and vice versa
		 */
		data_abstraction,
		/**
		 * this mapping type is responsible for translating terms from one
		 * natural language, i.e. German, to another, i.e. English. To keep this
		 * mapping type from becoming a thesaurus itself, additional constraints
		 * have to be specified, i.e. the words to be translated or at least the
		 * identifier of a ConceptualSchema.
		 */
		natural_language
	}
}
