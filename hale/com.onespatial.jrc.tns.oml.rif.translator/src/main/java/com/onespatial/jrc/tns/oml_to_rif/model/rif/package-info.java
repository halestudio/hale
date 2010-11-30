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

/**
 * Provides classes that model an interim state of the translation when it has
 * progressed two stages on from an {@link eu.esdihumboldt.goml.align.Alignment}
 * , such that the structure of the schema mapping at this stage is close to
 * that of a RIF-PRD {@link org.w3._2007.rif.Sentence}. The basic unit of
 * mapping is, accordingly, now the sentence (one of which which can contain
 * multiple GOML {@link eu.esdihumboldt.goml.align.Cell} instances). However, it
 * is still one stage removed from actual RIF as it lacks the syntactic elements
 * of the format.
 */
package com.onespatial.jrc.tns.oml_to_rif.model.rif;

