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
 * Provides classes to handle initial processing of an
 * {@link eu.esdihumboldt.commons.goml.align.Alignment} into its intermediate
 * {@link com.onespatial.jrc.tns.oml_to_rif.model.alignment.ModelAlignment}
 * format. The
 * {@link com.onespatial.jrc.tns.oml_to_rif.digest.UrlToAlignmentDigester}
 * enables an {@link eu.esdihumboldt.commons.goml.align.Alignment} to be read from a
 * {@link java.net.URL}. The
 * {@link com.onespatial.jrc.tns.oml_to_rif.digest.CqlToMappingConditionTranslator}
 * processes CQL predicates into their intermediate format as
 * {@link com.onespatial.jrc.tns.oml_to_rif.model.alignment.ModelMappingCondition}
 * s.
 */
package com.onespatial.jrc.tns.oml_to_rif.digest;

