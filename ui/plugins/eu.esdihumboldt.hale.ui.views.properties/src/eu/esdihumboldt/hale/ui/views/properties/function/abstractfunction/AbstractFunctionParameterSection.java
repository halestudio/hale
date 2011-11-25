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

package eu.esdihumboldt.hale.ui.views.properties.function.abstractfunction;

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
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import eu.esdihumboldt.hale.common.align.extension.function.AbstractFunction;
import eu.esdihumboldt.hale.common.align.extension.function.Function;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionParameter;
import eu.esdihumboldt.hale.ui.views.properties.function.AbstractFunctionSection;
import eu.esdihumboldt.hale.ui.views.properties.function.DefaultFunctionSection;

/**
 * Abstract function section with information of parameters
 * @author Patrick Lieb
 */
public class AbstractFunctionParameterSection extends AbstractFunctionSection<AbstractFunction<?>>{
	
	private Composite composite;
	
	private Table table;
	
	private TableViewer tableViewer;
	
	private TableViewerColumn nameviewercol;
	
	private TableViewerColumn occurenceviewercol;
	
	private TableViewerColumn descriptionviewercol;
	
	private TableViewerColumn lableviewercol;

	/**
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#createControls(org.eclipse.swt.widgets.Composite, org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage)
	 */
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		composite = getWidgetFactory().createFlatFormComposite(parent);
		
		TableColumnLayout columnLayout = new TableColumnLayout();
		composite.setLayout(columnLayout);
		
		tableViewer = new TableViewer(composite, SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		
		nameviewercol = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn namecol = nameviewercol.getColumn();
		columnLayout.setColumnData(namecol, new ColumnWeightData(25));
		namecol.setText("Name");
		nameviewercol.setLabelProvider(new CellLabelProvider(){

			@Override
			public void update(ViewerCell cell) {
				cell.setText(((FunctionParameter) cell.getElement()).getName());
			}
			
		});
		
		lableviewercol = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn lablecol = lableviewercol.getColumn();
		columnLayout.setColumnData(lablecol, new ColumnWeightData(25));
		lablecol.setText("Lable");
		lableviewercol.setLabelProvider(new CellLabelProvider(){

			@Override
			public void update(ViewerCell cell) {
				cell.setText(((FunctionParameter) cell.getElement()).getDisplayName());
			}
			
		});
		
		occurenceviewercol = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn occurencecol = occurenceviewercol.getColumn();
		columnLayout.setColumnData(occurencecol, new ColumnWeightData(25));
		occurencecol.setText("Occurence");
		occurenceviewercol.setLabelProvider(new CellLabelProvider(){

			@Override
			public void update(ViewerCell cell) {
				FunctionParameter cellparameter = ((FunctionParameter) cell.getElement());
				cell.setText(String.valueOf(cellparameter.getMinOccurrence()) + ".." + (String.valueOf(cellparameter.getMaxOccurrence())));
			}
			
		});
		
		descriptionviewercol = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn descriptioncol = descriptionviewercol.getColumn();
		columnLayout.setColumnData(descriptioncol, new ColumnWeightData(25));
		descriptioncol.setText("Description");
		descriptionviewercol.setLabelProvider(new CellLabelProvider(){

			@Override
			public void update(ViewerCell cell) {
				cell.setText(String.valueOf(((FunctionParameter) cell.getElement()).getDescription()));
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

	/**
	 * @see eu.esdihumboldt.hale.ui.views.properties.AbstractSection#setInput(java.lang.Object)
	 */
	@Override
	protected void setInput(Object input) {
		if (input instanceof Function) {
			tableViewer.setInput(((Function) input).getDefinedParameters());
		}
		
	}
	
}
