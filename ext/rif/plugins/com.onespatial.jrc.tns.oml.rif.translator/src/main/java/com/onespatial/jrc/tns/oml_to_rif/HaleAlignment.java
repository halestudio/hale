/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     1Spatial PLC <http://www.1spatial.com>
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
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
