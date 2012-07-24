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

package eu.esdihumboldt.hale.ui.views.report.properties.details.extension;

import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;

import de.cs3d.util.eclipse.extension.AbstractExtension;
import de.cs3d.util.eclipse.extension.ExtensionObjectFactoryCollection;
import de.cs3d.util.eclipse.extension.FactoryFilter;
import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.report.Report;

/**
 * Extension for {@link CustomReportDetailsPage}s.
 *
 * @author Kai Schwierczek
 */
public class CustomReportDetailsPageExtension extends
		AbstractExtension<CustomReportDetailsPage, CustomReportDetailsPageFactory> {
	private static final ALogger log = ALoggerFactory.getLogger(CustomReportDetailsPageExtension.class);

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
	protected CustomReportDetailsPageFactory createFactory(IConfigurationElement conf) throws Exception {
		return new CustomReportDetailsPageFactory(conf);
	}

	/**
	 * Returns the registered detail page for the given report type.<br>
	 * It searches for registered pages for the specified type or any of its
	 * super types/interfaces. If there are multiple matches a random one is returned.<br>
	 * If no matching type is registered at all, <code>null</code> is returned.
	 *
	 * @param reportType the type in question
	 * @return a registered {@link CustomReportDetailsPage} for the given type or <code>null</code> if there is none
	 */
	public CustomReportDetailsPage getDetailPage(@SuppressWarnings("rawtypes") final Class<? extends Report> reportType) {
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
