/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.common.align.io.impl;

import eu.esdihumboldt.hale.common.align.io.AlignmentWriter;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractExportProvider;

/**
 * Abstract alignment writer implementation
 * 
 * @author Simon Templer
 */
public abstract class AbstractAlignmentWriter extends AbstractExportProvider implements
		AlignmentWriter {

	private Alignment alignment;

	/**
	 * @see AlignmentWriter#setAlignment(Alignment)
	 */
	@Override
	public void setAlignment(Alignment alignment) {
		this.alignment = alignment;
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
	 * @see AbstractExportProvider#validate()
	 */
	@Override
	public void validate() throws IOProviderConfigurationException {
		super.validate();

		if (alignment == null) {
			fail("Alignment to write not set");
		}
	}

}
