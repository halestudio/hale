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

import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TreeColumnViewerLabelProvider;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.part.WorkbenchPart;

import eu.esdihumboldt.hale.models.HaleServiceListener;
import eu.esdihumboldt.hale.models.SchemaService;
import eu.esdihumboldt.hale.models.TaskService;
import eu.esdihumboldt.hale.models.task.TaskServiceAdapter;
import eu.esdihumboldt.hale.rcp.utils.tree.CollectionTreeNodeContentProvider;
import eu.esdihumboldt.hale.rcp.utils.tree.DefaultMultiColumnTreeNode;
import eu.esdihumboldt.hale.rcp.utils.tree.MapMultiColumnTreeNode;
import eu.esdihumboldt.hale.rcp.utils.tree.MultiColumnTreeNodeLabelProvider;
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
	
	private MapMultiColumnTreeNode<TypeDefinition, MapMultiColumnTreeNode<Task, TreeNode>> sourceNode;
	
	private MapMultiColumnTreeNode<TypeDefinition, MapMultiColumnTreeNode<Task, TreeNode>> targetNode;
	
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
		description.setLabelProvider(new TreeColumnViewerLabelProvider(
				new MultiColumnTreeNodeLabelProvider(0)));
		layout.setColumnData(description.getColumn(), new ColumnWeightData(1));
		
		// listeners
		schemaService = (SchemaService) PlatformUI.getWorkbench().getService(SchemaService.class);
		
		taskService = (TaskService) PlatformUI.getWorkbench().getService(TaskService.class);
		taskService.addListener(taskListener = new TaskServiceAdapter() {
			
			@Override
			public void taskRemoved(final Task task) {
				if (Display.getCurrent() != null) {
					removeTask(task);
				}
				else {
					final Display display = PlatformUI.getWorkbench().getDisplay();
					display.syncExec(new Runnable() {

						@Override
						public void run() {
							removeTask(task);
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
	}
	
	/**
	 * Update the view
	 */
	private void createInput() {
		TaskService taskService = (TaskService) PlatformUI.getWorkbench().getService(TaskService.class);
		
		final Collection<TreeNode> input = new ArrayList<TreeNode>();
		sourceNode = new MapMultiColumnTreeNode<TypeDefinition, MapMultiColumnTreeNode<Task,TreeNode>>("Source");
		targetNode = new MapMultiColumnTreeNode<TypeDefinition, MapMultiColumnTreeNode<Task,TreeNode>>("Target");
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
		MapMultiColumnTreeNode<Task,TreeNode> parent = getParentNode(task, true);
		if (parent != null) {
			parent.addChild(task, new DefaultMultiColumnTreeNode(task.getTitle()));
			// update viewer
			tree.refresh(parent, false);
		}
	}

	/**
	 * Remove a task
	 * 
	 * @param task the task to remove
	 */
	@SuppressWarnings("unchecked")
	protected void removeTask(Task task) {
		// remove task from model
		MapMultiColumnTreeNode<Task, TreeNode> parent = getParentNode(task, false);
		if (parent != null) {
			parent.removeChild(task);
			// remove empty nodes
			if (!parent.hasChildren()) {
				MapMultiColumnTreeNode<TypeDefinition, MapMultiColumnTreeNode<Task, TreeNode>> root = (MapMultiColumnTreeNode<TypeDefinition, MapMultiColumnTreeNode<Task, TreeNode>>) parent.getParent();
				root.removeChildNode(parent);
				tree.refresh(root, false);
			}
			else {
				tree.refresh(parent, false);
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
	private MapMultiColumnTreeNode<Task,TreeNode> getParentNode(Task task, boolean allowCreate) {
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
			MapMultiColumnTreeNode<Task,TreeNode> result = getGroupNode(sourceNode, group, false);
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
	private MapMultiColumnTreeNode<Task,TreeNode> getGroupNode(
			MapMultiColumnTreeNode<TypeDefinition, MapMultiColumnTreeNode<Task,TreeNode>> rootNode,
			TypeDefinition group, boolean allowCreate) {
		MapMultiColumnTreeNode<Task, TreeNode> groupNode = rootNode.getChild(group);
		if (groupNode == null && allowCreate) {
			groupNode = new MapMultiColumnTreeNode<Task, TreeNode>(group.getName().getLocalPart());
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
