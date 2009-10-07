/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.rcp.views.mapping;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TableColumn;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.ext.IParameter;
import eu.esdihumboldt.cst.align.ext.ITransformation;
import eu.esdihumboldt.hale.rcp.views.mapping.CellSelector.CellSelectionListener;

/**
 * Cell details view
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class CellDetails implements CellSelectionListener {

	private final TableViewer viewer;
	
	private TableColumn names;
	
	private TableColumn values;
	
	/**
	 * Constructor
	 * 
	 * @param parent the parent composite
	 */
	public CellDetails(Composite parent) {
		viewer = new TableViewer(parent);
		
		names = new TableColumn(viewer.getTable(), SWT.NONE);
		names.setWidth(200);
		names.setText("Name");
		values = new TableColumn(viewer.getTable(), SWT.NONE);
		values.setWidth(200);
		values.setText("Value");
		
		// content
		viewer.setContentProvider(new IStructuredContentProvider() {

			@Override
			public Object[] getElements(Object inputElement) {
				if (inputElement != null && inputElement instanceof ICell) {
					ICell cell = (ICell) inputElement;
					
					List<TableItem> items = new ArrayList<TableItem>();
					
					items.add(new TableItem("Entity 1", CellSelector.getShortName(cell.getEntity1())));
					items.add(new TableItem("Entity 2", CellSelector.getShortName(cell.getEntity2())));
					ITransformation transformation = cell.getEntity1().getTransformation();
					if (transformation != null) {
						items.add(new TableItem("Transformation", transformation.getLabel()));
						if (transformation.getService() != null) {
							items.add(new TableItem("Service", transformation.getService().toString()));
						}
						if (transformation.getParameters() != null) {
							for (IParameter param : transformation.getParameters()) {
								items.add(new TableItem(param.getName(), param.getValue()));
							}
						}
					}
					
					return items.toArray();
				}
				else {
					return new Object[]{};
				}
			}

			@Override
			public void dispose() {
				// do nothing
			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				// do nothing
			}
			
		});
		
		// labels
		viewer.setLabelProvider(new TableItemLabelProvider());
		
		// options
		viewer.getTable().setLinesVisible(true);
		viewer.getTable().setHeaderVisible(false);
		
		onSelectedCell(null);
	}

	/**
	 * Get the control
	 * 
	 * @return the control
	 */
	public Control getControl() {
		return viewer.getControl();
	}

	/**
	 * @see CellSelectionListener#onSelectedCell(ICell)
	 */
	@Override
	public void onSelectedCell(ICell cell) {
		viewer.setInput(cell);
		
		names.pack();
		values.pack();
		
		viewer.getControl().setEnabled(cell != null);
	}

}
