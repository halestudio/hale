/*
 * Copyright (c) 1Spatial Group Ltd.
 */

/**
 * Provides classes to handle initial processing of an
 * {@link eu.esdihumboldt.goml.align.Alignment} into its intermediate
 * {@link com.onespatial.jrc.tns.oml_to_rif.model.alignment.ModelAlignment}
 * format. The
 * {@link com.onespatial.jrc.tns.oml_to_rif.digest.UrlToAlignmentDigester}
 * enables an {@link eu.esdihumboldt.goml.align.Alignment} to be read from a
 * {@link java.net.URL}. The
 * {@link com.onespatial.jrc.tns.oml_to_rif.digest.CqlToMappingConditionTranslator}
 * processes CQL predicates into their intermediate format as
 * {@link com.onespatial.jrc.tns.oml_to_rif.model.alignment.ModelMappingCondition}
 * s.
 */
package com.onespatial.jrc.tns.oml_to_rif.digest;

