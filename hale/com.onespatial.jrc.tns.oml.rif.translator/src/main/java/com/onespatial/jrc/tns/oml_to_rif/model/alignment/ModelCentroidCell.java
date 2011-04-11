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
package com.onespatial.jrc.tns.oml_to_rif.model.alignment;

import java.util.List;

import com.onespatial.jrc.tns.oml_to_rif.api.TranslationException;
import com.onespatial.jrc.tns.oml_to_rif.model.alignment.ModelAttributeMappingCell;
import com.onespatial.jrc.tns.oml_to_rif.schema.GmlAttributePath;

import eu.esdihumboldt.goml.align.Alignment;
import eu.esdihumboldt.goml.omwg.Restriction;

/**
 * An interim model of a {@link Cell} within an {@link Alignment} that defines a
 * centroid mapping between source and target attributes. Used as a stage in
 * translation to RIF-PRD.
 * 
 * @author Susanne Reinwarth / TU Dresden
 */

public class ModelCentroidCell extends ModelAttributeMappingCell
{	
	private GeometryType geometryType = GeometryType.SURFACE;
	
	public ModelCentroidCell (GmlAttributePath sourceAttribute, GmlAttributePath targetAttribute,
			List<Restriction> filter) throws TranslationException
	{		
		super(sourceAttribute, targetAttribute, filter);
		/*
		 * FIXME fits for German AAA-Model, but there is probably a more sophisticated method
		 *       necessary to determine if the geometry type is a surface or a multi-surface
		 */
		if(sourceAttribute.get(0).getDefinition().getParentType().getSuperType().
				getName().getLocalPart().contains("MultiSurface"))
		{
			geometryType = GeometryType.MULTI_SURFACE;
		}
	}
	
	/**
	 * @return {@link GeometryType}
	 */
	public GeometryType getGeometryType()
	{
		return geometryType;
	}
}
