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

package eu.esdihumboldt.hale.common.align.io.impl;

import java.net.URI;

import eu.esdihumboldt.hale.common.align.io.AlignmentWriter;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractExportProvider;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;

/**
 * Abstract alignment writer implementation
 * 
 * @author Simon Templer
 */
public abstract class AbstractAlignmentWriter extends AbstractExportProvider implements
		AlignmentWriter {

	private Alignment alignment;
	private SchemaSpace sourceSchema;
	private SchemaSpace targetSchema;
	private URI previousTarget;

	/**
	 * @see AlignmentWriter#setAlignment(Alignment)
	 */
	@Override
	public void setAlignment(Alignment alignment) {
		this.alignment = alignment;
	}

	/**
	 * @see AlignmentWriter#setSourceSchema(SchemaSpace)
	 */
	@Override
	public void setSourceSchema(SchemaSpace sourceSchema) {
		this.sourceSchema = sourceSchema;
	}

	/**
	 * @see AlignmentWriter#setTargetSchema(SchemaSpace)
	 */
	@Override
	public void setTargetSchema(SchemaSpace targetSchema) {
		this.targetSchema = targetSchema;
	}

	/**
	 * Get the alignment
	 * 
	 * @return the alignment to write
	 */
	protected Alignment getAlignment() {
		return alignment;
	}

	/**
	 * Get the source schema.
	 * 
	 * @return the source schema
	 */
	protected SchemaSpace getSourceSchema() {
		return sourceSchema;
	}

	/**
	 * Get the source schema.
	 * 
	 * @return the target schema
	 */
	protected SchemaSpace getTargetSchema() {
		return targetSchema;
	}

	/**
	 * Checks for the alignment being available.
	 * 
	 * @see AbstractExportProvider#validate()
	 */
	@Override
	public void validate() throws IOProviderConfigurationException {
		super.validate();

		if (alignment == null) {
			fail("Alignment to write not set");
		}
	}

	/**
	 * @see AlignmentWriter#setPreviousTarget(URI)
	 */
	@Override
	public void setPreviousTarget(URI previousTarget) {
		this.previousTarget = previousTarget;
	}

	/**
	 * Returns the previous target of the project. May be <code>null</code>.
	 * 
	 * @return the previous target of the project. May be <code>null</code>
	 */
	protected URI getPreviousTarget() {
		return previousTarget;
	}

}
