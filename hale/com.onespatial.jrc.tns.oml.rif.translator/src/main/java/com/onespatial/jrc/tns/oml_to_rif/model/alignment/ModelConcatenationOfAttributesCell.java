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
 * a concatenation of source attributes to a target attribute. Used as a stage
 * in translation to RIF-PRD.
 * 
 * @author Susanne Reinwarth / TU Dresden
 */
public class ModelConcatenationOfAttributesCell extends AbstractModelFilter {
	
	private List<GmlAttributePath> sourceAttributes;
	private GmlAttributePath targetAttribute;
	private String separator;
	private String concatString;
	
	/**
	 * constructor
	 * @param sourceAttributes {@link List}&lt {@link GmlAttributePath}&gt
	 * @param targetAttribute {@link GmlAttributePath}
	 * @param separator separator between the elements in sourceAttributes
	 * @param concatString complete concatenation string
	 * @throws TranslationException 
	 */
	public ModelConcatenationOfAttributesCell(List<GmlAttributePath> sourceAttributes,
			GmlAttributePath targetAttribute, String separator, String concatString,
			List<Restriction> filter) throws TranslationException
	{
		super(filter);
		this.sourceAttributes = sourceAttributes;
		this.targetAttribute = targetAttribute;
		this.separator = separator;
		this.concatString = concatString;
	}
	
	/**
	 * @return {@link List}&lt{@link GmlAttributePath}&gt
	 */
	public List<GmlAttributePath> getSourceAttributes()
	{
		return sourceAttributes;
	}
	
	/**
	 * @return {@link GmlAttributePath}
	 */
	public GmlAttributePath getTargetAttribute()
	{
		return targetAttribute;
	}
	
	/**
	 * @return {@link String}
	 */
	public String getSeparator()
	{
		return separator;
	}
	
	/**
	 * @return {@link String}
	 */
	public String getConcatString()
	{
		return concatString;
	}
}
