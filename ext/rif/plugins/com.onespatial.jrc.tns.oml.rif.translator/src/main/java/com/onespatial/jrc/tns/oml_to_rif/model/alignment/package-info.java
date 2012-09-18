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
 * progressed one stage on from an {@link eu.esdihumboldt.commons.goml.align.Alignment},
 * such that a first stage of refactoring has been done to the structure of the
 * data within the schema mapping. Nevertheless, the structure of the model at
 * this stage is close to that of the original
 * {@link eu.esdihumboldt.commons.goml.align.Alignment}. The basic unit of mapping is
 * still on the level of the cell.
 */
package com.onespatial.jrc.tns.oml_to_rif.model.alignment;

