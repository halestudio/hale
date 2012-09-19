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
 * Provides classes that model an interim state of the translation when it has
 * progressed two stages on from an {@link eu.esdihumboldt.commons.goml.align.Alignment}
 * , such that the structure of the schema mapping at this stage is close to
 * that of a RIF-PRD {@link org.w3._2007.rif.Sentence}. The basic unit of
 * mapping is, accordingly, now the sentence (one of which which can contain
 * multiple GOML {@link eu.esdihumboldt.commons.goml.align.Cell} instances). However, it
 * is still one stage removed from actual RIF as it lacks the syntactic elements
 * of the format.
 */
package com.onespatial.jrc.tns.oml_to_rif.model.rif;

