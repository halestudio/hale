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

package eu.esdihumboldt.hale.ui.views.report.properties.details.extension;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.AbstractConfigurationFactory;
import de.fhg.igd.eclipse.util.extension.ExtensionUtil;
import eu.esdihumboldt.hale.common.core.report.Report;

/**
 * Factory for {@link CustomReportDetailsPage}s.
 * 
 * @author Kai Schwierczek
 */
public class CustomReportDetailsPageFactory extends
		AbstractConfigurationFactory<CustomReportDetailsPage> {

	/**
	 * Create a {@link CustomReportDetailsPage} factory based on the given
	 * configuration element.
	 * 
	 * @param conf the configuration element
	 */
	protected CustomReportDetailsPageFactory(IConfigurationElement conf) {
		super(conf, "class");
	}

	/**
	 * @see de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory#dispose(java.lang.Object)
	 */
	@Override
	public void dispose(CustomReportDetailsPage page) {
		// do nothing
	}

	/**
	 * @see de.fhg.igd.eclipse.util.extension.ExtensionObjectDefinition#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return getIdentifier();
	}

	/**
	 * @see de.fhg.igd.eclipse.util.extension.ExtensionObjectDefinition#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		return conf.getAttribute("id");
	}

	/**
	 * Returns the {@link Report} class this detail page is registered for.
	 * 
	 * @return the {@link Report} class this detail page is registered for
	 */
	@SuppressWarnings("unchecked")
	public Class<? extends Report<?>> getReportType() {
		return (Class<? extends Report<?>>) ExtensionUtil.loadClass(conf, "reportType");
	}
}
