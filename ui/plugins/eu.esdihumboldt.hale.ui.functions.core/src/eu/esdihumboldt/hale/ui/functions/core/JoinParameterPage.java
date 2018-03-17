/*
 * Copyright (c) 2013 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.functions.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.functions.JoinFunction;
import eu.esdihumboldt.hale.common.align.model.functions.join.JoinParameter;
import eu.esdihumboldt.hale.common.align.model.functions.join.JoinParameter.JoinCondition;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.core.io.impl.ComplexValue;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Reference;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.PrimaryKey;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.common.CommonSharedImages;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionComparator;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.common.definition.viewer.SchemaPatternFilter;
import eu.esdihumboldt.hale.ui.common.definition.viewer.StyledDefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.function.generic.AbstractGenericFunctionWizard;
import eu.esdihumboldt.hale.ui.function.generic.pages.AbstractParameterPage;
import eu.esdihumboldt.hale.ui.functions.core.internal.CoreFunctionsUIPlugin;
import eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService;
import eu.esdihumboldt.hale.ui.service.entity.util.EntityTypeIterableContentProvider;
import eu.esdihumboldt.hale.ui.util.viewer.tree.TreePathFilteredTree;
import eu.esdihumboldt.hale.ui.util.viewer.tree.TreePathProviderAdapter;

/**
 * Parameter page for the Join type transformation.
 * 
 * @author Kai Schwierczek
 */
public class JoinParameterPage extends AbstractParameterPage implements JoinFunction {

	private static final ALogger log = ALoggerFactory.getLogger(JoinParameterPage.class);

	private final List<ConditionPage> pages = new ArrayList<ConditionPage>();
	private List<TypeEntityDefinition> types = new ArrayList<TypeEntityDefinition>();
	private TableViewer table;

	private Image upIcon;
	private Image downIcon;
	private Image equalsIcon;

	/**
	 * Constructor.
	 */
	public JoinParameterPage() {
		super(FunctionUtil.getTypeFunction(ID, HaleUI.getServiceProvider()),
				"Please configure the join order");
		setPageComplete(false);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.function.generic.pages.ParameterPage#getConfiguration()
	 */
	@Override
	public ListMultimap<String, ParameterValue> getConfiguration() {
		ListMultimap<String, ParameterValue> result = ArrayListMultimap.create(1, 1);

		Set<JoinCondition> conditions = new HashSet<>();
		for (ConditionPage page : pages) {
			conditions.addAll(page.conditions);
		}
		JoinParameter param = new JoinParameter(types, conditions);

		result.put(PARAMETER_JOIN, new ParameterValue(new ComplexValue(param)));
		return result;
	}

	/**
	 * Creates a join parameter for the types up to <code>upToIndex</code>.
	 * 
	 * @param upToIndex the type index up to which the parameter should be
	 *            created for
	 * @return the current join parameter up to the specified index
	 */
	private JoinParameter createJoinParameter(int upToIndex) {
		Set<JoinCondition> conditions = new HashSet<>();
		for (int i = 0; i < upToIndex; i++)
			conditions.addAll(pages.get(i).conditions);
		JoinParameter param = new JoinParameter(types.subList(0, upToIndex + 1), conditions);
		return param;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#createContent(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		page.setLayout(new GridLayout(2, false));

		Label intro = new Label(page, SWT.NONE);
		intro.setText("Select the join order. Focus will happen on the topmost type.");
		intro.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false, 2, 1));

