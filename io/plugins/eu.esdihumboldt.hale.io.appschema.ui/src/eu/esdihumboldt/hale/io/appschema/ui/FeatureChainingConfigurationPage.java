/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.io.appschema.ui;

import static eu.esdihumboldt.hale.io.appschema.writer.AppSchemaMappingUtils.getJoinParameter;
import static eu.esdihumboldt.hale.io.appschema.writer.AppSchemaMappingUtils.getSortedJoinConditions;
import static eu.esdihumboldt.hale.io.appschema.writer.AppSchemaMappingUtils.getTargetType;
import static eu.esdihumboldt.hale.io.appschema.writer.AppSchemaMappingUtils.isNested;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.functions.join.JoinParameter;
import eu.esdihumboldt.hale.common.align.model.functions.join.JoinParameter.JoinCondition;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.core.io.impl.ComplexValue;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.appschema.AppSchemaIO;
import eu.esdihumboldt.hale.io.appschema.model.ChainConfiguration;
import eu.esdihumboldt.hale.io.appschema.model.FeatureChaining;
import eu.esdihumboldt.hale.io.appschema.writer.AbstractAppSchemaConfigurator;
import eu.esdihumboldt.hale.io.appschema.writer.AppSchemaMappingUtils;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionComparator;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.common.definition.viewer.SchemaPatternFilter;
import eu.esdihumboldt.hale.ui.common.definition.viewer.StyledDefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.function.generic.AbstractGenericFunctionWizard;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService;
import eu.esdihumboldt.hale.ui.service.entity.util.EntityTypeIterableContentProvider;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;
import eu.esdihumboldt.hale.ui.util.viewer.tree.TreePathFilteredTree;
import eu.esdihumboldt.hale.ui.util.viewer.tree.TreePathProviderAdapter;

