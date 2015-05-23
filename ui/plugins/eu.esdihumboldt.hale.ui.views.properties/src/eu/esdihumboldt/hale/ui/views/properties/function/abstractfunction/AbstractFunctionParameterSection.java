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

package eu.esdihumboldt.hale.ui.views.properties.function.abstractfunction;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import eu.esdihumboldt.hale.common.align.extension.function.FunctionDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionParameter;
import eu.esdihumboldt.hale.ui.views.properties.function.AbstractFunctionSection;

/**
 * Abstract function section with information of parameters
 * 
 * @author Patrick Lieb
 */
public class AbstractFunctionParameterSection extends
		AbstractFunctionSection<FunctionDefinition<?>> {

	private TableViewer tableViewer;

	/**
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#createControls(org.eclipse.swt.widgets.Composite,
	 *      org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage)
	 */
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

		Composite compparent = getWidgetFactory().createComposite(parent);
		compparent.setLayout(new FormLayout());

		Composite composite = getWidgetFactory().createComposite(compparent);
		TableColumnLayout columnLayout = new TableColumnLayout();
		composite.setLayout(columnLayout);
		FormData data = new FormData();
		data.width = 100;
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, -0);
		data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
		data.bottom = new FormAttachment(100, -ITabbedPropertyConstants.VSPACE);
		composite.setLayoutData(data);

		tableViewer = new TableViewer(composite, SWT.FULL_SELECTION);
		Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());

		TableViewerColumn nameviewercol = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn namecol = nameviewercol.getColumn();
		columnLayout.setColumnData(namecol, new ColumnWeightData(20));
		namecol.setText("Name");
		nameviewercol.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				cell.setText(((FunctionParameter) cell.getElement()).getName());
			}

		});

		TableViewerColumn lableviewercol = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn lablecol = lableviewercol.getColumn();
		columnLayout.setColumnData(lablecol, new ColumnWeightData(20));
		lablecol.setText("Label");
		lableviewercol.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				cell.setText(((FunctionParameter) cell.getElement()).getDisplayName());
			}

		});

		TableViewerColumn occurenceviewercol = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn occurencecol = occurenceviewercol.getColumn();
		columnLayout.setColumnData(occurencecol, new ColumnWeightData(10));
		occurencecol.setText("Occurence");
		occurenceviewercol.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				FunctionParameter cellparameter = ((FunctionParameter) cell.getElement());
				cell.setText(String.valueOf(cellparameter.getMinOccurrence()) + ".."
						+ (String.valueOf(cellparameter.getMaxOccurrence())));
			}

		});

		TableViewerColumn descriptionviewercol = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn descriptioncol = descriptionviewercol.getColumn();
		columnLayout.setColumnData(descriptioncol, new ColumnWeightData(40));
		descriptioncol.setText("Description");
		descriptionviewercol.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				cell.setText(String.valueOf(((FunctionParameter) cell.getElement())
						.getDescription()));
			}

		});
	}

	/**
	 * @see AbstractPropertySection#shouldUseExtraSpace()
	 */
	@Override
	public boolean shouldUseExtraSpace() {
		return true;
	}

	/**
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#refresh()
	 */
	@Override
	public void refresh() {
		tableViewer.refresh();
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.views.properties.AbstractTextSection#setInput(java.lang.Object)
	 */
	@Override
	protected void setInput(Object input) {
		if (input instanceof FunctionDefinition) {
			tableViewer.setInput(((FunctionDefinition<?>) input).getDefinedParameters());
		}

	}

}
