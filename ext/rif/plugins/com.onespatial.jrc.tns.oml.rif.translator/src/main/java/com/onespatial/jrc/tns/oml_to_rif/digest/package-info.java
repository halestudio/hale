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

