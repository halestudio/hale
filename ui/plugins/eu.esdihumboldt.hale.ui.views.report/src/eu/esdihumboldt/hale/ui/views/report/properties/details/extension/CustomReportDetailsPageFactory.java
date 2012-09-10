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

import org.eclipse.core.runtime.IConfigurationElement;

import de.cs3d.util.eclipse.extension.AbstractConfigurationFactory;
import de.cs3d.util.eclipse.extension.ExtensionUtil;
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
	 * @see de.cs3d.util.eclipse.extension.ExtensionObjectFactory#dispose(java.lang.Object)
	 */
	@Override
	public void dispose(CustomReportDetailsPage page) {
		// do nothing
	}

	/**
	 * @see de.cs3d.util.eclipse.extension.ExtensionObjectDefinition#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return getIdentifier();
	}

	/**
	 * @see de.cs3d.util.eclipse.extension.ExtensionObjectDefinition#getIdentifier()
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
