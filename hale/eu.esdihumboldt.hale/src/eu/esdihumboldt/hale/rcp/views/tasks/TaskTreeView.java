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

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeColumnViewerLabelProvider;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.part.WorkbenchPart;

import eu.esdihumboldt.hale.models.HaleServiceListener;
import eu.esdihumboldt.hale.models.SchemaService;
import eu.esdihumboldt.hale.models.TaskService;
import eu.esdihumboldt.hale.models.task.TaskServiceAdapter;
import eu.esdihumboldt.hale.rcp.utils.tree.CollectionTreeNodeContentProvider;
import eu.esdihumboldt.hale.rcp.utils.tree.DefaultTreeNode;
import eu.esdihumboldt.hale.rcp.utils.tree.MapTreeNode;
import eu.esdihumboldt.hale.rcp.utils.tree.SortedMapTreeNode;
import eu.esdihumboldt.hale.rcp.views.model.ModelNavigationView;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;
import eu.esdihumboldt.hale.task.ResolvedTask;
import eu.esdihumboldt.hale.task.Task;
import eu.esdihumboldt.hale.task.TaskUtils;

/**
 * Task view based on a tree
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class TaskTreeView extends ViewPart {
	
	/**
	 * The view ID
	 */
	public static String ID = "eu.esdihumboldt.hale.rcp.views.tasks.TaskTreeView";

	/**
	 * The tree viewer
	 */
	private TreeViewer tree;
	
	private TaskService taskService;

	private HaleServiceListener taskListener;
	
	private SchemaService schemaService;
	
	private MapTreeNode<TypeDefinition, MapTreeNode<ResolvedTask, TreeNode>> sourceNode;
	
	private MapTreeNode<TypeDefinition, MapTreeNode<ResolvedTask, TreeNode>> targetNode;
	
	/**
	 * @see WorkbenchPart#createPartControl(Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		page.setLayoutData(data);
		
		// tree column layout
		TreeColumnLayout layout = new TreeColumnLayout(); 
		page.setLayout(layout);
		
		// tree viewer
		tree = new TreeViewer(page, SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER);
		tree.setContentProvider(new CollectionTreeNodeContentProvider());
		
		tree.getTree().setHeaderVisible(true);
		tree.getTree().setLinesVisible(true);
		
		// columns
		
		// title/description
		TreeViewerColumn description = new TreeViewerColumn(tree, SWT.LEFT);
		description.getColumn().setText("Description");
		TreeColumnViewerLabelProvider descriptionLabelProvider = new TreeColumnViewerLabelProvider(
				new TaskDescriptionLabelProvider(0));
		description.setLabelProvider(descriptionLabelProvider);
		layout.setColumnData(description.getColumn(), new ColumnWeightData(1));
		
		// value
		TreeViewerColumn value = new TreeViewerColumn(tree, SWT.CENTER);
		value.getColumn().setText("!");
		TreeColumnViewerLabelProvider valueLabelProvider = new TreeColumnViewerLabelProvider(
				new TaskValueLabelProvider(0));
		value.setLabelProvider(valueLabelProvider);
		layout.setColumnData(value.getColumn(), new ColumnWeightData(0, 20));
		
		// number of tasks
		TreeViewerColumn number = new TreeViewerColumn(tree, SWT.RIGHT);
		number.getColumn().setText("#");
		TreeColumnViewerLabelProvider numberLabelProvider = new TreeColumnViewerLabelProvider(
				new TaskCountLabelProvider(0));
		number.setLabelProvider(numberLabelProvider);
		layout.setColumnData(number.getColumn(), new ColumnWeightData(0, 48));
		
		// listeners
		schemaService = (SchemaService) PlatformUI.getWorkbench().getService(SchemaService.class);
		
		taskService = (TaskService) PlatformUI.getWorkbench().getService(TaskService.class);
		taskService.addListener(taskListener = new TaskServiceAdapter() {
			
			@Override
			public void tasksRemoved(final Iterable<Task> tasks) {
				if (Display.getCurrent() != null) {
					removeTasks(tasks);
				}
				else {
					final Display display = PlatformUI.getWorkbench().getDisplay();
					display.syncExec(new Runnable() {

						@Override
						public void run() {
							removeTasks(tasks);
						}
						
					});
				}
			}

			@Override
			public void tasksAdded(final Iterable<Task> tasks) {
				if (Display.getCurrent() != null) {
					addTasks(tasks);
				}
				else {
					final Display display = PlatformUI.getWorkbench().getDisplay();
					display.syncExec(new Runnable() {

						@Override
						public void run() {
							addTasks(tasks);
						}
						
					});
				}
			}
			
		});
		
		createInput();
		
		configureActions();
		
		// interaction
		tree.addDoubleClickListener(new IDoubleClickListener() {
			
			@Override
			public void doubleClick(DoubleClickEvent event) {
				ISelection selection = event.getSelection();
				if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
					Object selected = ((IStructuredSelection) selection).getFirstElement();
					if (selected instanceof TreeNode) {
						// determine value
						Object tmp = ((TreeNode) selected).getValue();
						Object value;
						if (tmp.getClass().isArray()) {
							value = ((Object[]) tmp)[0];
						}
						else {
							value = tmp;
						}
						if (value instanceof Task) {
							// node is task node
							Task task = (Task) value;
							onDoubleClick(task.getMainContext().getIdentifier());
						}
						else if (value instanceof TypeDefinition) {
							TypeDefinition type = (TypeDefinition) value;
							onDoubleClick(type.getIdentifier());
						}
					}
				}
			}
		});
	}
	
	/**
	 * React on a double click on an item that represents the given identifier
	 * 
	 * @param identifier the identifier
	 */
	protected void onDoubleClick(String identifier) {
		//FIXME use selection mechanism instead of getting the view (real ugly)
		ModelNavigationView modelView = (ModelNavigationView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ModelNavigationView.ID);
		if (modelView != null) {
			modelView.selectItem(identifier);
		}
	}

	private void configureActions() {
		IActionBars bars = getViewSite().getActionBars();
		
		// tool-bar
		//IToolBarManager toolBar = bars.getToolBarManager();
		//toolBar.add(...);
		
		// menu
		IMenuManager menu = bars.getMenuManager();
		menu.add(new TaskProviderMenu());
	}

	/**
	 * Update the view
	 */
	private void createInput() {
		TaskService taskService = (TaskService) PlatformUI.getWorkbench().getService(TaskService.class);
		
		final Collection<TreeNode> input = new ArrayList<TreeNode>();
		sourceNode = new SortedMapTreeNode<TypeDefinition, MapTreeNode<ResolvedTask, TreeNode>>("Source");
		targetNode = new SortedMapTreeNode<TypeDefinition, MapTreeNode<ResolvedTask, TreeNode>>("Target");
		input.add(sourceNode);
		input.add(targetNode);
		
		Collection<ResolvedTask> tasks = taskService.getResolvedTasks();
		for (ResolvedTask task : tasks) {
			addTask(task);
		}
			
		tree.setInput(input);
	}

	/**
	 * Add a resolved task
	 * 
	 * @param task the task to add
	 */
	private void addTask(ResolvedTask task) {
		// add task to model
		MapTreeNode<ResolvedTask, TreeNode> parent = getParentNode(task, true);
		if (parent != null) {
			parent.addChild(task, new DefaultTreeNode(task));
			// update viewer
			tree.refresh(parent, true);
			// update icons
			TreeNode updateNode = parent.getParent();
			while (updateNode != null) {
				tree.update(updateNode, null);
				updateNode = updateNode.getParent();
			}
		}
	}
	
	/**
	 * Adds the given tasks
	 * 
	 * @param tasks the tasks to add
	 */
	protected void removeTasks(Iterable<Task> tasks) {
		//TODO smart refresh
		for (Task task : tasks) {
			removeTask(task);
		}
	}

	/**
	 * Remove a task
	 * 
	 * @param task the task to remove
	 */
	@SuppressWarnings("unchecked")
	private void removeTask(Task task) {
		ResolvedTask resolved = taskService.resolveTask(task);
		// remove task from model
		MapTreeNode<ResolvedTask, TreeNode> parent = getParentNode(resolved, false);
		if (parent != null) {
			parent.removeChild(resolved);
			// remove empty nodes
			if (!parent.hasChildren()) {
				MapTreeNode<TypeDefinition, MapTreeNode<ResolvedTask, TreeNode>> root = (MapTreeNode<TypeDefinition, MapTreeNode<ResolvedTask, TreeNode>>) parent.getParent();
				root.removeChildNode(parent);
				tree.refresh(root, true);
			}
			else {
				tree.refresh(parent, true);
				// update icons
				TreeNode updateNode = parent.getParent();
				while (updateNode != null) {
					tree.update(updateNode, null);
					updateNode = updateNode.getParent();
				}
			}
		}
	}

	/**
	 * Get the parent node for the given task
	 * 
	 * @param task the task
	 * @param allowCreate allow creating the node if it does not yet exist
	 * 
	 * @return the parent node
	 */
	private MapTreeNode<ResolvedTask, TreeNode> getParentNode(ResolvedTask task, boolean allowCreate) {
		TypeDefinition group = TaskUtils.getGroup(task);
		if (group.getName().getNamespaceURI().equals(schemaService.getSourceNameSpace())) {
			// source task
			return getGroupNode(sourceNode, group, allowCreate);
		}
		else if (group.getName().getNamespaceURI().equals(schemaService.getTargetNameSpace())) {
			// target task
			return getGroupNode(targetNode, group, allowCreate);
		}
		else {
			// invalid task ?
			MapTreeNode<ResolvedTask, TreeNode> result = getGroupNode(sourceNode, group, false);
			if (result == null) {
				result = getGroupNode(targetNode, group, false);
			}
			return result;
		}
	}

	/**
	 * Get the group node for the group defined by the given type definition
	 * 
	 * @param rootNode the root node
	 * @param group the group's type definition
	 * @param allowCreate allow creating the node if it does not yet exist
	 * 
	 * @return the group node
	 */
	private MapTreeNode<ResolvedTask, TreeNode> getGroupNode(
			MapTreeNode<TypeDefinition, MapTreeNode<ResolvedTask, TreeNode>> rootNode,
			TypeDefinition group, boolean allowCreate) {
		MapTreeNode<ResolvedTask, TreeNode> groupNode = rootNode.getChild(group);
		if (groupNode == null && allowCreate) {
			groupNode = new SortedMapTreeNode<ResolvedTask, TreeNode>(group);
			rootNode.addChild(group, groupNode);
			// update viewer
			tree.refresh(rootNode, true);
		}
		return groupNode;
	}

	/**
	 * Adds the given tasks
	 * 
	 * @param tasks the tasks to add
	 */
	protected void addTasks(Iterable<Task> tasks) {
		//TODO smart refresh
		for (Task task : tasks) {
			addTask(taskService.resolveTask(task));
		}
	}

	/**
	 * @see WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		tree.getControl().setFocus();
	}

	/**
	 * @see WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		if (taskListener != null) {
			TaskService taskService = (TaskService) PlatformUI.getWorkbench().getService(TaskService.class);
			taskService.removeListener(taskListener);
		}
		
		super.dispose();
	}

}
