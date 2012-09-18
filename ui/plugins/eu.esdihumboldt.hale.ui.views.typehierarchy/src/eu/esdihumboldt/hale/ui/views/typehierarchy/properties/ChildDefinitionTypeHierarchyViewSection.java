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

package eu.esdihumboldt.hale.ui.views.typehierarchy.properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import eu.esdihumboldt.hale.ui.views.properties.definition.DefaultDefinitionSection;
import eu.esdihumboldt.hale.ui.views.typehierarchy.TypeHierarchyView;

/**
 * Properties section with the hierarchy view of a {@link ChildDefinition}
 * 
 * @author Patrick Lieb
 */
public class ChildDefinitionTypeHierarchyViewSection extends
		DefaultDefinitionSection<ChildDefinition<?>> {

	private Link link;

	private SelectionAdapter adapter;

	/**
	 * @see AbstractPropertySection#createControls(Composite,
	 *      TabbedPropertySheetPage)
	 */
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		FormData data;
		link = new Link(composite, 0);
		link.setBackground(getWidgetFactory().getColors().getBackground());

		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
		link.setLayoutData(data);
		link.setText("<A>Open Type in HierarchyView</A>");

		CLabel namespaceLabel = getWidgetFactory().createCLabel(composite, "TypeHierarchy:"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(link, 15);
		data.top = new FormAttachment(link, 0, SWT.CENTER);
		namespaceLabel.setLayoutData(data);
		adapter = new SelectionAdapter() {
			// only initializing
		};
		link.addSelectionListener(adapter);
	}

	@Override
	public void refresh() {
		link.removeSelectionListener(adapter);
		adapter = new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
							.showView(TypeHierarchyView.ID);
				} catch (PartInitException e1) {
					e1.printStackTrace();
				}
				TypeHierarchyView thv = (TypeHierarchyView) PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage().findView(TypeHierarchyView.ID);
				thv.setType(getDefinition().getParentType());
			}
		};
		link.addSelectionListener(adapter);
	}

}
