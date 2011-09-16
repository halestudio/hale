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

package eu.xsdi.mdl.identifier;

import java.util.Collection;
import java.util.List;

import org.opengis.feature.Feature;

import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;
import eu.xsdi.mdl.model.Mismatch;
import eu.xsdi.mdl.model.MismatchCell;

/**
 * The {@link MismatchIdentifier} is a set-based reasoner that allows 
 * identifying potential mismatches.
 * 
 * @author Thorsten Reitz
 * @version $Id$ 
 * @since 0.1.0
 */
public class MismatchIdentifier {

	/**
	 * Identify {@link Mismatch}es solely based on Cell information. i.e. based 
	 * on the type of mapping performed.
	 * @param cell the Cell for which to identify {@link Mismatch}es. May not be null.
	 * @return a {@link List} of the identified {@link Mismatch}es.
	 */
	public List<Mismatch> identifyMismatches(MismatchCell cell) {
		MismatchRuleRepository.getCellbasedRules();
		return null;
	}
	
	/**
	 * Identify {@link Mismatch}es based on Cell and Schema information.
	 * @param cell the Cell for which to identify {@link Mismatch}es. May not 
	 * be null.
	 * @param sourceType the {@link TypeDefinition} for the mapped source 
	 * element. May not be null.
	 * @param targetType the {@link TypeDefinition} for the mapped target 
	 * element. May not be null.
	 * @return a {@link List} of the identified {@link Mismatch}es.
	 */
	public List<Mismatch> identifyMismatches(
			MismatchCell cell, 
			TypeDefinition sourceType, 
			TypeDefinition targetType) {
		return null;
	}
	
	/**
	 * Identify {@link Mismatch}es based on Cell, Schema and Instance information.
	 * @param cell the Cell for which to identify {@link Mismatch}es. May not 
	 * be null.
	 * @param sourceType the {@link TypeDefinition} for the mapped source 
	 * element. May not be null.
	 * @param targetType the {@link TypeDefinition} for the mapped target 
	 * element. May not be null.
	 * @param sourceFeatures a {@link Collection} of {@link Feature}s of the 
	 * sourceType. May be null.
	 * @param targetFeatures a {@link Collection} of {@link Feature}s of the 
	 * targetType. May be null.
	 * @return a {@link List} of the identified {@link Mismatch}es.
	 */
	public List<Mismatch> identifyMismatches(
			MismatchCell cell, 
			TypeDefinition sourceType, 
			TypeDefinition targetType, 
			Collection<Feature> sourceFeatures, 
			Collection<Feature> targetFeatures) {
		return null;
	}
	
}