/**
 * Configuration page for feature chaining settings.
 * 
 * <p>
 * This page is actually a stub whose only responsibility is to create an
 * adequate number of {@link ChainPage} child pages.
 * </p>
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class FeatureChainingConfigurationPage extends
		AbstractConfigurationPage<AbstractAppSchemaConfigurator, AppSchemaAlignmentExportWizard> {

	private final List<ChainPage> pages = new ArrayList<ChainPage>();
	private final FeatureChaining featureChaining = new FeatureChaining();
	private IPageChangingListener changeListener;
	private boolean goingBack = false;

	/**
	 * Default constructor.
	 */
	public FeatureChainingConfigurationPage() {
		super("feature.chaining.conf");
		setTitle("Configure feature chaining");
		setPageComplete(false);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#dispose()
	 */
	@Override
	public void dispose() {
		if (changeListener != null) {
			IWizardContainer container = getContainer();
			if (container instanceof WizardDialog) {
				((WizardDialog) container).removePageChangingListener(changeListener);
			}
		}

		super.dispose();
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage#enable()
	 */
	@Override
	public void enable() {
		// nothing to do... yet?
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage#disable()
	 */
	@Override
	public void disable() {
		// nothing to do... yet?
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.IOWizardPage#loadPreSelection(eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration)
	 */
	@Override
	public void loadPreSelection(IOConfiguration conf) {
		// TODO: I fear this method is never called...
		super.loadPreSelection(conf);

		FeatureChaining previousConf = conf.getProviderConfiguration()
				.get(AppSchemaIO.PARAM_CHAINING).as(FeatureChaining.class);
		if (previousConf != null && previousConf.getJoins().size() > 0) {
			featureChaining.replaceJoins(previousConf.getJoins());
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.IOWizardPage#updateConfiguration(eu.esdihumboldt.hale.common.core.io.IOProvider)
	 */
	@Override
	public boolean updateConfiguration(AbstractAppSchemaConfigurator provider) {
		provider.setParameter(AppSchemaIO.PARAM_CHAINING, new ComplexValue(featureChaining));

		return true;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#onShowPage(boolean)
	 */
	@Override
	protected void onShowPage(boolean firstShow) {
		super.onShowPage(firstShow);

		if (firstShow) {
			for (ChainPage page : pages)
				page.dispose();
			pages.clear();

			AlignmentService alignmentService = HaleUI.getServiceProvider()
					.getService(AlignmentService.class);
			Alignment alignment = alignmentService.getAlignment();

			int pageIdx = 0;
			Collection<? extends Cell> typeCells = alignment.getActiveTypeCells();
			for (Cell typeCell : typeCells) {
				if (AppSchemaMappingUtils.isJoin(typeCell)) {
					JoinParameter joinParameter = getJoinParameter(typeCell);
					List<JoinCondition> conditions = getSortedJoinConditions(joinParameter);
					TypeEntityDefinition joinTarget = getTargetType(typeCell).getDefinition();

					for (int i = 0; i < joinParameter.types.size() - 1; i++) {
						ChainPage chainPage = new ChainPage(pageIdx, typeCell.getId(), i,
								joinParameter.types, conditions, joinTarget);

						chainPage.setWizard(getWizard());
						pages.add(chainPage);
						pageIdx++;
					}
				}
			}
		}

		setPageComplete(true);
		if (!goingBack) {
			getContainer().showPage(getNextPage());
		}
		else {
			getContainer().showPage(getPreviousPage());
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
			return super.getNextPage();
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
			for (ChainPage page : pages)
				if (!page.isPageComplete())
					return false;
		return true;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#createContent(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		IWizardContainer container = getContainer();
		if (container instanceof WizardDialog) {
			changeListener = new IPageChangingListener() {

				@Override
				public void handlePageChanging(PageChangingEvent event) {
					Object currentPage = event.getCurrentPage();
					Object targetPage = event.getTargetPage();

					if ((currentPage instanceof ChainPage
							|| currentPage instanceof AppSchemaDataStoreConfigurationPage)
							&& targetPage instanceof FeatureChainingConfigurationPage) {
						goingBack = true;
					}
					else if (currentPage instanceof IncludeSchemaConfigurationPage
							&& targetPage instanceof FeatureChainingConfigurationPage) {
						goingBack = false;
					}
				}
			};

			WizardDialog dialog = (WizardDialog) container;
			dialog.addPageChangingListener(changeListener);
		}
		else {
			changeListener = null;
		}
	}

	private class ChainPage extends HaleWizardPage<AbstractGenericFunctionWizard<?, ?>> {

		private final List<TypeEntityDefinition> joinTypes;
		private final TypeEntityDefinition joinTarget;
		private final int pageIdx;
		private final String joinCellId;
		private final int chainIdx;
		private final TypeEntityDefinition containerTypeSource;
		private final TypeEntityDefinition nestedTypeSource;
		private EntityDefinition containerTypeTarget;
		private PropertyEntityDefinition nestedTypeTarget;
		private final JoinCondition joinCondition;
		private final String message;
		private boolean uniqueMapping = false;

		// UI
		private TableViewer joinTypesViewer;
		private Button checkUniqueMapping;

		protected ChainPage(int pageIdx, String joinCellId, int chainIdx,
				List<TypeEntityDefinition> joinTypes, List<JoinCondition> joinConditions,
				TypeEntityDefinition joinTarget) {
			super("join-" + joinCellId + "; chain-" + chainIdx, "Chain "
					+ AlignmentUtil.getTypeEntity(joinConditions.get(chainIdx).baseProperty)
							.getDefinition().getDisplayName()
					+ " and "
					+ AlignmentUtil.getTypeEntity(joinConditions.get(chainIdx).joinProperty)
							.getDefinition().getDisplayName(),
					null);
			this.joinCondition = joinConditions.get(chainIdx);
			this.message = "Please select target for nested source type " + AlignmentUtil
					.getTypeEntity(joinCondition.joinProperty).getDefinition().getDisplayName();
			this.joinTypes = joinTypes;
			this.joinTarget = joinTarget;
			this.pageIdx = pageIdx;
			this.joinCellId = joinCellId;
			this.chainIdx = chainIdx;
			this.containerTypeSource = AlignmentUtil
					.getTypeEntity(joinConditions.get(chainIdx).baseProperty);
			this.nestedTypeSource = AlignmentUtil
					.getTypeEntity(joinConditions.get(chainIdx).joinProperty);

			setPageComplete(false);
			setMessage();
		}

		private void setMessage() {
			if (!isPageComplete()) {
				setMessage(this.message, ERROR);
			}
			else {
				setMessage("");
			}
		}

		/**
		 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#onShowPage(boolean)
		 */
		@Override
		protected void onShowPage(boolean firstShow) {
			super.onShowPage(firstShow);

			// can't reliably get the previous chain configuration from current
			// chain configuration, because the latter may not exist yet
			int previousChainIndex = joinTypes.indexOf(containerTypeSource) - 1;
			ChainConfiguration previousChainConf = (previousChainIndex >= 0)
					? featureChaining.getChain(joinCellId, previousChainIndex) : null;
			ChainConfiguration chainConf = featureChaining.getChain(joinCellId, chainIdx);
			// set nested type target
			if (chainConf != null) {
				nestedTypeTarget = chainConf.getNestedTypeTarget();
				uniqueMapping = (chainConf.getMappingName() != null
						&& !chainConf.getMappingName().isEmpty());
				checkUniqueMapping.setSelection(uniqueMapping);
			}
			else {
				nestedTypeTarget = null;
				uniqueMapping = false;
				checkUniqueMapping.setSelection(uniqueMapping);
			}
			// set container type target
			if (previousChainConf != null) {
				containerTypeTarget = previousChainConf.getNestedTypeTarget();
			}
			else {
				containerTypeTarget = joinTarget;
			}

			joinTypesViewer.refresh();
		}

		@Override
		public IWizardPage getNextPage() {
			if (pageIdx + 1 < pages.size())
				return pages.get(pageIdx + 1);
			else
				return FeatureChainingConfigurationPage.super.getNextPage();
		}

		/**
		 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#createContent(org.eclipse.swt.widgets.Composite)
		 */
		@Override
		protected void createContent(Composite page) {
			Composite main = new Composite(page, SWT.NONE);
			main.setLayout(new GridLayout(3, false));

			joinTypesViewer = createJoinTypesViewer(main);
			createTargetTypeViewer(main);
			checkUniqueMapping = new Button(main, SWT.CHECK);
			checkUniqueMapping.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 3, 1));
			checkUniqueMapping.setText("Generate unique mapping for nested target type");
			checkUniqueMapping.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					updateConfiguration();
				}
			});
		}

		private TableViewer createJoinTypesViewer(Composite parent) {
			Composite tableParent = new Composite(parent, SWT.NONE);
			TableColumnLayout layout = new TableColumnLayout();
			tableParent.setLayout(layout);
			GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 2);
			gridData.minimumHeight = 150;
			tableParent.setLayoutData(gridData);

			final TableViewer tableViewer = new TableViewer(tableParent,
					SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
			tableViewer.getControl()
					.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 2));
			tableViewer.setContentProvider(ArrayContentProvider.getInstance());
			tableViewer.getTable().setHeaderVisible(true);
			tableViewer.getTable().setLinesVisible(true);
			// disable selection on table viewer
			tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

				@Override
				public void selectionChanged(final SelectionChangedEvent event) {
					if (!event.getSelection().isEmpty()) {
						tableViewer.setSelection(StructuredSelection.EMPTY);
					}
				}
			});

			final DefinitionLabelProvider dlp = new DefinitionLabelProvider(tableViewer, true,
					true);
			TableViewerColumn typeColumn = new TableViewerColumn(tableViewer, SWT.NONE);
			layout.setColumnData(typeColumn.getColumn(), new ColumnWeightData(1, true));
			typeColumn.setLabelProvider(new ColumnLabelProvider() {

				@Override
				public String getText(Object element) {
					TypeEntityDefinition typeEntityDef = (TypeEntityDefinition) element;
					return dlp.getText(typeEntityDef);
				}

				@Override
				public Image getImage(Object element) {
					TypeEntityDefinition typeEntityDef = (TypeEntityDefinition) element;
					return dlp.getImage(typeEntityDef);
				}
			});
			typeColumn.getColumn().setText("Source Type");

			TableViewerColumn roleColumn = new TableViewerColumn(tableViewer, SWT.NONE);
			layout.setColumnData(roleColumn.getColumn(), new ColumnWeightData(2, true));
			roleColumn.setLabelProvider(new RoleLabelProvider());
			roleColumn.getColumn().setText("Role");

			TableViewerColumn conditionColumn = new TableViewerColumn(tableViewer, SWT.NONE);
			layout.setColumnData(conditionColumn.getColumn(), new ColumnWeightData(2, true));
			conditionColumn.setLabelProvider(new ConditionLabelProvider());
			conditionColumn.getColumn().setText("Join Property");

			TableViewerColumn targetColumn = new TableViewerColumn(tableViewer, SWT.NONE);
			layout.setColumnData(targetColumn.getColumn(), new ColumnWeightData(2, true));
			targetColumn.setLabelProvider(new TargetLabelProvider());
			targetColumn.getColumn().setText("Target Type");

			tableViewer
					.setInput(new TypeEntityDefinition[] { containerTypeSource, nestedTypeSource });

			return tableViewer;
		}

		private TreeViewer createTargetTypeViewer(Composite parent) {

			PatternFilter patternFilter = new SchemaPatternFilter();
			patternFilter.setIncludeLeadingWildcard(true);
			final FilteredTree filteredTree = new TreePathFilteredTree(parent,
					SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER, patternFilter, true);
			GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 2);
			gridData.minimumHeight = 160;
			gridData.minimumWidth = 140;
			filteredTree.setLayoutData(gridData);
			TreeViewer viewer = filteredTree.getViewer();

			viewer.setComparator(new DefinitionComparator());

			EntityDefinitionService eds = PlatformUI.getWorkbench()
					.getService(EntityDefinitionService.class);
			viewer.setContentProvider(new TreePathProviderAdapter(
					new EntityTypeIterableContentProvider(eds, SchemaSpaceID.TARGET)));
			viewer.setLabelProvider(new StyledDefinitionLabelProvider(viewer));

			viewer.setInput(Collections.singletonList(joinTarget));
			viewer.addSelectionChangedListener(new ISelectionChangedListener() {

				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					TreeSelection selection = (TreeSelection) event.getSelection();
					if (!selection.isEmpty()
							&& selection.getFirstElement() instanceof PropertyEntityDefinition) {
						PropertyEntityDefinition selectedProperty = (PropertyEntityDefinition) selection
								.getFirstElement();
						TypeDefinition selectedPropertyType = selectedProperty.getDefinition()
								.getPropertyType();
						List<ChildContext> selectedPropertyPath = selectedProperty
								.getPropertyPath();

						SchemaService schemaService = HaleUI.getServiceProvider()
								.getService(SchemaService.class);
						SchemaSpace targetSchema = schemaService.getSchemas(SchemaSpaceID.TARGET);
						List<ChildContext> containerPath = containerTypeTarget.getPropertyPath();

						if (targetSchema.getMappingRelevantTypes().contains(selectedPropertyType)
								&& isNested(containerPath, selectedPropertyPath)) {
							nestedTypeTarget = selectedProperty;
							setPageComplete(true);
							joinTypesViewer.refresh();
							updateConfiguration();
							return;
						}
					}

					nestedTypeTarget = null;
					setPageComplete(false);
					joinTypesViewer.refresh();

					updateConfiguration();
				}
			});

			return viewer;
		}

		private void updateConfiguration() {
			ChainConfiguration conf = featureChaining.getChain(joinCellId, chainIdx);
			if (conf == null) {
				conf = new ChainConfiguration();
				featureChaining.putChain(joinCellId, chainIdx, conf);
			}
			conf.setChainIndex(chainIdx);
			int containerTypeIdx = joinTypes.indexOf(containerTypeSource);
			// if container type is the first element in the list, no previous
			// chain exists
			conf.setPrevChainIndex(containerTypeIdx - 1);
			conf.setNestedTypeTarget(nestedTypeTarget);
			uniqueMapping = checkUniqueMapping.getSelection();
			if (uniqueMapping) {
				conf.setMappingName(UUID.randomUUID().toString());
			}
			else {
				conf.setMappingName(null);
			}

			setMessage();
		}

		private class RoleLabelProvider extends ColumnLabelProvider {

			@Override
			public String getText(Object element) {
				TypeEntityDefinition typeEntityDef = (TypeEntityDefinition) element;
				return (typeEntityDef.equals(containerTypeSource)) ? "CONTAINER" : "NESTED";
			}

		}

		private class ConditionLabelProvider extends ColumnLabelProvider {

			@Override
			public String getText(Object element) {
				TypeEntityDefinition typeEntityDef = (TypeEntityDefinition) element;
				PropertyEntityDefinition property = (typeEntityDef.equals(containerTypeSource))
						? joinCondition.baseProperty : joinCondition.joinProperty;
				return property.getDefinition().getDisplayName();
			}

		}

		private class TargetLabelProvider extends StyledDefinitionLabelProvider {

			/**
			 * Suppresses mandatory and cardinality attributes display
			 */
			public TargetLabelProvider() {
				super(new DefinitionLabelProvider(null, false, true), true);
			}

			/**
			 * @see eu.esdihumboldt.hale.ui.common.definition.viewer.StyledDefinitionLabelProvider#extractElement(java.lang.Object)
			 */
			@Override
			protected Object extractElement(Object element) {
				TypeEntityDefinition typeEntityDef = (TypeEntityDefinition) element;
				return (typeEntityDef.equals(containerTypeSource)) ? containerTypeTarget
						: nestedTypeTarget;
			}

		}
	}

}
