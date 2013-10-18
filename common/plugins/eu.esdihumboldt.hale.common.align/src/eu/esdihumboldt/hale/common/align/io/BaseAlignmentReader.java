/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.align.io;

import java.net.URI;

import eu.esdihumboldt.hale.common.align.model.MutableAlignment;
import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * Provides support for reading a base alignment.
 * 
 * @author Kai Schwierczek
 */
public interface BaseAlignmentReader extends ImportProvider {

	/**
	 * Set the source schema
	 * 
	 * @param sourceSchema the source schema
	 */
	public void setSourceSchema(TypeIndex sourceSchema);

	/**
	 * Set the target schema
	 * 
	 * @param targetSchema the source schema
	 */
	public void setTargetSchema(TypeIndex targetSchema);

	/**
	 * Set the alignment to be expanded
	 * 
	 * @param alignment the alignment
	 */
	public void setAlignment(MutableAlignment alignment);

	/**
	 * Set the project location (if available).
	 * 
	 * @param location the project location
	 */
	public void setProjectLocation(URI location);
}
