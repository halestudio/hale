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

package eu.esdihumboldt.hale.ui.views.report.properties.summary;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.ui.util.components.URILink;

/**
 * Extended summary for {@link IOReport}.
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ReportIOSummary extends AbstractReportSummary {
	
	/**
	 * Link to the file from {@link IOReport}
	 */
	private URILink link;
	private Link displayLink;
	private Text linktext;
	
	/**
	 * Text for the link
	 */
	public Text linkText;
	
	/**
	 * @see AbstractPropertySection#createControls(Composite, TabbedPropertySheetPage)
	 */
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		
		// urilink
		link = new URILink(composite, SWT.None, null, "<A>Open Location</A>");
		
		displayLink = link.getLink();
		
		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(composite, ITabbedPropertyConstants.VSPACE);
		displayLink.setLayoutData(data);
		displayLink.setBackground(getWidgetFactory().getColors().getBackground());
		
		// link label
		CLabel linkLabel = getWidgetFactory().createCLabel(composite, "Link:"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, -ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(composite, 0, SWT.CENTER);
		linkLabel.setLayoutData(data);
		
		CLabel namespaceLabel = getWidgetFactory()
		.createCLabel(composite, "Location:"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(displayLink,15);
		data.top = new FormAttachment(displayLink, 0, SWT.CENTER);
		namespaceLabel.setLayoutData(data);
		
		linktext = getWidgetFactory().createText(composite, "");
		linktext.setEditable(false);

		data = new FormData();
		data.width = 100;
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(displayLink, ITabbedPropertyConstants.VSPACE);
		data.bottom = new FormAttachment(100, -ITabbedPropertyConstants.VSPACE);
		linktext.setLayoutData(data);

		namespaceLabel = getWidgetFactory()
				.createCLabel(composite, ""); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(linktext,
				-ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(linktext, 0, SWT.TOP);
		namespaceLabel.setLayoutData(data);
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
		
		this.link.refresh(((IOReport)report).getTarget().getLocation());
		this.linktext.setText(((IOReport)report).getTarget().getLocation().toASCIIString());
		this.displayLink = link.getLink();
	}
}
