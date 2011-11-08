/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.common.align.model;

import java.util.Collection;

import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;

/**
 * Alignment model utility methods.
 * @author Simon Templer
 */
public abstract class AlignmentUtil {

	/**
	 * Determines if the given cell is a type cell.
	 * @param cell the cell
	 * @return if the cell is a type cell
	 */
	public static boolean isTypeCell(Cell cell) {
		// check if cell is a type cell
		return cell.getTarget().values().iterator().next() instanceof Type;
	}
	
	/**
	 * Determines if the given alignment has any type relations.
	 * @param alignment the alignment
	 * @return if any type cells are present in the alignment
	 */
	public static boolean hasTypeRelation(Alignment alignment) {
		for (Cell cell : alignment.getCells()) {
			if (isTypeCell(cell)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Determines if the given alignment contains a relation between the
	 * given types.
	 * @param alignment the alignment
	 * @param sourceType the source type, may be <code>null</code> for any 
	 *   source type
	 * @param targetType the target type, may be <code>null</code> for any
	 *   target type 
	 * @return if a relation between the given types exists in the alignment
	 */
	public static boolean hasTypeRelation(Alignment alignment, 
			TypeEntityDefinition sourceType, TypeEntityDefinition targetType) {
		if (sourceType == null && targetType == null) {
			// accept any type relation
			return hasTypeRelation(alignment);
		}
		else if (sourceType == null) {
			// accept any relation to the given target type
			Collection<? extends Cell> cells = alignment.getCells(targetType); //FIXME only target!
			return !cells.isEmpty();
		}
		else if (targetType == null) {
			// accept any relation to the given source type
			Collection<? extends Cell> cells = alignment.getCells(sourceType); //FIXME only source!
			return !cells.isEmpty();
		}
		else {
			// accept relations only if they combine both types
			Collection<? extends Cell> targetCells = alignment.getCells(targetType); //FIXME only target!
			Collection<? extends Cell> sourceCells = alignment.getCells(sourceType); //FIXME only source!
			targetCells.retainAll(sourceCells);
			return !targetCells.isEmpty();
		}
	}
	
	/**
	 * Determines if the given cell is an augmentation.
	 * @param cell the cell
	 * @return if the cell is an augmentation
	 */
	public static boolean isAugmentation(Cell cell) {
		// check if cell is an augmentation cell
		return cell.getSource().isEmpty();
	}

}
