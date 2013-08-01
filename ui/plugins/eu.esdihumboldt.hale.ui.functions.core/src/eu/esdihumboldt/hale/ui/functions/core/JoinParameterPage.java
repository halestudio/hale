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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.extension.function.TypeFunctionExtension;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.functions.JoinFunction;
import eu.esdihumboldt.hale.common.align.model.functions.join.JoinParameter;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.core.io.impl.ComplexValue;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionComparator;
import eu.esdihumboldt.hale.ui.common.definition.viewer.SchemaPatternFilter;
import eu.esdihumboldt.hale.ui.common.definition.viewer.StyledDefinitionLabelProvider;
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

	private final List<ConditionPage> pages = new ArrayList<ConditionPage>();
	private List<TypeEntityDefinition> types = new ArrayList<TypeEntityDefinition>();
	private TableViewer table;

	private Image upIcon;
	private Image downIcon;

	/**
	 * Constructor.
	 */
	public JoinParameterPage() {
		super(TypeFunctionExtension.getInstance().get(ID),
				"Please configure the identifier generation");
		setPageComplete(true); // TODO change to false
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.function.generic.pages.ParameterPage#getConfiguration()
	 */
	@Override
	public ListMultimap<String, ParameterValue> getConfiguration() {
		ListMultimap<String, ParameterValue> result = ArrayListMultimap.create(1, 1);

		// TODO fill with actual value
		JoinParameter param = new JoinParameter();

		result.put(PARAMETER_JOIN, new ParameterValue(new ComplexValue(param)));
		return result;
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
		table.setLabelProvider(new StyledDefinitionLabelProvider());
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
		while (iter.hasNext())
			types.add(AlignmentUtil.getTypeEntity(iter.next().getDefinition()));

		// TODO if it doesn't change, do not rebuild
		if (false)
			return;

		for (ConditionPage page : pages)
			page.dispose();
		pages.clear();
		this.types = types;

		if (table != null)
			table.setInput(types);

		for (int i = 1; i < types.size(); i++) {
			ConditionPage conditionPage = new ConditionPage(i);
			conditionPage.setWizard(getWizard());
			pages.add(conditionPage);
		}
		getWizard().getContainer().updateButtons();

		if (firstShow && !getInitialValues().isEmpty()) {
			// TODO check whether initial values match and use 'em
			setPageComplete(true); // TODO move after checks whether
									// initialValues fit
		}
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

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		if (upIcon != null)
			upIcon.dispose();
		if (downIcon != null)
			downIcon.dispose();
	}

	/**
	 * Page for specifying the join conditions of a type
	 */
	private class ConditionPage extends WizardPage {

		// TODO manage list of conditions
		// TODO fill this page with initial conditions if available
		// TODO fill JoinParameter with data from this page

		private int typeIndex;

		private TreeViewer leftViewer;
		private Button addButton;
		private TreeViewer rightViewer;
		private ListViewer conditions;

		protected ConditionPage(int typeIndex) {
			// TODO get type display name
			super("Join configuration for " + null);
			setPageComplete(false);
			this.typeIndex = typeIndex;
		}

		@Override
		public void createControl(Composite parent) {
			Composite main = new Composite(parent, SWT.NONE);
			main.setLayout(new GridLayout(3, false));

			// This is the page to select Join conditions for the "index"' type.
			Label intro = new Label(main, SWT.NONE);
			intro.setText("Add Join condition(s) for type " + null);
			intro.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

			DefinitionComparator comparator = new DefinitionComparator();
			EntityDefinitionService eds = (EntityDefinitionService) PlatformUI.getWorkbench()
					.getService(EntityDefinitionService.class);

			PatternFilter leftPatternFilter = new SchemaPatternFilter();
			leftPatternFilter.setIncludeLeadingWildcard(true);
			final FilteredTree leftFilteredTree = new TreePathFilteredTree(main, SWT.SINGLE
					| SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER, leftPatternFilter, true);
			leftFilteredTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			leftViewer = leftFilteredTree.getViewer();
			leftViewer.setComparator(comparator);
			leftViewer.setLabelProvider(new StyledDefinitionLabelProvider());
			leftViewer.setContentProvider(new TreePathProviderAdapter(
					new EntityTypeIterableContentProvider(eds, SchemaSpaceID.SOURCE)));
			leftViewer.setInput(Collections.singleton(types.get(typeIndex)));
			leftViewer.addSelectionChangedListener(new ISelectionChangedListener() {

				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					updateButtonStatus();
				}
			});

			addButton = new Button(main, SWT.PUSH);
			addButton.setText("=");
			addButton.setEnabled(false);
			addButton.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					addCondition();
					super.widgetSelected(e);
				}
			});

			PatternFilter rightPatternFilter = new SchemaPatternFilter();
			leftPatternFilter.setIncludeLeadingWildcard(true);
			final FilteredTree rightFilteredTree = new TreePathFilteredTree(main, SWT.SINGLE
					| SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER, rightPatternFilter, true);
			rightFilteredTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			rightViewer = rightFilteredTree.getViewer();
			rightViewer.setComparator(comparator);
			rightViewer.setLabelProvider(new StyledDefinitionLabelProvider());
			rightViewer.setContentProvider(new TreePathProviderAdapter(
					new EntityTypeIterableContentProvider(eds, SchemaSpaceID.SOURCE)));
			rightViewer.setInput(types.subList(0, typeIndex));
			rightViewer.addSelectionChangedListener(new ISelectionChangedListener() {

				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					updateButtonStatus();
				}
			});

			conditions = new ListViewer(main, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
			conditions.setContentProvider(ArrayContentProvider.getInstance());
			conditions.getControl().setLayoutData(
					new GridData(SWT.FILL, SWT.BOTTOM, true, false, 3, 1));

			// TODO conditions as table

			setControl(main);
		}

		/**
		 * Called if the ordering of pages changed, maybe even this page's type
		 * changed.
		 */
		private void checkAndRefresh() {
			// may be called before the page control was created
			if (getControl() == null)
				return;
			leftViewer.setInput(Collections.singleton(types.get(typeIndex)));
			leftViewer.refresh();
			// sublist is a view, so simply refresh viewer
			rightViewer.refresh();
			// TODO check whether created conditions are still valid
			conditions.refresh();
		}

		private void updateButtonStatus() {
			ISelection leftSelection = leftViewer.getSelection();
			ISelection rightSelection = rightViewer.getSelection();

			if (leftSelection.isEmpty() || rightSelection.isEmpty()) {
				addButton.setEnabled(false);
			}
			else {
				Object leftElem = ((IStructuredSelection) leftSelection).getFirstElement();
				Object rightElem = ((IStructuredSelection) rightSelection).getFirstElement();
				EntityDefinition leftDef = (EntityDefinition) leftElem;
				EntityDefinition rightDef = (EntityDefinition) rightElem;

				// TODO better checks (already in list? HasValueFlag?)
				if (leftDef instanceof PropertyEntityDefinition
						&& rightDef instanceof PropertyEntityDefinition)
					addButton.setEnabled(true);
				else
					addButton.setEnabled(false);
			}
		}

		private void addCondition() {
			// Can only be called if selection is valid
			ISelection leftSelection = leftViewer.getSelection();
			ISelection rightSelection = rightViewer.getSelection();
			Object leftElem = ((IStructuredSelection) leftSelection).getFirstElement();
			Object rightElem = ((IStructuredSelection) rightSelection).getFirstElement();
			PropertyEntityDefinition leftDef = (PropertyEntityDefinition) leftElem;
			PropertyEntityDefinition rightDef = (PropertyEntityDefinition) rightElem;

			// TODO add that to the condition list

			conditions.refresh();

			setPageComplete(true);
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
