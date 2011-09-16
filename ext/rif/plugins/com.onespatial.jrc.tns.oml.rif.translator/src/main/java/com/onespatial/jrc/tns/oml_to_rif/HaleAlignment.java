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
package com.onespatial.jrc.tns.oml_to_rif;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import eu.esdihumboldt.commons.goml.align.Alignment;
import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;

/**
 * Combines a OML {@link Alignment} with schema information from HALE
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class HaleAlignment {
	
	private final Alignment alignment;
	
	private final Map<String, SchemaElement> sourceElements = new HashMap<String, SchemaElement>();
	
	private final Map<String, SchemaElement> targetElements = new HashMap<String, SchemaElement>();
	
	/**
	 * Creates a HALE alignment
	 * 
	 * @param alignment the OML mapping
	 * @param sourceSchema the source schema elements
	 * @param targetSchema the target schema elements
	 */
	public HaleAlignment(Alignment alignment, Collection<? extends SchemaElement> sourceSchema,
			Collection<? extends SchemaElement> targetSchema) {
		super();
		
		this.alignment = alignment;
		
		for (SchemaElement element : sourceSchema) {
			sourceElements.put(element.getIdentifier(), element);
		}
		
		for (SchemaElement element : targetSchema) {
			targetElements.put(element.getIdentifier(), element);
		}
	}

	/**
	 * @return the alignment
	 */
	public Alignment getAlignment() {
		return alignment;
	}

	/**
	 * @return the sourceElements
	 */
	public Map<String, SchemaElement> getSourceElements() {
		return sourceElements;
	}

	/**
	 * @return the targetElements
	 */
	public Map<String, SchemaElement> getTargetElements() {
		return targetElements;
	}

}
