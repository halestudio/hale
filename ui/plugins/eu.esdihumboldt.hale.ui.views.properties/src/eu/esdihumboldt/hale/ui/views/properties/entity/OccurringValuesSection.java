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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import com.google.common.collect.Multiset.Entry;

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

	private Button copy;

	private OccurringValuesListener ovlistener;

	/**
	 * Default constructor.
	 */
	public OccurringValuesSection() {
		super();

		service = PlatformUI.getWorkbench().getService(OccurringValuesService.class);
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
				service.updateOccurringValues((PropertyEntityDefinition) getEntity());
			}
		});
		GridDataFactory.swtDefaults().align(SWT.END, SWT.BEGINNING).grab(false, false)
				.applyTo(refresh);

		// values table
		values = new TableViewer(getWidgetFactory().createTable(page, SWT.MULTI | SWT.BORDER));
		GridDataFactory.fillDefaults().grab(true, true).span(1, 2).applyTo(values.getControl());
		values.setContentProvider(new ArrayContentProvider() {

			@Override
			public Object[] getElements(Object inputElement) {
				if (inputElement instanceof OccurringValues) {
					return ((OccurringValues) inputElement).getValues().entrySet().toArray();
				}

				return new Object[] {};
			}

		});
		values.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof Entry) {
					// XXX use styled label provider instead?
					Entry<?> entry = (Entry<?>) element;
					if (entry.getCount() > 1) {
						return super.getText(entry.getElement()) + "\t(\u00d7" + entry.getCount()
								+ ")";
					}
					else
						return super.getText(entry.getElement());
				}

				return super.getText(element);
			}

		});
		values.setInput(null);

		// values context menu
		MenuManager manager = new MenuManager();
		manager.setRemoveAllWhenShown(true);
		manager.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {
				// populate context menu

				// get selection
				ISelection selection = values.getSelection();
				if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
					Object[] sels = ((IStructuredSelection) selection).toArray();
					List<String> values = new ArrayList<String>();
					for (Object sel : sels) {
						if (sel instanceof Entry<?>) {
							values.add(((Entry<?>) sel).getElement().toString());
						}
					}

					if (!values.isEmpty()) {
						manager.add(new AddConditionAction(getEntity(), values, false));
						manager.add(new AddParentConditionAction(getEntity(), values, false));
						if (values.size() > 1) {
							manager.add(new Separator());
							manager.add(new AddConditionAction(getEntity(), values, true));
							manager.add(new AddParentConditionAction(getEntity(), values, true));
						}
					}
				}
			}

		});
		manager.setRemoveAllWhenShown(true);
		final Menu valuesMenu = manager.createContextMenu(values.getControl());
		values.getControl().setMenu(valuesMenu);

		// copy button
		copy = getWidgetFactory().createButton(page, null, SWT.PUSH);
		copy.setImage(
				PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_TOOL_COPY));
		copy.setToolTipText("Copy values to the clipboard");
		copy.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				copyToClipboard();
			}

		});
		GridDataFactory.swtDefaults().align(SWT.END, SWT.BEGINNING).grab(false, false)
				.applyTo(copy);

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
	 * Copy the values table to the clipboard.
	 */
	private void copyToClipboard() {
		if (values != null) {
			Table table = values.getTable();
			if (table.getSelectionCount() > 0) {
				TableItem rows[] = table.getItems(); // getSelection();
				StringBuilder sb = new StringBuilder();
				int cc = table.getColumnCount();
				if (cc == 0) {
					// there is a column even if used like a list
					cc = 1;
				}
				for (int row = 0; row < rows.length; row++) {
					if (row > 0)
						sb.append("\n");
					TableItem item = rows[row];
					for (int column = 0; column < cc; column++) {
						if (column > 0)
							sb.append(SWT.TAB);
						sb.append(item.getText(column));
					}
				}
				Clipboard clipBoard = new Clipboard(Display.getCurrent());
				clipBoard.setContents(new Object[] { sb.toString() },
						new Transfer[] { TextTransfer.getInstance() });
				clipBoard.dispose();
			}
		}
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
					copy.setEnabled(ov != null);

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
