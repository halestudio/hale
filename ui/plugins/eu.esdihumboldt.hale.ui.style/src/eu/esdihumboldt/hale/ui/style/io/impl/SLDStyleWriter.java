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

package eu.esdihumboldt.hale.ui.style.io.impl;

import java.io.IOException;
import java.io.OutputStream;

import org.geotools.styling.SLDTransformer;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;

/**
 * Writes styles to SLD.
 * 
 * @author Simon Templer
 */
public class SLDStyleWriter extends AbstractStyleWriter {

	/**
	 * @see IOProvider#isCancelable()
	 */
	@Override
	public boolean isCancelable() {
		return false;
	}

	/**
	 * @see AbstractIOProvider#execute(ProgressIndicator, IOReporter)
	 */
	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		progress.begin("Save styles to SLD", ProgressIndicator.UNKNOWN);

		SLDTransformer trans = new SLDTransformer();
		trans.setIndentation(2);

		OutputStream out = getTarget().getOutput();
		try {
			trans.transform(getStyle(), out);
			reporter.setSuccess(true);
		} catch (Exception e) {
			reporter.error(new IOMessageImpl("Saving the style as SLD failed.", e));
			reporter.setSuccess(false);
		} finally {
			out.close();
			progress.end();
		}

		return reporter;
	}

	/**
	 * @see AbstractIOProvider#getDefaultTypeName()
	 */
	@Override
	protected String getDefaultTypeName() {
		return "Styled Layer Descriptor";
	}

}
