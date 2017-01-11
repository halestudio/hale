/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.functions.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.google.common.base.Joiner;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.extension.function.FunctionParameterDefinition;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.common.definition.viewer.StyledDefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.function.generic.AbstractGenericFunctionWizard;
import eu.esdihumboldt.hale.ui.function.generic.pages.ParameterPage;
import eu.esdihumboldt.hale.ui.service.project.ProjectVariablesContentProposalProvider;

/**
 * Base parameter page for parameter pages that contain a listing of source
 * types which can be put together to a target value.
 * 
 * @param <T> the type of the text field/editor
 * 
 * @author Kai Schwierczek
 */
public abstract class SourceListParameterPage<T>
		extends HaleWizardPage<AbstractGenericFunctionWizard<?, ?>>
		implements ParameterPage, IContentProposalProvider {

	private String initialValue = "";
	private T textField;

	private final List<EntityDefinition> variables = new ArrayList<EntityDefinition>();
	private TableViewer varTable;

	private final List<IContentProposalProvider> additionalContentProposalProviders = new ArrayList<>();
	private final ProjectVariablesContentProposalProvider projectVariablesProposalsProvider = new ProjectVariablesContentProposalProvider();

	/**
	 * @see HaleWizardPage#HaleWizardPage(String, String, ImageDescriptor)
	 */
	protected SourceListParameterPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	/**
	 * @see HaleWizardPage#HaleWizardPage(String)
	 */
	protected SourceListParameterPage(String pageName) {
		super(pageName);
	}

	/**
	 * Should return the parameter which should be configured using all source
	 * properties.
	 * 
	 * @return the parameter name
	 */
	protected abstract String getParameterName();

	/**
	 * Should return the name of the source property which should be used.
	 * 
	 * @return the source property name
	 */
	protected abstract String getSourcePropertyName();

	/**
	 * Subclasses can configure the text field to for example add some
	 * validation mechanism.
	 * 
	 * @param textField the text field to configure
	 */
	protected void configure(T textField) {
		// default: do nothing
	}

	/**
	 * @return the list of {@link EntityDefinition}s
	 */
	protected List<EntityDefinition> getVariables() {
		return variables;
	}

	/**
	 * Adds an {@link IContentProposalProvider} to this parameter page that may
	 * contribute to the content assistance.
	 * 
	 * @param provider provider to add
	 */
	protected void addContentProposalProvider(IContentProposalProvider provider) {
		this.additionalContentProposalProviders.add(provider);
	}

	/**
	 * This gets called for all variables.<br>
	 * Subclasses can change how they are displayed here.<br>
	 * The default format is like "part1.part2.name".
	 * 
	 * @param variable the variable
	 * @return the modified name
	 */
	protected String getVariableName(EntityDefinition variable) {
		if (variable.getPropertyPath() != null && !variable.getPropertyPath().isEmpty()) {
			List<String> names = new ArrayList<String>();
			for (ChildContext context : variable.getPropertyPath()) {
				names.add(context.getChild().getName().getLocalPart());
			}
			String longName = Joiner.on('.').join(names);
			return longName;
		}
		else
			return variable.getDefinition().getDisplayName();
	}

	/**
	 * This gets called, when the user chose other source properties.
	 * 
	 * @param variables the new source properties
	 */
	protected void sourcePropertiesChanged(Iterable<EntityDefinition> variables) {
		// do nothing by default
	}

	/**
	 * @see ParameterPage#setParameter(Set, ListMultimap)
	 */
	@Override
	public void setParameter(Set<FunctionParameterDefinition> params,
			ListMultimap<String, ParameterValue> initialValues) {
		for (FunctionParameterDefinition param : params) {
			if (param.getName().equals(getParameterName())) {
				String description = param.getDescription();
				if (description != null) {
					setMessage(description);
				}
				String displayName = param.getDisplayName();
				if (displayName != null) {
					setTitle(displayName);
				}
				break;
			}
		}

		if (initialValues != null) {
			List<ParameterValue> initialData = initialValues.get(getParameterName());
			if (initialData.size() > 0)
				initialValue = initialData.get(0).as(String.class);
		}
	}

	/**
	 * @see ParameterPage#getConfiguration()
	 */
	@Override
	public ListMultimap<String, ParameterValue> getConfiguration() {
		ListMultimap<String, ParameterValue> params = ArrayListMultimap.create();
		params.put(getParameterName(), new ParameterValue(getText(textField)));
		return params;
	}

	/**
	 * @see HaleWizardPage#onShowPage(boolean)
	 */
	@Override
	protected void onShowPage(boolean firstShow) {
		Cell cell = getWizard().getUnfinishedCell();

		// update variables as they could have changed
		variables.clear();

		List<? extends Entity> sourceEntities = cell.getSource().get(getSourcePropertyName());
		for (Entity entity : sourceEntities) {
			variables.add(entity.getDefinition());
		}

		Map<EntityDefinition, String> varsAndNames = determineDefaultVariableNames(variables);
		varTable.setInput(varsAndNames.entrySet());

		// Update project variables content provider
		projectVariablesProposalsProvider.reload();

		// inform subclasses
		sourcePropertiesChanged(varsAndNames.keySet());

		((Composite) getControl()).layout();
	}

	/**
	 * Determine the variable names from the corresponding entity definitions.<br>
	 * <br>
	 * The default implementation uses
	 * {@link #getVariableName(EntityDefinition)} to determine the name for each
	 * variable independently.
	 * 
	 * @param variables the variables
	 * @return the variables associated to the variable names to use
	 */
	protected Map<EntityDefinition, String> determineDefaultVariableNames(
			List<EntityDefinition> variables) {
		Map<EntityDefinition, String> result = new LinkedHashMap<EntityDefinition, String>();

		for (EntityDefinition var : variables) {
			result.put(var, getVariableName(var));
		}

		return result;
	}

	/**
	 * @see HaleWizardPage#createContent(Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		GridLayout layout = GridLayoutFactory.swtDefaults().create();
		// Add margin to leave space for control decorations
		layout.marginLeft = 5;
		layout.marginRight = 5;
		page.setLayout(layout);

		// input field
		textField = createAndLayoutTextField(page);

		// let subclasses for example add validation
		configure(textField);

		addContentProposalProvider(projectVariablesProposalsProvider);

		setText(textField, initialValue);

		// variables
		Label label = new Label(page, SWT.NONE);
		label.setText("Available variables (double click to insert)");
		label.setLayoutData(GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER)
				.indent(4, 12).create());

		// variables table
		Composite tableComposite = new Composite(page, SWT.NONE);
		tableComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, false)
				.hint(SWT.DEFAULT, 100).create());
		TableColumnLayout columnLayout = new TableColumnLayout();
		tableComposite.setLayout(columnLayout);
		varTable = new TableViewer(tableComposite, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);
		varTable.getTable().setHeaderVisible(true);
		TableViewerColumn entityColumn = new TableViewerColumn(varTable, SWT.NONE);
		columnLayout.setColumnData(entityColumn.getColumn(), new ColumnWeightData(2, true));
		entityColumn.getColumn().setText("Entity");
		varTable.setContentProvider(ArrayContentProvider.getInstance());
		varTable.setLabelProvider(new StyledDefinitionLabelProvider(varTable) {

			@Override
			protected Object extractElement(Object element) {
				if (element instanceof Entry) {
					return ((Entry<?, ?>) element).getKey();
				}

				return super.extractElement(element);
			}

		});
		varTable.getTable().addMouseListener(new MouseAdapter() {

			/**
			 * @see MouseAdapter#mouseDoubleClick(MouseEvent)
			 */
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				ISelection sel = varTable.getSelection();
				if (!sel.isEmpty() && sel instanceof IStructuredSelection) {
					Object selected = ((IStructuredSelection) sel).getFirstElement();
					if (selected instanceof Entry) {
						selected = ((Entry<?, ?>) selected).getValue();
					}

					insertTextAtCurrentPos(textField, selected.toString());
				}
			}
		});

		// variable name column
		TableViewerColumn varColumn = new TableViewerColumn(varTable, SWT.NONE);
		columnLayout.setColumnData(varColumn.getColumn(), new ColumnWeightData(1, true));
		varColumn.getColumn().setText("Variable");
		varColumn.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				Object element = cell.getElement();

				if (element instanceof Entry) {
					element = ((Entry<?, ?>) element).getValue();
				}

				cell.setText(element.toString());
			}
		});
	}

	/**
	 * Set the text of the text field to the given value.
	 * 
	 * @param textField the text field
	 * @param value the value to set as text
	 */
	protected abstract void setText(T textField, String value);

	/**
	 * Get the current text of the text field.
	 * 
	 * @param textField the text field
	 * @return the current text of the text field
	 */
	protected abstract String getText(T textField);

	/**
	 * Insert a given text at the current position of the given text field.
	 * 
	 * @param textField the text field
	 * @param insert the text to insert
	 */
	protected abstract void insertTextAtCurrentPos(T textField, String insert);

	/**
	 * Create and text field and layout it.
	 * 
	 * @param parent the parent composite, it has a one-column grid layout
	 * 
	 * @return the created text field
	 */
	protected abstract T createAndLayoutTextField(Composite parent);

	/**
	 * Get the text editor/field.
	 * 
	 * @return the text field
	 */
	protected T getTextField() {
		return textField;
	}

	/**
	 * @see org.eclipse.jface.fieldassist.IContentProposalProvider#getProposals(java.lang.String,
	 *      int)
	 */
	@Override
	public IContentProposal[] getProposals(final String contents, final int position) {
		final List<IContentProposal> proposals = new ArrayList<>();

		variables.forEach(var -> proposals.add(createContentProposal(contents, position, var)));
		additionalContentProposalProviders.forEach(provider -> proposals
				.addAll(Arrays.asList(provider.getProposals(contents, position))));

		return proposals.toArray(new IContentProposal[proposals.size()]);
	}

	/**
	 * TODO
	 * 
	 * @param entity
	 * @return
	 */
	protected IContentProposal createContentProposal(String contents, int position,
			EntityDefinition entity) {
		return new IContentProposal() {

			@Override
			public String getLabel() {
				return entity.getDefinition().getDisplayName();
			}

			@Override
			public String getDescription() {
				return null;
			}

			@Override
			public int getCursorPosition() {
				return getContent().length();
			}

			@Override
			public String getContent() {
				return getVariableName(entity);
			}
		};
	}

}
