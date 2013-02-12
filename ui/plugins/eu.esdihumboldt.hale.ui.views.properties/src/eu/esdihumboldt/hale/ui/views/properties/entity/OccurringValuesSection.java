/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.views.properties.entity;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.ui.common.CommonSharedImages;
import eu.esdihumboldt.hale.ui.service.values.OccurringValues;
import eu.esdihumboldt.hale.ui.service.values.OccurringValuesListener;
import eu.esdihumboldt.hale.ui.service.values.OccurringValuesService;

/**
 * Page displaying the occurring values for a property.
 * 
 * @author Simon Templer
 */
public class OccurringValuesSection extends AbstractEntityDefSection {

	private final OccurringValuesService service;

	private TableViewer values;

	private Button refresh;

	private OccurringValuesListener ovlistener;

	/**
	 * Default constructor.
	 */
	public OccurringValuesSection() {
		super();

		service = (OccurringValuesService) PlatformUI.getWorkbench().getService(
				OccurringValuesService.class);
	}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

		Composite page = getWidgetFactory().createComposite(parent);
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(page);

		// refresh button
		refresh = getWidgetFactory().createButton(page, null, SWT.PUSH);
		refresh.setImage(CommonSharedImages.getImageRegistry().get(CommonSharedImages.IMG_REFRESH));
		refresh.setToolTipText("Update the occurring values");
		refresh.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				service.updateOccuringValues((PropertyEntityDefinition) getEntity());
			}
		});
		GridDataFactory.swtDefaults().align(SWT.END, SWT.BEGINNING).grab(false, false)
				.applyTo(refresh);

		// values table
		values = new TableViewer(getWidgetFactory().createTable(page, SWT.MULTI | SWT.BORDER));
		GridDataFactory.fillDefaults().grab(true, true).applyTo(values.getControl());
		values.setContentProvider(new ArrayContentProvider() {

			@Override
			public Object[] getElements(Object inputElement) {
				if (inputElement instanceof OccurringValues) {
					return ((OccurringValues) inputElement).getValues().toArray();
				}

				return new Object[] {};
			}

		});
		values.setLabelProvider(new LabelProvider());
		values.setInput(null);

		// add listener to service
		service.addListener(ovlistener = new OccurringValuesListener() {

			@Override
			public void occurringValuesUpdated(PropertyEntityDefinition property) {
				if (property.equals(OccurringValuesSection.this.getEntity())) {
					update();
				}
			}

			@Override
			public void occurringValuesInvalidated(SchemaSpaceID schemaSpace) {
				if (schemaSpace.equals(OccurringValuesSection.this.getEntity().getSchemaSpace())) {
					update();
				}
			}
		});

		update();
	}

	/**
	 * Update the section state.
	 */
	private void update() {
		final Display display = PlatformUI.getWorkbench().getDisplay();
		display.syncExec(new Runnable() {

			@Override
			public void run() {
				if (values != null) {
					OccurringValues ov = null;
					if (getEntity() != null) {
						ov = service.getOccurringValues((PropertyEntityDefinition) getEntity());
					}

					values.setInput(ov);
					values.getControl().setEnabled(ov != null);
					refresh.setEnabled(ov == null || !ov.isUpToDate());

					// TODO some icon to state that information is not
					// up-to-date?

					// relayout so scrolling is done inside table
					// XXX not really working
					values.getControl().getParent().layout();
					values.getControl().getParent().getParent().layout();
				}
			}
		});
	}

	@Override
	public void refresh() {
		update();
	}

	@Override
	public boolean shouldUseExtraSpace() {
		return true;
	}

	@Override
	public void dispose() {
		if (ovlistener != null) {
			service.removeListener(ovlistener);
		}

		super.dispose();
	}

}