		table = new TableViewer(page, SWT.V_SCROLL | SWT.BORDER | SWT.SINGLE);
		table.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));
		table.setLabelProvider(new StyledDefinitionLabelProvider(table));
		table.setContentProvider(ArrayContentProvider.getInstance());
		table.setInput(types);

		final Button up = new Button(page, SWT.PUSH);
		final Button down = new Button(page, SWT.PUSH);

		upIcon = CoreFunctionsUIPlugin.getImageDescriptor("icons/nav_up.gif").createImage();
		up.setImage(upIcon);
		up.setLayoutData(new GridData(SWT.CENTER, SWT.BEGINNING, false, false));
		up.setEnabled(false);
		up.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = getCurrentSelection();
				if (index > 0) {
					swapTypes(index - 1, index);
					if (index - 1 == 0)
						up.setEnabled(false);
					down.setEnabled(true);
				}
			}
		});

		downIcon = CoreFunctionsUIPlugin.getImageDescriptor("icons/nav_down.gif").createImage();
		down.setImage(downIcon);
		down.setLayoutData(new GridData(SWT.CENTER, SWT.BEGINNING, false, false));
		down.setEnabled(false);
		down.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = getCurrentSelection();
				if (index + 1 < types.size()) {
					swapTypes(index, index + 1);
					if (index + 2 == types.size())
						down.setEnabled(false);
					up.setEnabled(true);
				}
			}
		});

		table.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				int index = getCurrentSelection();
				if (index < 0) {
					down.setEnabled(false);
					up.setEnabled(false);
				}
				else {
					down.setEnabled(index + 1 < types.size());
					up.setEnabled(index > 0);
				}
			}
		});
	}

	/**
	 * Swaps the given types, their pages and validates all affected pages
	 * conditions after the change.
	 * 
	 * @param lowerTypeIndex the lower type index
	 * @param higherTypeIndex the higher type index
	 */
	private void swapTypes(int lowerTypeIndex, int higherTypeIndex) {
		TypeEntityDefinition lowerType = types.get(lowerTypeIndex);
		types.set(lowerTypeIndex, types.get(higherTypeIndex));
		types.set(higherTypeIndex, lowerType);
		table.refresh();

		// swap pages accordingly
		int lowerPageIndex = lowerTypeIndex - 1;
		int higherPageIndex = higherTypeIndex - 1;

		if (lowerPageIndex != -1) {
			// swap the page positions together with their types
			// so it is possible to keep some conditions from before
			ConditionPage lowerPage = pages.get(lowerPageIndex);
			ConditionPage higherPage = pages.get(higherPageIndex);

			lowerPage.typeIndex = higherTypeIndex;
			pages.set(higherPageIndex, lowerPage);

			higherPage.typeIndex = lowerTypeIndex;
			pages.set(lowerPageIndex, higherPage);
		}

		// check and refresh pages in between
		for (ConditionPage page : pages.subList(Math.max(0, lowerPageIndex), higherPageIndex + 1))
			page.checkAndRefresh();
		getContainer().updateButtons();
	}

	/**
	 * Returns the currently selected index or -1.
	 * 
	 * @return the currently selected index or -1
	 */
	private int getCurrentSelection() {
		ISelection selection = table.getSelection();
		if (selection.isEmpty())
			return -1;
		else {
			IStructuredSelection sel = (IStructuredSelection) selection;
			TypeEntityDefinition type = (TypeEntityDefinition) sel.getFirstElement();
			return types.indexOf(type);
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#onShowPage(boolean)
	 */
	@Override
	protected void onShowPage(boolean firstShow) {
		super.onShowPage(firstShow);

		Cell cell = getWizard().getUnfinishedCell();

		List<? extends Entity> sourceEntities = cell.getSource().get(JOIN_TYPES);
		List<TypeEntityDefinition> types = new ArrayList<TypeEntityDefinition>();
		Iterator<? extends Entity> iter = sourceEntities.iterator();
		while (iter.hasNext()) {
			types.add(AlignmentUtil.getTypeEntity(iter.next().getDefinition()));
		}

		if (sameTypes(this.types, types)) {
			return;
		}

		if (containsDuplicateType(types)) {
			setPageComplete(false);
			setErrorMessage("The selected source types contain duplicates.");
			this.types.clear();
			table.setInput(null);
			return;
		}
		else {
			setErrorMessage(null);
		}

		JoinParameter initialValue = null;
		if (firstShow && !getInitialValues().get(PARAMETER_JOIN).isEmpty()) {
			initialValue = getInitialValues().get(PARAMETER_JOIN).get(0).as(JoinParameter.class);

			if (initialValue != null) {
				// use ordering of the initial value (needs to be modifiable)
				List<TypeEntityDefinition> tmp = new ArrayList<>(initialValue.getTypes());

				// append any type that were added, or remove types that were
				// removed
				tmp.retainAll(types);
				List<TypeEntityDefinition> more = new ArrayList<>(types);
				more.removeAll(tmp);
				tmp.addAll(more);

				types = tmp;

				// apply potentially changed type list
				initialValue = new JoinParameter(types, initialValue.getConditions());
				if (initialValue.validate(true) != null) { // try to fix config
					// not recoverable
					initialValue = null;
				}
			}

		}

		this.types = types;

		for (ConditionPage page : pages) {
			page.dispose();
		}
		pages.clear();

		if (table != null) {
			table.setInput(types);
		}

		for (int i = 1; i < types.size(); i++) {
			ConditionPage conditionPage = new ConditionPage(i);
			conditionPage.setWizard(getWizard());
			pages.add(conditionPage);
		}

		if (initialValue != null) {
			// add initial conditions
			for (JoinCondition condition : initialValue.getConditions()) {
				TypeEntityDefinition joinType = AlignmentUtil.getTypeEntity(condition.joinProperty);
				int typeIndex = types.indexOf(joinType);
				int pageIndex = typeIndex - 1;
				pages.get(pageIndex).conditions.add(condition);
				pages.get(pageIndex).updateCompletionStatus();
			}
		}

		// order is always valid, will trigger updateButtons
		setPageComplete(true);
	}

	/**
	 * Returns whether the two lists contain the same types disregarding their
	 * order.
	 * 
	 * @param a the first list
	 * @param b the second list
	 * @return whether the two lists contain the same types disregarding their
	 *         order
	 */
	private boolean sameTypes(List<TypeEntityDefinition> a, List<TypeEntityDefinition> b) {
		if (a.size() != b.size())
			return false;
		for (TypeEntityDefinition type : a)
			if (!b.contains(type))
				return false;
		return true;
	}

	/**
	 * Checks whether the given {@link Iterable} of type entity definitions
	 * contains any type definition more than once.
	 * 
	 * @param types the entity definitions to check
	 * @return true, if there is a duplicate type definition.
	 */
	private boolean containsDuplicateType(Iterable<TypeEntityDefinition> types) {
		Set<TypeDefinition> typeDefs = new HashSet<>();
		for (TypeEntityDefinition type : types)
			if (typeDefs.contains(type.getDefinition()))
				return true;
			else
				typeDefs.add(type.getDefinition());
		return false;
	}

	/**
	 * @see org.eclipse.jface.wizard.WizardPage#getNextPage()
	 */
	@Override
	public IWizardPage getNextPage() {
		if (pages.size() > 0)
			return pages.get(0);
		else
			return null;
	}

	/**
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		// may only return true, if this page with all sub pages is complete
		if (!super.isPageComplete())
			return false;
		else
			for (ConditionPage page : pages)
				if (!page.isPageComplete())
					return false;
		return true;
	}

	/**
	 * @see org.eclipse.jface.wizard.WizardPage#canFlipToNextPage()
	 */
	@Override
	public boolean canFlipToNextPage() {
		// should still be true, even if only this page is completed.
		return super.isPageComplete() && getNextPage() != null;
	}

	@Override
	public void dispose() {
		super.dispose();
		if (upIcon != null)
			upIcon.dispose();
		if (downIcon != null)
			downIcon.dispose();
		if (equalsIcon != null)
			equalsIcon.dispose();
	}

	@Override
	public String getHelpContext() {
		return "eu.esdihumboldt.hale.common.align.join_order";
	}

	/**
	 * Page for specifying the join conditions of a type
	 */
	private class ConditionPage extends HaleWizardPage<AbstractGenericFunctionWizard<?, ?>> {

		private int typeIndex;
		private final Set<JoinCondition> conditions = new HashSet<>();

		private TreeViewer joinViewer;
		private Button addButton;
		private TreeViewer baseViewer;
		private TableViewer conditionViewer;
		private Label joinText;

		private final Map<JoinCondition, Button> removeConditionButtons = new HashMap<>();

		protected ConditionPage(int typeIndex) {
			super("join" + typeIndex,
					"Join " + types.get(typeIndex).getDefinition().getDisplayName(), null);
			setDescription("Please select join conditions for type "
					+ types.get(typeIndex).getDefinition().getDisplayName());
			setPageComplete(false);
			this.typeIndex = typeIndex;
		}

		@Override
		public String getHelpContext() {
			return "eu.esdihumboldt.hale.common.align.join_condition";
		}

		@Override
		protected void createContent(Composite page) {
			Composite main = new Composite(page, SWT.NONE);
			main.setLayout(new GridLayout(3, false));

			joinViewer = createTypeViewer(main, Collections.singleton(types.get(typeIndex)));

			if (equalsIcon == null)
				equalsIcon = CoreFunctionsUIPlugin.getImageDescriptor("icons/equals.gif")
						.createImage();

			addButton = new Button(main, SWT.PUSH);
			addButton.setImage(equalsIcon);
			addButton.setEnabled(false);
			addButton.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					addCondition();
					super.widgetSelected(e);
				}
			});

			baseViewer = createTypeViewer(main, types.subList(0, typeIndex));

			joinText = new Label(main, SWT.NONE);
			joinText.setText(
					"Join type " + types.get(typeIndex).getDefinition().getDisplayName() + " on:");
			joinText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

			conditionViewer = createConditionViewer(main, conditions);
		}

		private TableViewer createConditionViewer(Composite parent,
				Collection<JoinCondition> input) {
			parent = new Composite(parent, SWT.NONE);
			TableColumnLayout layout = new TableColumnLayout();
			parent.setLayout(layout);
			GridData gridData = new GridData(SWT.FILL, SWT.BOTTOM, true, true, 3, 1);
			gridData.minimumHeight = 80;
			parent.setLayoutData(gridData);
			TableViewer viewer = new TableViewer(parent,
					SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);

			viewer.getTable().setHeaderVisible(true);
			viewer.getTable().setLinesVisible(true);
			viewer.setComparator(new DefinitionComparator() {

				@Override
				public int compare(Viewer viewer, Object e1, Object e2) {
					JoinCondition j1 = (JoinCondition) e1;
					JoinCondition j2 = (JoinCondition) e2;
					return super.compare(viewer, j1.joinProperty, j2.joinProperty);
				}
			});
			viewer.setContentProvider(ArrayContentProvider.getInstance());

			final DefinitionLabelProvider dlp = new DefinitionLabelProvider(viewer, true, true);
			viewer.getTable().addDisposeListener(new DisposeListener() {

				@Override
				public void widgetDisposed(DisposeEvent e) {
					dlp.dispose();
				}
			});

			TableViewerColumn joinTypeColumn = new TableViewerColumn(viewer, SWT.NONE);
			layout.setColumnData(joinTypeColumn.getColumn(), new ColumnWeightData(1, true));
			joinTypeColumn.setLabelProvider(new ColumnLabelProvider() {

				@Override
				public String getText(Object element) {
					JoinCondition condition = (JoinCondition) element;
					return dlp.getText(AlignmentUtil.getTypeEntity(condition.joinProperty));
				}

				@Override
				public Image getImage(Object element) {
					JoinCondition condition = (JoinCondition) element;
					return dlp.getImage(AlignmentUtil.getTypeEntity(condition.joinProperty));
				}
			});

			TableViewerColumn joinPropertyColumn = new TableViewerColumn(viewer, SWT.NONE);
			layout.setColumnData(joinPropertyColumn.getColumn(), new ColumnWeightData(2, true));
			joinPropertyColumn.setLabelProvider(new ColumnLabelProvider() {

				@Override
				public String getText(Object element) {
					JoinCondition condition = (JoinCondition) element;
					return dlp.getText(condition.joinProperty);
				}

				@Override
				public Image getImage(Object element) {
					JoinCondition condition = (JoinCondition) element;
					return dlp.getImage(condition.joinProperty);
				}
			});

			TableViewerColumn equalColumn = new TableViewerColumn(viewer, SWT.NONE);
			layout.setColumnData(equalColumn.getColumn(), new ColumnWeightData(0, false));
			equalColumn.setLabelProvider(new ColumnLabelProvider() {

				@Override
				public String getText(Object element) {
					return "=";
				}
			});
			equalColumn.getColumn().setWidth(20);

			TableViewerColumn baseTypeColumn = new TableViewerColumn(viewer, SWT.NONE);
			layout.setColumnData(baseTypeColumn.getColumn(), new ColumnWeightData(1, true));
			baseTypeColumn.setLabelProvider(new ColumnLabelProvider() {

				@Override
				public String getText(Object element) {
					JoinCondition condition = (JoinCondition) element;
					return dlp.getText(AlignmentUtil.getTypeEntity(condition.baseProperty));
				}

				@Override
				public Image getImage(Object element) {
					JoinCondition condition = (JoinCondition) element;
					return dlp.getImage(AlignmentUtil.getTypeEntity(condition.baseProperty));
				}
			});

			TableViewerColumn basePropertyColumn = new TableViewerColumn(viewer, SWT.NONE);
			layout.setColumnData(basePropertyColumn.getColumn(), new ColumnWeightData(2, true));
			basePropertyColumn.setLabelProvider(new ColumnLabelProvider() {

				@Override
				public String getText(Object element) {
					JoinCondition condition = (JoinCondition) element;
					return dlp.getText(condition.baseProperty);
				}

				@Override
				public Image getImage(Object element) {
					JoinCondition condition = (JoinCondition) element;
					return dlp.getImage(condition.baseProperty);
				}
			});

			TableViewerColumn deleteConditionColumn = new TableViewerColumn(viewer, SWT.NONE);
			layout.setColumnData(deleteConditionColumn.getColumn(), new ColumnWeightData(0, false));
			deleteConditionColumn.setLabelProvider(new ColumnLabelProvider() {

				@Override
				public void update(ViewerCell cell) {
					TableItem item = (TableItem) cell.getItem();
					final JoinCondition condition = (JoinCondition) cell.getElement();
					Button button;
					if (removeConditionButtons.containsKey(condition))
						button = removeConditionButtons.get(condition);
					else {
						button = new Button((Composite) cell.getViewerRow().getControl(), SWT.NONE);
						button.setImage(CommonSharedImages.getImageRegistry()
								.get(CommonSharedImages.IMG_REMOVE));
						removeConditionButtons.put(condition, button);
						button.addSelectionListener(new SelectionAdapter() {

							@Override
							public void widgetSelected(SelectionEvent e) {
								removeCondition(condition, true);
							}
						});
					}
					TableEditor editor = new TableEditor(item.getParent());
					editor.grabHorizontal = true;
					editor.grabVertical = true;
					editor.setEditor(button, item, cell.getColumnIndex());
					editor.layout();
				}
			});
			deleteConditionColumn.getColumn().setWidth(20);

			viewer.setInput(input);
			return viewer;
		}

		private TreeViewer createTypeViewer(Composite parent,
				Collection<TypeEntityDefinition> input) {
			PatternFilter patternFilter = new SchemaPatternFilter();
			patternFilter.setIncludeLeadingWildcard(true);
			final FilteredTree filteredTree = new TreePathFilteredTree(parent,
					SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER, patternFilter, true);
			GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
			gridData.minimumHeight = 160;
			gridData.minimumWidth = 140;
			filteredTree.setLayoutData(gridData);
			TreeViewer viewer = filteredTree.getViewer();

			viewer.setComparator(new DefinitionComparator());

			EntityDefinitionService eds = PlatformUI.getWorkbench()
					.getService(EntityDefinitionService.class);
			viewer.setContentProvider(new TreePathProviderAdapter(
					new EntityTypeIterableContentProvider(eds, SchemaSpaceID.SOURCE)));
			viewer.setLabelProvider(new StyledDefinitionLabelProvider(viewer));

			viewer.setInput(input);
			viewer.addSelectionChangedListener(new ISelectionChangedListener() {

				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					updateButtonStatus();
				}
			});

			return viewer;
		}

		/**
		 * Called if the ordering of pages changed, maybe even this page's type
		 * changed.
		 */
		private void checkAndRefresh() {
			String typeDisplayName = types.get(typeIndex).getDefinition().getDisplayName();
			setTitle("Join " + typeDisplayName);
			setDescription("Please select join conditions for type " + typeDisplayName);

			// may be called before the page control was created
			if (getControl() == null)
				return;

			joinText.setText("Join type " + typeDisplayName + " on:");

			joinViewer.setInput(Collections.singleton(types.get(typeIndex)));
			joinViewer.refresh();
			List<TypeEntityDefinition> baseTypes = types.subList(0, typeIndex);
			baseViewer.setInput(baseTypes);
			baseViewer.refresh();

			for (JoinCondition condition : new ArrayList<>(conditions)) {
				TypeEntityDefinition joinType = AlignmentUtil.getTypeEntity(condition.joinProperty);
				TypeEntityDefinition baseType = AlignmentUtil.getTypeEntity(condition.baseProperty);

				if (!joinType.equals(types.get(typeIndex)) || !baseTypes.contains(baseType))
					removeCondition(condition, false);
			}
			conditionViewer.refresh();
			updateCompletionStatus();
		}

		/**
		 * Removes the specified condition and disposes of the remove button.
		 * 
		 * @param condition the condition to remove
		 * @param refresh whether to refresh the viewer and update completion
		 *            status or not
		 */
		private void removeCondition(JoinCondition condition, boolean refresh) {
			conditions.remove(condition);
			removeConditionButtons.get(condition).dispose();
			removeConditionButtons.remove(condition);
			if (refresh) {
				conditionViewer.refresh();
				updateCompletionStatus();
			}
		}

		private void updateButtonStatus() {
			ISelection leftSelection = joinViewer.getSelection();
			ISelection rightSelection = baseViewer.getSelection();

			boolean enable = false;

			if (!leftSelection.isEmpty() && !rightSelection.isEmpty()) {
				Object leftElem = ((IStructuredSelection) leftSelection).getFirstElement();
				Object rightElem = ((IStructuredSelection) rightSelection).getFirstElement();
				EntityDefinition leftDef = (EntityDefinition) leftElem;
				EntityDefinition rightDef = (EntityDefinition) rightElem;

				if (leftDef instanceof PropertyEntityDefinition
						&& rightDef instanceof PropertyEntityDefinition) {
					PropertyEntityDefinition leftProp = (PropertyEntityDefinition) leftDef;
					PropertyEntityDefinition rightProp = (PropertyEntityDefinition) rightDef;
					if (leftProp.getDefinition().getPropertyType().getConstraint(HasValueFlag.class)
							.isEnabled()
							&& rightProp.getDefinition().getPropertyType()
									.getConstraint(HasValueFlag.class).isEnabled())
						enable = true;
				}
			}
			addButton.setEnabled(enable);
		}

		private void addCondition() {
			// can only be called if selection is valid
			ISelection joinSelection = joinViewer.getSelection();
			ISelection baseSelection = baseViewer.getSelection();
			Object joinElem = ((IStructuredSelection) joinSelection).getFirstElement();
			Object baseElem = ((IStructuredSelection) baseSelection).getFirstElement();
			PropertyEntityDefinition joinDef = (PropertyEntityDefinition) joinElem;
			PropertyEntityDefinition baseDef = (PropertyEntityDefinition) baseElem;

			JoinCondition condition = new JoinCondition(baseDef, joinDef);
			if (!conditions.contains(condition)) {
				conditions.add(condition);
				conditionViewer.refresh();
			}

			updateCompletionStatus();
		}

		private void updateCompletionStatus() {
			boolean pageComplete = false;
			if (conditions.isEmpty())
				setErrorMessage("Add at least one condition.");
			else {
				// Previous pages are valid, this page has at least one
				// condition.
				// If it has more than one, we need to check the dependency
				// chain.
				// For this purpose use the validate method of JoinParameter...
				if (conditions.size() > 1 && createJoinParameter(typeIndex).validate() != null)
					setErrorMessage(
							"Conditions depend on different types which do not depend on each other.");
				else {
					pageComplete = true;
					setErrorMessage(null);
				}
			}

			// after any condition changes also update following pages...
			// they can become invalid due to the removal of conditions, so it
			// has to happen here, and not onShowPage
			for (ConditionPage page : pages.subList(typeIndex, pages.size()))
				page.updateCompletionStatus();

			// this call after the others to update the buttons, too
			setPageComplete(pageComplete);
		}

		@Override
		protected void onShowPage(boolean firstShow) {
			super.onShowPage(firstShow);

			// no initial data -> check Reference constraints
			if (conditions.isEmpty()) {
				boolean added = false;
				TypeEntityDefinition joinTypeEntity = types.get(typeIndex);
				List<TypeDefinition> baseTypes = new ArrayList<>(typeIndex);
				for (int i = 0; i < typeIndex; i++)
					baseTypes.add(types.get(i).getDefinition());

				added |= addReferenceConditions(joinTypeEntity, types.subList(0, typeIndex), false);
				for (TypeEntityDefinition baseTypeEntity : types.subList(0, typeIndex))
					added |= addReferenceConditions(baseTypeEntity,
							Collections.singletonList(joinTypeEntity), true);

				if (added) {
					conditionViewer.refresh();
					updateCompletionStatus();
				}
			}
		}

		private boolean addReferenceConditions(TypeEntityDefinition joinType,
				List<TypeEntityDefinition> baseTypes, boolean invert) {
			int size = baseTypes.size();
			List<TypeDefinition> baseTypeDefs = new ArrayList<>(size);
			for (int i = 0; i < size; i++)
				baseTypeDefs.add(baseTypes.get(i).getDefinition());
			boolean added = false;

			// only check direct children (->database)
			Collection<? extends ChildDefinition<?>> children = DefinitionUtil
					.getAllChildren(joinType.getDefinition());

			// check all direct children
			for (ChildDefinition<?> child : children) {
				// only properties
				if (child.asProperty() == null)
					continue;
				PropertyDefinition property = child.asProperty();
				Reference ref = property.getConstraint(Reference.class);
				Collection<? extends TypeDefinition> referencedTypes = ref.getReferencedTypes();
				// only if they reference something
				if (referencedTypes == null || referencedTypes.isEmpty())
					continue;

				// check all references
				for (TypeDefinition referencedType : referencedTypes) {
					int index = baseTypeDefs.indexOf(referencedType);
					// only those which occur in the base types
					if (index == -1)
						continue;

					PropertyEntityDefinition baseDef = getPrimaryKeyEntity(baseTypes.get(index));
					// only those with a property primary key
					if (baseDef == null)
						continue;

					// create property entity definitions
					PropertyEntityDefinition joinDef = (PropertyEntityDefinition) AlignmentUtil
							.getChild(joinType, property.getName());

					if (invert) {
						PropertyEntityDefinition tmp = joinDef;
						joinDef = baseDef;
						baseDef = tmp;
					}

					// add join condition
					conditions.add(new JoinCondition(baseDef, joinDef));
					added = true;
				}
			}
			return added;
		}

		private List<ChildDefinition<?>> getPrimaryKeyPath(TypeDefinition type) {
			PrimaryKey key = type.getConstraint(PrimaryKey.class);

			List<ChildDefinition<?>> path = new ArrayList<>();
			Definition<?> parent = type;

			List<QName> namePath = key.getPrimaryKeyPath();
			for (QName name : namePath) {
				ChildDefinition<?> child = DefinitionUtil.getChild(parent, name);
				if (child == null) {
					log.error("Primary key property path could not be resolved in type "
							+ type.getName());
					return null;
				}
				path.add(child);
				parent = child;
			}

			if (key.hasPrimaryKey() && path.get(path.size() - 1) instanceof PropertyDefinition)
				return path;
			else
				return null;
		}

		private PropertyEntityDefinition getPrimaryKeyEntity(TypeEntityDefinition type) {
			List<ChildDefinition<?>> primaryKeyPath = getPrimaryKeyPath(type.getDefinition());
			if (primaryKeyPath != null)
				return (PropertyEntityDefinition) AlignmentUtil.createEntityFromDefinitions(
						type.getType(), primaryKeyPath, type.getSchemaSpace(), type.getFilter());
			else
				return null;
		}

		@Override
		public IWizardPage getNextPage() {
			int pageIndex = typeIndex - 1;
			if (pageIndex + 1 < pages.size())
				return pages.get(pageIndex + 1);
			else
				return JoinParameterPage.super.getNextPage();
		}
	}
}
