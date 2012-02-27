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

package eu.esdihumboldt.hale.ui.views.properties.function;

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
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import eu.esdihumboldt.hale.common.align.extension.function.AbstractParameter;
import eu.esdihumboldt.hale.ui.views.properties.AbstractTextSection;

/**
 * Abstract function section with general information on functions displayed in
 * a table
 * 
 * @author Patrick
 * @param <P>
 *            the Function parameter
 */
public abstract class AbstractFunctionTableSection<P extends AbstractParameter>
		extends AbstractTextSection {

	private TableViewer tableViewer;

	/**
	 * @return the tableViewer
	 */
	public TableViewer getTableViewer() {
		return tableViewer;
	}

	/**
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#createControls(org.eclipse.swt.widgets.Composite,
	 *      org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
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

		TableViewerColumn nameviewercol = new TableViewerColumn(tableViewer,
				SWT.NONE);
		TableColumn namecol = nameviewercol.getColumn();
		columnLayout.setColumnData(namecol, new ColumnWeightData(20));
		namecol.setText("Name");
		nameviewercol.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				cell.setText(((P) cell.getElement()).getName());
			}

		});

		TableViewerColumn lableviewercol = new TableViewerColumn(tableViewer,
				SWT.NONE);
		TableColumn lablecol = lableviewercol.getColumn();
		columnLayout.setColumnData(lablecol, new ColumnWeightData(20));
		lablecol.setText("Lable");
		lableviewercol.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				cell.setText(((P) cell.getElement()).getDisplayName());
			}

		});

		TableViewerColumn occurenceviewercol = new TableViewerColumn(
				tableViewer, SWT.NONE);
		TableColumn occurencecol = occurenceviewercol.getColumn();
		columnLayout.setColumnData(occurencecol, new ColumnWeightData(20));
		occurencecol.setText("Occurence");
		occurenceviewercol.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				P cellparameter = ((P) cell.getElement());
				cell.setText(String.valueOf(cellparameter.getMinOccurrence())
						+ ".."
						+ (String.valueOf(cellparameter.getMaxOccurrence())));
			}

		});

		TableViewerColumn descriptionviewercol = new TableViewerColumn(
				tableViewer, SWT.NONE);
		TableColumn descriptioncol = descriptionviewercol.getColumn();
		columnLayout.setColumnData(descriptioncol, new ColumnWeightData(20));
		descriptioncol.setText("Description");
		descriptionviewercol.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				cell.setText(String.valueOf(((P) cell.getElement())
						.getDescription()));
			}

		});
	}

	/**
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#refresh()
	 */
	@Override
	public void refresh() {
		tableViewer.refresh();
	}

}
