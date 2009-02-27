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

import eu.esdihumboldt.hale.task.impl.TasklistMock;


/**
 * The Tasklist of the Webclient. The Tasklist . The main component of 
 * this View is a TreeViewer which provides errors, warnings and 
 * tasks to the user.
 * @author cjauss
 *
 */
public class TasklistView extends ViewPart{
	
	public static final String ID ="eu.esdihumboldt.hale.rcp.views.tasks.TasklistView";
	private Tasklist tasklist;

	@Override
	public void createPartControl(Composite parent) {
		Composite viewerComposite = new Composite(parent, SWT.NONE);
		FillLayout fLay = new FillLayout();
		viewerComposite.setLayout(fLay);
		
		// Table for the viewer
		Table t = new Table(viewerComposite,SWT.NONE);
		t.setHeaderVisible(true);
		t.setLinesVisible(true);
		
		final TableViewer tViewer = new TableViewer(t);
		
		//Severity column for the TaskListView with Sorter
		TableColumn severityColumn = new TableColumn(t,SWT.WRAP);
		severityColumn.setText("Severity");
		severityColumn.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				tViewer.setSorter(
						new TasklistTableSorter(TasklistTableSorter.SEVERITY));
			}
		});
		severityColumn.setWidth(70);
		

		//Value column for the TaskListView with Sorter
		TableColumn valueColumn = new TableColumn(t,SWT.WRAP);
		valueColumn.setText("Value");
		valueColumn.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				tViewer.setSorter(
						new TasklistTableSorter(TasklistTableSorter.VALUE));
			}
		});
		valueColumn.setWidth(70);
		
		//Type column for the TaskListView with Sorter
		TableColumn taskTypeColumn = new TableColumn(t,SWT.WRAP);
		taskTypeColumn.setText("Tasktype");
		taskTypeColumn.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				tViewer.setSorter(
						new TasklistTableSorter(TasklistTableSorter.TYPE));
			}
		});
		taskTypeColumn.setWidth(70);
		
		//Title column for the TaskListView with Sorter
		TableColumn titleColumn = new TableColumn(t,SWT.WRAP);
		titleColumn.setText("Title");
		titleColumn.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				tViewer.setSorter(
						new TasklistTableSorter(TasklistTableSorter.TITLE));
			}
		});
		titleColumn.setWidth(70);
		
		//Source Implementation Name column for the TaskListView with Sorter
		TableColumn sourceImplNameColumn = new TableColumn(t,SWT.WRAP);
		sourceImplNameColumn.setText("Source Implementation Name");
		sourceImplNameColumn.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				tViewer.setSorter(
						new TasklistTableSorter(TasklistTableSorter.SOURCEIMPLEMENTAIONNAME));
			}
		});
		sourceImplNameColumn.setWidth(160);
		
		//Source Implementation Name column for the TaskListView with Sorter
		TableColumn sourceCreatreasonColumn = new TableColumn(t,SWT.WRAP);
		sourceCreatreasonColumn.setText("Task Creation Reason");
		sourceCreatreasonColumn.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				tViewer.setSorter(
						new TasklistTableSorter(TasklistTableSorter.TASKCREATIONREASON));
			}
		});
		sourceCreatreasonColumn.setWidth(200);
		
		CellEditor[] cellEditors = new CellEditor[5];
		cellEditors[0] = new TextCellEditor(t);
		cellEditors[1] = new TextCellEditor(t);
		cellEditors[2] = new TextCellEditor(t);
		cellEditors[3] = new TextCellEditor(t);
		cellEditors[4] = new TextCellEditor(t);
		
		tViewer.setCellEditors(cellEditors);
		tViewer.setCellModifier(new TasklistCellModifier());
		tViewer.setSorter(new TasklistTableSorter(TasklistTableSorter.SEVERITY));
		tViewer.setLabelProvider(new TasklistLabelProvider());
		tViewer.setContentProvider(new TasklistContentProvider(tViewer));
		// create a new empty Tasklist
		createTasklist();
		//TODO set Tasklist as input
		tViewer.setInput(new TasklistMock());
	}
	
	
	/**
	 * Creates an empty Tasklist.
	 */
	private void createTasklist(){
		this.tasklist = new Tasklist();
	}
	
	
	public Tasklist getTasklist(){
		return tasklist;
	}
	
	
	@Override
	public void setFocus(){
		
	}
}