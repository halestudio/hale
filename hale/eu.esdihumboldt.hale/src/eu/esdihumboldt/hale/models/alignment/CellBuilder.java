/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.models.alignment;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import eu.esdihumboldt.commons.goml.align.Cell;
import eu.esdihumboldt.commons.goml.align.Entity;
import eu.esdihumboldt.commons.goml.oml.ext.Transformation;
import eu.esdihumboldt.specification.cst.align.ICell;
import eu.esdihumboldt.specification.cst.align.ext.IParameter;
import eu.esdihumboldt.specification.cst.align.ext.ITransformation;

/**
 * This is a utility and helper class that allows to build cells for an 
 * alignment very easily, usually by providing the source and target 
 * {@link Entity} and an identifier for the relation or transformation to use.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class CellBuilder {
	
	/**
	 * Build a cell
	 * 
	 * @param entity1 the source entity
	 * @param entity2 the target entity
	 * @param transformation the transformation
	 * 
	 * @return the cell
	 */
	public static ICell getCell(
			Entity entity1, Entity entity2, Transformation transformation) {
		Cell cell = new Cell();
		List<String> cellLabels = new ArrayList<String>();
		cellLabels.add(UUID.randomUUID().toString());
		cell.setLabel(cellLabels);
		entity1.setTransformation(transformation);
		cell.setEntity1(entity1);
		cell.setEntity2(entity2);
		return cell;
	}
	
	/**
	 * Build a transformation
	 * 
	 * @param transformationID the transformation id
	 * @param parameters the transformation parameters
	 * 
	 * @return the transformation
	 */
	public static ITransformation getTransformation(
			String transformationID, List<IParameter> parameters) {
		Transformation t = new Transformation();
		t.setLabel(transformationID);
		t.setParameters(parameters);
		return t;
	}
	
}
