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

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.report.Report;

/**
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ReportIOSummary extends ReportSummary {
	
	private Text linkText;
	
	private Hyperlink link;
	
	/**
	 * @see AbstractPropertySection#createControls(Composite, TabbedPropertySheetPage)
	 */
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
//		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
//		FormData data;
		
		
//		link = new Link(composite, SWT.NONE);
//		link.setText("<a href=\"\">File</a>");
		link = getWidgetFactory().createHyperlink(composite, "File", SWT.None);
		link.setVisible(false);
		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(timeText, ITabbedPropertyConstants.VSPACE);
//		linkText.setLayoutData(data);
		link.setLayoutData(data);
		
		link.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e){
				// TODO add HyperlinkAdapter
			}
		  });
		
		CLabel linkLabel = getWidgetFactory()
				.createCLabel(composite, "Link:"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(link,
				-ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(link, 0, SWT.CENTER);
		linkLabel.setLayoutData(data);
		
		
	}
	
	/**
	 * @see AbstractPropertySection#setInput(IWorkbenchPart, ISelection)
	 */
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
	}
	
	/**
	 * @see AbstractPropertySection#refresh()
	 */
	@Override
	public void refresh() {
		super.refresh();
		
//		link.setText("<a href=\""+((IOReport)report).getTarget().getLocation().toString()+"\">File</a>");
		try {
			link.setText(((IOReport)report).getTarget().getLocation().toString());
			link.setHref(new URL(((IOReport)report).getTarget().getLocation().toString()));
			link.setVisible(true);
		} catch (MalformedURLException e) {
			link.setVisible(false);
		}
		
	}
}
