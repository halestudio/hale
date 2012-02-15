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

package eu.esdihumboldt.hale.ui.views.mapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.WorkbenchPart;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;

import eu.esdihumboldt.hale.common.align.extension.function.AbstractFunction;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.ui.common.function.viewer.FunctionLabelProvider;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.align.AlignmentServiceAdapter;
import eu.esdihumboldt.hale.ui.service.align.AlignmentServiceListener;

/**
 * View displaying the current alignment
 * @author Simon Templer
 */
public class AlignmentView extends AbstractMappingView {
	
	/**
	 * The view ID
	 */
	public static final String ID = "eu.esdihumboldt.hale.ui.views.mapping.alignment";

	private AlignmentServiceListener alignmentListener;
	
	private ComboViewer typeRelations;
	
	private final FunctionLabelProvider functionLabels = new FunctionLabelProvider();

	/**
	 * @see AbstractMappingView#createPartControl(Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);
		page.setLayout(GridLayoutFactory.fillDefaults().create());
		
		// create type relation selection control
		typeRelations = new ComboViewer(page, SWT.DROP_DOWN | SWT.READ_ONLY);
		typeRelations.setContentProvider(ArrayContentProvider.getInstance());
		typeRelations.setLabelProvider(new LabelProvider() {

			@Override
			public Image getImage(Object element) {
				if (element instanceof Cell) {
					// use function image if possible
					Cell cell = (Cell) element;
					String functionId = cell.getTransformationIdentifier();
					AbstractFunction<?> function = FunctionUtil.getFunction(functionId);
					if (function != null) {
						return functionLabels.getImage(function);
					}
					return null;
				}
				
				return super.getImage(element);
			}

			@Override
			public String getText(Object element) {
				if (element instanceof Cell) {
					Cell cell = (Cell) element;
					
					StringBuffer result = new StringBuffer();
					
					// include function name if possible
					String functionId = cell.getTransformationIdentifier();
					AbstractFunction<?> function = FunctionUtil.getFunction(functionId);
					if (function != null) {
						result.append(functionLabels.getText(function));
						result.append(": ");
					}
					
					result.append(entitiesText(cell.getSource().values()));
					result.append(" to ");
					result.append(entitiesText(cell.getTarget().values()));
					
					return result.toString();
				}
				
				return super.getText(element);
			}

			private String entitiesText(Collection<? extends Entity> entities) {
				return Joiner.on(", ").join(Collections2.transform(entities, new Function<Entity, String>() {
					@Override
					public String apply(Entity input) {
						return input.getDefinition().getDefinition().getDisplayName();
					}
				}));
			}
			
		});
		typeRelations.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updateGraph();
			}
		});
		typeRelations.getControl().setLayoutData(GridDataFactory.swtDefaults()
				.align(SWT.FILL, SWT.CENTER).grab(true, false).create());
		
		// create viewer
		Composite viewerContainer = new Composite(page, SWT.NONE);
		viewerContainer.setLayout(new FillLayout());
		viewerContainer.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		super.createPartControl(viewerContainer);
		
		AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(AlignmentService.class);
		
		update();
		
		as.addListener(alignmentListener = new AlignmentServiceAdapter() {

			@Override
			public void alignmentCleared() {
				update();
			}

			@Override
			public void cellRemoved(Cell cell) {
				update();
			}

			@Override
			public void cellsUpdated(Iterable<Cell> cells) {
				update();
			}

			@Override
			public void cellsAdded(Iterable<Cell> cells) {
				update();
			}
		});
	}

	/**
	 * Set the current alignment
	 */
	private void update() {
		final Display display = PlatformUI.getWorkbench().getDisplay();
		display.syncExec(new Runnable() {
			
			@Override
			public void run() {
				ISelection selection = typeRelations.getSelection();
				Cell lastSelected = null;
				if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
					lastSelected = (Cell) ((IStructuredSelection) selection).getFirstElement();
				}
				
				// update type relations
				AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(AlignmentService.class);
				Collection<? extends Cell> typeCells = as.getAlignment().getTypeCells();
				typeRelations.setInput(typeCells);
				
				ISelection newSelection;
				if (lastSelected != null && typeCells.contains(lastSelected)) {
					newSelection = new StructuredSelection(lastSelected);
				}
				else if (typeCells.isEmpty()) {
					newSelection = new StructuredSelection();
				}
				else {
					newSelection = new StructuredSelection(typeCells.iterator().next());
				}
				typeRelations.setSelection(newSelection);
				
				// call to updateGraph is done implicitly through selection change
			}
		});
	}
		
	private void updateGraph() {
		ISelection selection = typeRelations.getSelection();
		
		Cell typeCell = null;
		if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
			typeCell = (Cell) ((IStructuredSelection) selection).getFirstElement();
		}
		
		if (typeCell != null) {
			AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(AlignmentService.class);
			Collection<Cell> cells = new ArrayList<Cell>();
			cells.add(typeCell);
			cells.addAll(AlignmentUtil.getPropertyCellsFromTypeCell(as.getAlignment(), typeCell));
			getViewer().setInput(cells);
		}
		else {
			getViewer().setInput(Collections.EMPTY_LIST);
		}
		getViewer().applyLayout();
	}

	/**
	 * @see WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		if (alignmentListener != null) {
			AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(AlignmentService.class);
			as.removeListener(alignmentListener);
		}
		
		functionLabels.dispose();
		
		super.dispose();
	}

}
