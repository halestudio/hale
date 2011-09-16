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

package eu.esdihumboldt.cst.transformer.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.esdihumboldt.commons.goml.omwg.FeatureClass;
import eu.esdihumboldt.cst.transformer.service.rename.RenameFeatureFunction;
import eu.esdihumboldt.specification.cst.align.ICell;
import eu.esdihumboldt.specification.cst.align.ICell.RelationType;
import eu.esdihumboldt.specification.cst.align.ext.ITransformation;

/**
 * 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class RetypeInfo {
	
	private final Logger log = Logger.getLogger(RetypeInfo.class);
	
	private final String source;
	
	private final String target;
	
	private ICell renameCell;
	
	private final Set<ICell> cells = new HashSet<ICell>();
	
	private CellCardinalityType instanceCardinality = null;

	/**
	 * @param source
	 * @param target
	 */
	public RetypeInfo(String source, String target) {
		super();
		
		this.source = source;
		this.target = target;
	}

	/**
	 * Update the mapping's instance cardinality
	 * 
	 * @param cardinality
	 */
	public void updateInstanceCardinality(CellCardinalityType cardinality) {
		if (this.instanceCardinality == null) {
			this.instanceCardinality = cardinality;
		}
		else {
			// instance cardinality was already set
			// only override the default (one_to_one)
			switch (this.instanceCardinality) {
			case one_to_one:
				this.instanceCardinality = cardinality;
				break;
			default:
				log.warn("Instance cardinality was tried to set multiple times"); //$NON-NLS-1$
			}
		}
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @return the target
	 */
	public String getTarget() {
		return target;
	}

	/**
	 * @return the instanceCardinality
	 */
	public CellCardinalityType getInstanceCardinality() {
		return instanceCardinality;
	}

	/**
	 * Add a cell that is relevant to the mapping between the source and target
	 * type
	 * 
	 * @param cell the cell
	 */
	public void addCell(ICell cell) {
		this.cells.add(cell);
		
		// check for rename cell
		if (cell.getRelation() != null && cell.getRelation().equals(RelationType.Equivalence)
				&& cell.getEntity1() instanceof FeatureClass
				&& cell.getEntity2() instanceof FeatureClass) {
			renameCell = cell;
		}
	}

	/**
	 * @return the renameCell
	 */
	public ICell getRenameCell() {
		return renameCell;
	}

	/**
	 * @return the cells
	 */
	public Set<ICell> getCells() {
		return new HashSet<ICell>(cells);
	}

	/**
	 * Get the cells defining an attributive transformation (no retypes and
	 *   augmentations)
	 *   
	 * @see AlignmentIndex#getAttributiveCellsPerEntity(String)
	 *   
	 * @return the cells
	 */
	public Collection<ICell> getAttributiveCells() {
		Collection<ICell> result = new ArrayList<ICell>();
		for (ICell cell : cells) {
			ITransformation t = cell.getEntity1().getTransformation();
			if (t != null && t.getService() != null
					&& !t.getService().getLocation().equals(
							RenameFeatureFunction.class.getName())) {
				result.add(cell);
			}
		}
		return result;
	}
	
}
