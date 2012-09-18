/*
 * LICENSE: This program is being made available under the LGPL 3.0 license.
 * For more information on the license, please read the following:
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * 
 * For additional information on the Model behind Mismatches, please refer to
 * the following publication(s):
 * Thorsten Reitz (2010): A Mismatch Description Language for Conceptual Schema 
 * Mapping and Its Cartographic Representation, Geographic Information Science,
 * http://www.springerlink.com/content/um2082120r51232u/
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
