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

package eu.esdihumboldt.hale.ui.views.report.properties;

import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.part.WorkbenchPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class ReportPropertiesViewPart extends ViewPart implements
		ITabbedPropertySheetPageContributor {

	/**
	 * @see ITabbedPropertySheetPageContributor#getContributorId()
	 */
	@Override
	public String getContributorId() {
		return "eu.esdihumboldt.hale.ui.views.report.properties";
	}

	/**
	 * @see WorkbenchPart#getAdapter(Class)
	 */
	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (adapter == IPropertySheetPage.class)
			return new TabbedPropertySheetPage(this);
		return super.getAdapter(adapter);
	}

}
