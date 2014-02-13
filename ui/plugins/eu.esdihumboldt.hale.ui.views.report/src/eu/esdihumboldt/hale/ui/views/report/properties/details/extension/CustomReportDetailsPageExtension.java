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

import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;

import de.cs3d.util.eclipse.extension.AbstractExtension;
import de.cs3d.util.eclipse.extension.ExtensionObjectFactoryCollection;
import de.cs3d.util.eclipse.extension.FactoryFilter;
import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.report.Report;

/**
 * Extension for {@link CustomReportDetailsPage}s.
 * 
 * @author Kai Schwierczek
 */
public class CustomReportDetailsPageExtension extends
		AbstractExtension<CustomReportDetailsPage, CustomReportDetailsPageFactory> {

	private static final ALogger log = ALoggerFactory
			.getLogger(CustomReportDetailsPageExtension.class);

	/**
	 * The extension point ID.
	 */
	public static final String ID = "eu.esdihumboldt.hale.ui.views.report.detailpage";

	private static CustomReportDetailsPageExtension instance;

	/**
	 * Get the extension instance.
	 * 
	 * @return the custom report details page extension
	 */
	public static CustomReportDetailsPageExtension getInstance() {
		if (instance == null) {
			instance = new CustomReportDetailsPageExtension();
		}
		return instance;
	}

	/**
	 * Default constructor.
	 */
	private CustomReportDetailsPageExtension() {
		super(ID);
	}

	/**
	 * @see AbstractExtension#createFactory(IConfigurationElement)
	 */
	@Override
	protected CustomReportDetailsPageFactory createFactory(IConfigurationElement conf)
			throws Exception {
		return new CustomReportDetailsPageFactory(conf);
	}

	/**
	 * Returns the registered detail page for the given report type.<br>
	 * It searches for registered pages for the specified type or any of its
	 * super types/interfaces. If there are multiple matches a random one is
	 * returned.<br>
	 * If no matching type is registered at all, <code>null</code> is returned.
	 * 
	 * @param reportType the type in question
	 * @return a registered {@link CustomReportDetailsPage} for the given type
	 *         or <code>null</code> if there is none
	 */
	public CustomReportDetailsPage getDetailPage(
			@SuppressWarnings("rawtypes") final Class<? extends Report> reportType) {
		List<CustomReportDetailsPageFactory> factories = getFactories(new FactoryFilter<CustomReportDetailsPage, CustomReportDetailsPageFactory>() {

			@Override
			public boolean acceptFactory(CustomReportDetailsPageFactory factory) {
				return factory.getReportType().isAssignableFrom(reportType);
			}

			@Override
			public boolean acceptCollection(
					ExtensionObjectFactoryCollection<CustomReportDetailsPage, CustomReportDetailsPageFactory> collection) {
				return true;
			}
		});
		if (factories.isEmpty())
			return null;
		else {
			if (factories.size() > 1)
				log.warn("Multiple matching custom report details pages, using first one.");
			// XXX maybe choose more intelligent?
			// Are there actually Use Cases where multiple matches make sense?
			try {
				return factories.get(0).createExtensionObject();
			} catch (Exception e) {
				log.warn("Exception creating custom report details page.", e);
				return null;
			}
		}
	}
}
