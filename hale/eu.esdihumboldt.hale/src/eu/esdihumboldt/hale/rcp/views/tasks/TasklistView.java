/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.rcp.views.tasks;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

import eu.esdihumboldt.hale.models.HaleServiceListener;
import eu.esdihumboldt.hale.models.TaskService;
import eu.esdihumboldt.hale.models.UpdateMessage;


/**
 * The Tasklist of the Application.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class TasklistView 
	extends ViewPart 
	implements HaleServiceListener {
	
	public static final String ID ="eu.esdihumboldt.hale.rcp.views.tasks.TasklistView";

	private TableViewer tableViewer;
	
	private TaskService taskService;

	@Override
	public void createPartControl(Composite parent) {
		
		// get a reference to the TaskService.
		this.taskService = (TaskService) this.getSite().getService(
				TaskService.class);
		this.taskService.addListener(this);
		
		Composite viewerComposite = new Composite(parent, SWT.NONE);
		FillLayout fLay = new FillLayout();
		viewerComposite.setLayout(fLay);
		
		// Table for the viewer
		Table t = new Table(viewerComposite,SWT.NONE);
		t.setHeaderVisible(true);
		t.setLinesVisible(true);
		
		this.tableViewer = new TableViewer(t);
		
		//Severity column for the TaskListView with Sorter
		TableColumn severityColumn = new TableColumn(t,SWT.WRAP);
		severityColumn.setText("Type");
		severityColumn.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				tableViewer.setSorter(
						new TasklistTableSorter(TasklistTableSorter.SEVERITY));
			}
		});
		severityColumn.setWidth(50);
		

		//Value column for the TaskListView with Sorter
		TableColumn valueColumn = new TableColumn(t,SWT.WRAP);
		valueColumn.setText("Value");
		valueColumn.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				tableViewer.setSorter(
						new TasklistTableSorter(TasklistTableSorter.VALUE));
			}
		});
		valueColumn.setWidth(50);
		
		//Title column for the TaskListView with Sorter
		TableColumn titleColumn = new TableColumn(t,SWT.WRAP);
		titleColumn.setText("Title");
		titleColumn.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				tableViewer.setSorter(
						new TasklistTableSorter(TasklistTableSorter.TITLE));
			}
		});
		titleColumn.setWidth(170);
		
		//Source Implementation Name column for the TaskListView with Sorter
		TableColumn sourceImplNameColumn = new TableColumn(t,SWT.WRAP);
		sourceImplNameColumn.setText("Source Implementation Name");
		sourceImplNameColumn.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				tableViewer.setSorter(
						new TasklistTableSorter(TasklistTableSorter.SOURCEIMPLEMENTAIONNAME));
			}
		});
		sourceImplNameColumn.setWidth(50);
		
		//Source Implementation Name column for the TaskListView with Sorter
		TableColumn sourceCreatreasonColumn = new TableColumn(t,SWT.WRAP);
		sourceCreatreasonColumn.setText("Task Creation Reason");
		sourceCreatreasonColumn.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				tableViewer.setSorter(
						new TasklistTableSorter(TasklistTableSorter.TASKCREATIONREASON));
			}
		});
		sourceCreatreasonColumn.setWidth(100);
		
		CellEditor[] cellEditors = new CellEditor[5];
		cellEditors[0] = new TextCellEditor(t);
		cellEditors[1] = new TextCellEditor(t);
		cellEditors[2] = new TextCellEditor(t);
		cellEditors[3] = new TextCellEditor(t);
		cellEditors[4] = new TextCellEditor(t);
		
		this.tableViewer.setCellEditors(cellEditors);
		this.tableViewer.setCellModifier(new TasklistCellModifier());
		this.tableViewer.setSorter(new TasklistTableSorter(TasklistTableSorter.SEVERITY));
		this.tableViewer.setLabelProvider(new TasklistLabelProvider());
		this.tableViewer.setContentProvider(new TasklistContentProvider(this.tableViewer));

	}
	
	
	
	@Override
	public void setFocus(){
		
	}


	/* (non-Javadoc)
	 * @see eu.esdihumboldt.hale.models.HaleServiceListener#update()
	 */
	@Override
	public void update(UpdateMessage message) {
		this.tableViewer.setInput(taskService.getOpenTasks());
	}
}