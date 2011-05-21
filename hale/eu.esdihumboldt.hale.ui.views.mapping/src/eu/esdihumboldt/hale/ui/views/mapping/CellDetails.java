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
package eu.esdihumboldt.hale.ui.views.mapping;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TableColumn;

import eu.esdihumboldt.commons.goml.omwg.Restriction;
import eu.esdihumboldt.cst.transformer.FilterUtils;
import eu.esdihumboldt.hale.mapping.helper.EntityHelper;
import eu.esdihumboldt.hale.ui.model.mapping.CellInfo;
import eu.esdihumboldt.hale.ui.selection.CellSelection;
import eu.esdihumboldt.hale.ui.views.mapping.internal.Messages;
import eu.esdihumboldt.specification.cst.align.ext.IParameter;
import eu.esdihumboldt.specification.cst.align.ext.ITransformation;

/**
 * Cell details view
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class CellDetails implements ISelectionChangedListener {

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
		names.setText(Messages.CellDetails_NameText);
		values = new TableColumn(viewer.getTable(), SWT.NONE);
		values.setWidth(200);
		values.setText(Messages.CellDetails_ValueText);
		
		// content
		viewer.setContentProvider(new IStructuredContentProvider() {

			@Override
			public Object[] getElements(Object inputElement) {
				if (inputElement != null && inputElement instanceof CellInfo) {
					CellInfo cell = (CellInfo) inputElement;
					
					List<TableItem> items = new ArrayList<TableItem>();
					
					items.add(new TableItem(Messages.CellDetails_Entity1Title, EntityHelper.getShortName(cell.getCell().getEntity1())));
					
					// add filters
					List<Restriction> restrictions = FilterUtils.getRestrictions(cell.getCell().getEntity1());
					if (restrictions != null) {
						for (Restriction res : restrictions) {
							items.add(new TableItem(Messages.CellDetails_FilterTitle, res.getCqlStr()));
						}
					}
					
					items.add(new TableItem(Messages.CellDetails_Entity2Title, EntityHelper.getShortName(cell.getCell().getEntity2())));
					
					ITransformation transformation = cell.getCell().getEntity1().getTransformation();
					if (transformation != null) {
//						if (transformation.getAbout() != null) {
//							items.add(new TableItem("Transformation", transformation.getAbout().getAbout()));
//						}
						if (transformation.getService() != null) {
							items.add(new TableItem(Messages.CellDetails_TransformationTitle, transformation.getService().toString()));
						}
						if (transformation.getParameters() != null) {
							for (IParameter param : transformation.getParameters()) {
								items.add(new TableItem(param.getName(), param.getValue()));
							}
						}
					}
					
					ITransformation augmentation = cell.getCell().getEntity2().getTransformation();
					if (augmentation != null) {
//						if (augmentation.getAbout() != null) {
//							items.add(new TableItem("Augmentation", augmentation.getAbout().getAbout()));
//						}
						if (augmentation.getService() != null) {
							items.add(new TableItem(Messages.CellDetails_AugmentationTitle, augmentation.getService().toString()));
						}
						if (augmentation.getParameters() != null) {
							for (IParameter param : augmentation.getParameters()) {
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
		
		selectionChanged(null);
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
	 * @see ISelectionChangedListener#selectionChanged(SelectionChangedEvent)
	 */
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		CellInfo cell;
		if (event == null) {
			cell = null;
		}
		else if (event.getSelection() instanceof CellSelection) {
			cell = ((CellSelection) event.getSelection()).getCellInfo();
		}
		else {
			return;
		}
		
		viewer.setInput(cell);
		
		names.pack();
		values.pack();
		
		viewer.getControl().setEnabled(cell != null);
	}

}
