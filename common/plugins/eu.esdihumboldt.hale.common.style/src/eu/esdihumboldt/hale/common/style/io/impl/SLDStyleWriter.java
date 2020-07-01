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

package eu.esdihumboldt.hale.common.style.io.impl;

import java.io.IOException;
import java.io.OutputStream;

import org.geotools.xml.styling.SLDTransformer;

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
