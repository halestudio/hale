/*
 * Copyright (c) 1Spatial Group Ltd.
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

