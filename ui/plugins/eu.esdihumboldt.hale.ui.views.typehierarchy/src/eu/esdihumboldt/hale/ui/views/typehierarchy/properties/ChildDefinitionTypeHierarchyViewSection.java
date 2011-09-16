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

package eu.esdihumboldt.hale.ui.views.typehierarchy.properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.ui.views.properties.DefaultDefinitionSection;
import eu.esdihumboldt.hale.ui.views.typehierarchy.TypeHierarchyView;

/**
 * TODO Type description
 * @author Patrick Lieb
 */
public class ChildDefinitionTypeHierarchyViewSection extends DefaultDefinitionSection<ChildDefinition<?>>{

	private Link link;
	
	/**
	 * @see AbstractPropertySection#createControls(Composite, TabbedPropertySheetPage)
	 */
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		Composite composite = getWidgetFactory()
				.createFlatFormComposite(parent);
		FormData data;
		link = new Link(composite, SWT.BORDER);
		
		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
		link.setLayoutData(data);
		link.setText("Open Type in HierarchyView");
		
		CLabel namespaceLabel = getWidgetFactory()
		.createCLabel(composite, "TypeHierarchy"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(link,
										-ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(link, 0, SWT.CENTER);
										namespaceLabel.setLayoutData(data);
		
		
		link.addMouseListener(new MouseListener(){
			
			@Override
			public void mouseUp(MouseEvent event) {
				// only initialize
			}

			@Override
			public void mouseDown(MouseEvent event) {
				// only initialize
			}

			@Override
			public void mouseDoubleClick(MouseEvent event) {
				// only initialize
			}
		});
	}
	
	@Override
	public void refresh(){
		link.addMouseListener(new MouseListener(){
			
			@Override
			public void mouseUp(MouseEvent event) {
				// do nothing
			}

			@Override
			public void mouseDown(MouseEvent event) {
				try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(TypeHierarchyView.ID);
					TypeHierarchyView thv = (TypeHierarchyView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(TypeHierarchyView.ID);
//					TODO thv.setType(getDefinition().getParentType());
				} catch (PartInitException e) {
					// ignore
				}
			}

			@Override
			public void mouseDoubleClick(MouseEvent event) {
				try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(TypeHierarchyView.ID);
					TypeHierarchyView thv = (TypeHierarchyView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(TypeHierarchyView.ID);
//					TODO thv.setType(getDefinition().getParentType());
				} catch (PartInitException e) {
					// ignore
				}
			}
		});
	}
	
}
