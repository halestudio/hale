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

package eu.esdihumboldt.hale.common.align.io.impl;

import java.io.IOException;
import java.net.URI;

import eu.esdihumboldt.hale.common.align.io.BaseAlignmentReader;
import eu.esdihumboldt.hale.common.align.model.MutableAlignment;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractImportProvider;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * Base alignment reader.
 * 
 * @author Kai Schwierczek
 */
public class JaxbBaseAlignmentReader extends AbstractImportProvider implements BaseAlignmentReader {

	private TypeIndex sourceSchema;
	private TypeIndex targetSchema;
	private MutableAlignment alignment;
	private URI projectLocation;

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.IOProvider#isCancelable()
	 */
	@Override
	public boolean isCancelable() {
		return false;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider#execute(eu.esdihumboldt.hale.common.core.io.ProgressIndicator,
	 *      eu.esdihumboldt.hale.common.core.io.report.IOReporter)
	 */
	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		progress.begin("Load hale alignment", ProgressIndicator.UNKNOWN);

		try {
			JaxbAlignmentIO.addBaseAlignment(getAlignment(), getSource().getUsedLocation(),
					projectLocation, getSourceSchema(), getTargetSchema(), reporter);
		} catch (Exception e) {
			reporter.error(new IOMessageImpl(e.getMessage(), e));
			reporter.setSuccess(false);
			return reporter;
		}

		progress.end();
		reporter.setSuccess(true);
		return reporter;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider#getDefaultTypeName()
	 */
	@Override
	protected String getDefaultTypeName() {
		return "hale alignment";
	}

	/**
	 * @see BaseAlignmentReader#setSourceSchema(TypeIndex)
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
	 * @see BaseAlignmentReader#setTargetSchema(TypeIndex)
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

	/**
	 * Get the alignment
	 * 
	 * @return the alignment
	 */
	public MutableAlignment getAlignment() {
		return alignment;
	}

	/**
	 * @param alignment the alignment to set
	 */
	@Override
	public void setAlignment(MutableAlignment alignment) {
		this.alignment = alignment;
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

		if (alignment == null) {
			fail("Alignment not set");
		}
	}

	/**
	 * @see BaseAlignmentReader#setProjectLocation(URI)
	 */
	@Override
	public void setProjectLocation(URI projectLocation) {
		this.projectLocation = projectLocation;
	}

}
