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
import com.onespatial.jrc.tns.oml_to_rif.schema.GmlAttributePath;

import eu.esdihumboldt.goml.align.Alignment;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.omwg.Restriction;

/**
 * An interim model of a {@link Cell} within an {@link Alignment} that maps
 * source attributes or default values to the INSPIRE Identifier. Used as a
 * stage in translation to RIF-PRD.
 * 
 * @author Susanne Reinwarth / TU Dresden
 */

public class ModelIdentifierCell extends ModelAttributeMappingCell
{
	private String namespace;
	private String versionId;
	private String versionNilReason;
	
	public ModelIdentifierCell(GmlAttributePath sourceId, GmlAttributePath targetAttribute,
			String namespace, String versionId, String versionNilReason,
			List<Restriction> filter) throws TranslationException
	{
		super(sourceId, targetAttribute, filter);
		this.namespace = namespace;
		this.versionId = versionId;
		this.versionNilReason = versionNilReason;
	}
	
	/**
	 * @return {@link String}
	 */
	public String getNamespace()
	{
		return namespace;
	}
	
	/**
	 * @return {@link String}
	 */
	public String getVersionId()
	{
		return versionId;
	}
	
	/**
	 * @return {@link String}
	 */
	public String getVersionNilReason()
	{
		return versionNilReason;
	}
}
