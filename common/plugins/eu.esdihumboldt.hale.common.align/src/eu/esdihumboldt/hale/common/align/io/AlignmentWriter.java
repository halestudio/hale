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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.align.io;

import java.net.URI;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.core.io.ExportProvider;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;

/**
 * Provides support for writing alignments
 * 
 * @author Simon Templer
 */
public interface AlignmentWriter extends ExportProvider {

	/**
	 * Set the alignment to write.
	 * 
	 * @param alignment the alignment
	 */
	public void setAlignment(Alignment alignment);

	/**
	 * Set the source schema associated to the alignment.
	 * 
	 * @param source the source schema
	 */
	public void setSourceSchema(SchemaSpace source);

	/**
	 * Set the target schema associated to the alignment.
	 * 
	 * @param target the target schema
	 */
	public void setTargetSchema(SchemaSpace target);

	/**
	 * Sets the previous target if the project was saved before. May be the
	 * project file or the alignment file.
	 * 
	 * @param previousTarget the previous target
	 */
	public void setPreviousTarget(URI previousTarget);

}
