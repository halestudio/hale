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

package eu.esdihumboldt.hale.models;

import java.net.URI;
import java.util.List;

import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.goml.align.Alignment;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.align.Entity;

/**
 * The {@link AlignmentService} provides access to the currently loaded 
 * alignment. The model it uses is directly derived from OML (Ontology
 * Mapping Language)
 * 
 * @author Thorsten Reitz
 * @version {$Id}
 */
public interface AlignmentService 
	extends UpdateService {
	
	/**
	 * @return the entire {@link Alignment} as currently represented in the Alignment Model.
	 */
	public Alignment getAlignment();
	
	/**
	 * @param alignment the {@link Alignment} to update or add to the Alignment Model.
	 * @return true if an existing alignment was updated, false if a new one was added.
	 */
	public boolean addOrUpdateAlignment(Alignment alignment);
	
	/**
	 * This method is used to return all Alignments that have the given type 
	 * as their source or target as a {@link AlignmentDocument}.
	 * @param type the {@link FeatureType} for which to return the Alignments.
	 * @return
	 */
	public List<ICell> getAlignmentForType(FeatureType type);
	
	
	public ICell getAlignmentForType(FeatureType type1, FeatureType type2);
	
	/**
	 * 
	 * @param cell
	 * @return 
	 */
	public boolean addOrUpdateCell(Cell cell);
	
	
	/**
	 * 
	 * @param entity
	 * @return all Cells containing the given Entity as a target Entity if the 
	 * Entity is part of the target schema, or all Cells containing the Entity 
	 * as a source Entity if the Entity is part of the source schema.
	 */
	public List<ICell> getCell(Entity entity);
	
	public ICell getCell(Entity e1, Entity e2);
	
	/**
	 * Adds the alignments defined in an OML file to the currently loaded ones if the alignments
	 * match the currently loaded schemas.
	 * @param file the {@link URI} to the file from which to load alignments.
	 * @return true if the loading was successful.
	 */
	public boolean loadAlignment(URI file);
	
	/**
	 * Invoke this operation if you want to clear out all alignments stored. 
	 * This method is required when one wants to start working on a new alignment.
	 * @return true if the cleaning was successful.
	 */
	public boolean cleanModel();

	/**
	 * Removes the given cell
	 * 
	 * @param cell the cell to remove
	 */
	public void removeCell(ICell cell);

}
