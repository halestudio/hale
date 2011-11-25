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

package eu.esdihumboldt.hale.ui.views.properties.function.propertyfunction;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import eu.esdihumboldt.hale.common.align.extension.function.AbstractParameter;
import eu.esdihumboldt.hale.common.align.extension.function.PropertyFunction;
import eu.esdihumboldt.hale.ui.views.properties.function.DefaultFunctionSection;

/**
 * Abstract section for property functions
 * @author Patrick Lieb
 * @param <T> the FunctionParameter
 */
public abstract class AbstractPropertyFunctionSection<T extends AbstractParameter> extends DefaultFunctionSection<PropertyFunction>{

private Composite parent;
	
	private Composite composite;
	
	private TabbedPropertySheetPage aTabbedPropertySheetPage;
	
	private TableViewer tableViewer;
	

	/**
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#createControls(org.eclipse.swt.widgets.Composite, org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage)
	 */
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		this.parent = parent;
		this.aTabbedPropertySheetPage = aTabbedPropertySheetPage;
		super.createControls(parent, aTabbedPropertySheetPage);
	}

	/**
	 * @param input the input
	 * @param title the title for the table
	 * 
	 */
	protected void abstractRefresh(Object input, String title) {
		
		if (composite != null)
			composite.dispose();
		
		super.createControls(parent, aTabbedPropertySheetPage);
		
		composite = getWidgetFactory().createFlatFormComposite(parent);
		TableColumnLayout columnLayout = new TableColumnLayout();
		composite.setLayout(columnLayout);
		tableViewer = new TableViewer(composite, SWT.FULL_SELECTION);
		Table table = tableViewer.getTable();

		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		TableViewerColumn col = new TableViewerColumn(tableViewer, SWT.NONE);
//		TableColumn tabcol = col.getColumn();
		columnLayout.setColumnData(col.getColumn(), new ColumnWeightData(90));
//		col.getColumn().setWidth(100);
		col.getColumn().setText(title);
		col.setLabelProvider(new CellLabelProvider(){

			@SuppressWarnings("unchecked")
			@Override
			public void update(ViewerCell cell) {
				// FIXME: set correct cell label
				cell.setText(((T) cell.getElement()).getClass().getCanonicalName());
			}
			
		});
		
			tableViewer.setInput(input);
			
			parent.layout();
			parent.getParent().layout();
	}
}
