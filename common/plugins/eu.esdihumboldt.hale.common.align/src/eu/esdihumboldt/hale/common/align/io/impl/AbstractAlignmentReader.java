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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import eu.esdihumboldt.hale.common.align.io.AlignmentReader;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.MutableAlignment;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.functions.AssignFunction;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultCell;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.PathUpdate;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractImportProvider;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * Abstract alignment reader implementation
 * 
 * @author Simon Templer
 */
public abstract class AbstractAlignmentReader extends AbstractImportProvider implements
		AlignmentReader {

	private TypeIndex sourceSchema;
	private TypeIndex targetSchema;
	private PathUpdate updater;
	private MutableAlignment alignment;

	/**
	 * @see AlignmentReader#setSourceSchema(TypeIndex)
	 */
	@Override
	public void setSourceSchema(TypeIndex sourceSchema) {
		this.sourceSchema = sourceSchema;
	}

	/**
	 * Get the source schema
	 * 
	 * @return the source schema
	 */
	public TypeIndex getSourceSchema() {
		return sourceSchema;
	}

	/**
	 * @see AlignmentReader#setTargetSchema(TypeIndex)
	 */
	@Override
	public void setTargetSchema(TypeIndex targetSchema) {
		this.targetSchema = targetSchema;
	}

	/**
	 * Get the target schema
	 * 
	 * @return the target schema
	 */
	public TypeIndex getTargetSchema() {
		return targetSchema;
	}

	@Override
	public MutableAlignment getAlignment() {
		return alignment;
	}

	/**
	 * Load the alignment. Success or failure should be reported via the given
	 * reporter.
	 * 
	 * @param progress the progress indicator
	 * @param reporter the reporter
	 * @return the alignment or <code>null</code>
	 * @throws IOProviderConfigurationException if the I/O provider was not
	 *             configured properly
	 * @throws IOException if an I/O operation fails
	 */
	protected abstract MutableAlignment loadAlignment(ProgressIndicator progress,
			IOReporter reporter) throws IOProviderConfigurationException, IOException;

	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		try {
			alignment = loadAlignment(progress, reporter);
			if (alignment == null) {
				// make sure failure is reported
				reporter.setSuccess(false);
			}
			else {
				alignment = postProcess(alignment);
			}
		} catch (Exception e) {
			// make sure failure is reported
			reporter.error(new IOMessageImpl(e.getMessage(), e));
			reporter.setSuccess(false);
			return reporter;
		}

		return reporter;
	}

	/**
	 * Post process the alignment after loading.
	 * 
	 * @param alignment the alignment to process
	 * @return the processed alignment
	 */
	protected MutableAlignment postProcess(MutableAlignment alignment) {
		/*
		 * Processing of core functions. This should eventually be handled
		 * through an extension point to allow external contributions.
		 */
		Collection<? extends Cell> originalCells = new ArrayList<>(alignment.getCells());
		for (Cell orgCell : originalCells) {

			// replace assign with source by bound assign
			// for backwards compatibility
			if (AssignFunction.ID.equals(orgCell.getTransformationIdentifier())
					&& orgCell.getSource() != null && !orgCell.getSource().isEmpty()) {
				// assign with a source assigned
				// -> replace by bound assign
				MutableCell newCell = new DefaultCell(orgCell);
				newCell.setTransformationIdentifier(AssignFunction.ID_BOUND);
				alignment.removeCell(orgCell);
				alignment.addCell(newCell);
			}

		}

		return alignment;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.io.AlignmentReader#setPathUpdater(eu.esdihumboldt.hale.common.core.io.PathUpdate)
	 */
	@Override
	public void setPathUpdater(PathUpdate updater) {
		this.updater = updater;
	}

	/**
	 * Get the location updater
	 * 
	 * @return the location updater
	 */
	public PathUpdate getPathUpdater() {
		return updater;
	}

	/**
	 * @see AbstractImportProvider#validate()
	 */
	@Override
	public void validate() throws IOProviderConfigurationException {
		super.validate();

		if (sourceSchema == null) {
			fail("Source schema not set");
		}

		if (targetSchema == null) {
			fail("Target schema not set");
		}
	}

	/**
	 * @see AbstractIOProvider#getDefaultTypeName()
	 */
	@Override
	protected String getDefaultTypeName() {
		return "HALE alignment";
	}

}
