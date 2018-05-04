/*
 * Copyright (c) 2018 wetransform GmbH
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
 *     GeoSolutions <https://www.geo-solutions.it>
 */

package eu.esdihumboldt.hale.io.appschema.ui;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.google.common.base.Joiner;

import eu.esdihumboldt.hale.common.core.io.impl.ComplexValue;
import eu.esdihumboldt.hale.common.core.io.report.impl.DefaultIOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.io.appschema.AppSchemaIO;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.AppSchemaDataAccessType;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.NamespacesPropertyType.Namespace;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.TypeMappingsPropertyType.FeatureTypeMapping;
import eu.esdihumboldt.hale.io.appschema.model.WorkspaceConfiguration;
import eu.esdihumboldt.hale.io.appschema.model.WorkspaceMetadata;
import eu.esdihumboldt.hale.io.appschema.ui.FeatureChainingConfigurationPage.ChainPage;
import eu.esdihumboldt.hale.io.appschema.writer.AbstractAppSchemaConfigurator;
import eu.esdihumboldt.hale.io.appschema.writer.AppSchemaMappingGenerator;
import eu.esdihumboldt.hale.ui.io.ExportWizard;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;

/**
 * Configuration page for workspace related settings.
 * 
 * <p>
 * Allows users to change the name and the value of the isolated attribute for
 * all workspaces that will be created in GeoServer when the App-Schema mapping
 * is published.
 * </p>
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class WorkspaceConfigurationPage extends
		AbstractConfigurationPage<AbstractAppSchemaConfigurator, ExportWizard<AbstractAppSchemaConfigurator>> {

	private static Pattern XML_NAME_PATTERN;

	static {

		String nameStartCharSet = "A-Z_a-z\u00C0-\u00D6\u00D8-\u00F6\u00F8-\u02FF\u0370-\u037D\u037F"
				+ "\u1FFF\u200C\u200D\u2070-\u218F\u2C00\u2FEF\u3001\uD7FF\uF900-\uFDCF"
				+ "\uFDF0-\uFFFD";
		String nameStartChar = "[" + nameStartCharSet + "]";
		String nameChar = ("[" + nameStartCharSet + "\\-.0-9\u0087\u0300-\u036F\u203F-\u2040]");
		String name = "(?:" + nameStartChar + nameChar + "*)";
		XML_NAME_PATTERN = Pattern.compile(name, Pattern.CASE_INSENSITIVE);

	}

	private static final Image CHECKED = AppSchemaUIPlugin.getImageDescriptor("icons/checked.gif")
			.createImage();
	private static final Image UNCHECKED = AppSchemaUIPlugin
			.getImageDescriptor("icons/unchecked.gif").createImage();

	private IPageChangingListener changeListener;
	private final WorkspaceConfiguration workspaceConf;
	private TableViewer workspaceTableViewer;
	private boolean goingBack = false;

	/**
	 * Default constructor.
	 */
	public WorkspaceConfigurationPage() {
		super("workspace.conf");
		setTitle("Configure workspaces");
		setDescription(
				"If needed, edit the name of a workspace and mark it as isolated to avoid name clashes with "
						+ "feature types already published in GeoServer.");
		setPageComplete(true);
		workspaceConf = new WorkspaceConfiguration();
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
	 * @see eu.esdihumboldt.hale.ui.io.IOWizardPage#updateConfiguration(eu.esdihumboldt.hale.common.core.io.IOProvider)
	 */
	@Override
	public boolean updateConfiguration(AbstractAppSchemaConfigurator provider) {
		boolean isValid = true;

		// check for duplicates
		Set<String> newNames = new HashSet<>();
		for (WorkspaceMetadata workspace : workspaceConf.getWorkspaces()) {
			if (newNames.contains(workspace.getName())) {
				// duplicate found
				setErrorMessage("Duplicated workspace name found: \"" + workspace.getName() + "\"");
				isValid = false;
				break;
			}
			newNames.add(workspace.getName());
		}

		if (!isValid) {
			return false;
		}

		provider.setParameter(AppSchemaIO.PARAM_WORKSPACE, new ComplexValue(workspaceConf));
		return true;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#onShowPage(boolean)
	 */
	@Override
	protected void onShowPage(boolean firstShow) {
		super.onShowPage(firstShow);

		IContentType contentType = getWizard().getContentType();
		if (!contentType.getId().equals(AppSchemaIO.CONTENT_TYPE_ARCHIVE)
				&& !contentType.getId().equals(AppSchemaIO.CONTENT_TYPE_REST)) {
			// configuration does not apply, skip page
			if (!goingBack) {
				getContainer().showPage(getNextPage());
			}
			else {
				getContainer().showPage(getPreviousPage());
			}
			return;
		}

		if (!firstShow) {
			// empty feature type collections, since they may change
			for (WorkspaceMetadata workspace : workspaceConf.getWorkspaces()) {
				workspace.getFeatureTypes().clear();
			}
		}

		if (!goingBack) {
			updateWorkspaceTable();
		}
	}

	private void updateWorkspaceTable() {
		AbstractAppSchemaConfigurator configurator = getWizard().getProvider();
		DefaultIOReporter reporter = new DefaultIOReporter(configurator.getTarget(),
				"Generate Temporary App-Schema Mapping", AppSchemaIO.CONTENT_TYPE_MAPPING, true);
		AppSchemaMappingGenerator generator;
		try {
			configurator.generateMapping(reporter);
			generator = configurator.getMappingGenerator();
			AppSchemaDataAccessType appSchemaMapping = generator.getGeneratedMapping()
					.getAppSchemaMapping();
			List<FeatureTypeMapping> typeMappings = appSchemaMapping.getTypeMappings()
					.getFeatureTypeMapping();
			for (FeatureTypeMapping typeMapping : typeMappings) {
				String[] workspaceAndType = typeMapping.getTargetElement().split(":");
				String workspaceName = workspaceAndType[0];
				String typeName = workspaceAndType[1];

				List<Namespace> namespaces = appSchemaMapping.getNamespaces().getNamespace();
				for (Namespace namespace : namespaces) {
					if (workspaceName.equals(namespace.getPrefix())) {
						String uri = namespace.getUri();
						if (workspaceConf.hasWorkspace(uri)) {
							workspaceConf.getWorkspace(uri).getFeatureTypes().add(typeName);
						}
						else {
							WorkspaceMetadata workspace = new WorkspaceMetadata(workspaceName, uri);
							workspace.getFeatureTypes().add(typeName);
							workspaceConf.addWorkspace(workspace);
						}
					}
				}
			}

			// remove workspaces that contain no features
			workspaceConf.getWorkspaces().forEach((ws) -> {
				if (ws.getFeatureTypes().isEmpty()) {
					workspaceConf.removeWorkspace(ws.getNamespaceUri());
				}
			});

			workspaceTableViewer.setInput(workspaceConf.getWorkspaces());
			reporter.setSuccess(true);
		} catch (Exception e) {
			reporter.error(new IOMessageImpl(e.getMessage(), e));
			reporter.setSuccess(false);
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#createContent(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		Composite main = new Composite(page, SWT.NONE);
		main.setLayout(new GridLayout(3, false));

		Composite tableParent = new Composite(main, SWT.NONE);
		TableColumnLayout layout = new TableColumnLayout();
		tableParent.setLayout(layout);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 2);
		gridData.minimumHeight = 150;
		tableParent.setLayoutData(gridData);

		workspaceTableViewer = new TableViewer(tableParent,
				SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
		workspaceTableViewer.getControl()
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 2));
		workspaceTableViewer.setContentProvider(ArrayContentProvider.getInstance());
		workspaceTableViewer.getTable().setHeaderVisible(true);
		workspaceTableViewer.getTable().setLinesVisible(true);
		// disable selection on table viewer
		workspaceTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				// prevent selection
				if (!event.getSelection().isEmpty()) {
					workspaceTableViewer.setSelection(StructuredSelection.EMPTY);
				}
			}
		});

		TableViewerColumn nameColumn = new TableViewerColumn(workspaceTableViewer, SWT.NONE);
		layout.setColumnData(nameColumn.getColumn(), new ColumnWeightData(2, true));
		nameColumn.setLabelProvider(new WorkspaceNameLabelProvider());
		nameColumn.getColumn().setText("Name");
		nameColumn.setEditingSupport(new WorkspaceNameEditingSupport(workspaceTableViewer));

		TableViewerColumn isolatedColumn = new TableViewerColumn(workspaceTableViewer, SWT.NONE);
		layout.setColumnData(isolatedColumn.getColumn(), new ColumnWeightData(1, true));
		isolatedColumn.setLabelProvider(new WorkspaceIsolatedLabelProvider());
		isolatedColumn.getColumn().setText("Isolated");
		isolatedColumn.setEditingSupport(new WorkspaceIsolatedEditingSupport(workspaceTableViewer));

		TableViewerColumn namespaceColumn = new TableViewerColumn(workspaceTableViewer, SWT.NONE);
		layout.setColumnData(namespaceColumn.getColumn(), new ColumnWeightData(3, true));
		namespaceColumn.setLabelProvider(new WorkspaceNamespaceLabelProvider());
		namespaceColumn.getColumn().setText("Namespace");

		TableViewerColumn featuresColumn = new TableViewerColumn(workspaceTableViewer, SWT.NONE);
		layout.setColumnData(featuresColumn.getColumn(), new ColumnWeightData(3, true));
		featuresColumn.setLabelProvider(new WorkspaceFeaturesLabelProvider());
		featuresColumn.getColumn().setText("Features");

		IWizardContainer container = getContainer();
		if (container instanceof WizardDialog) {
			changeListener = new IPageChangingListener() {

				@Override
				public void handlePageChanging(PageChangingEvent event) {
					Object currentPage = event.getCurrentPage();
					Object targetPage = event.getTargetPage();

					if ((currentPage instanceof FeatureChainingConfigurationPage
							|| currentPage instanceof ChainPage)
							&& targetPage instanceof WorkspaceConfigurationPage) {
						goingBack = false;
					}
					else if (currentPage instanceof AppSchemaDataStoreConfigurationPage
							&& targetPage instanceof WorkspaceConfigurationPage) {
						goingBack = true;
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

	private class WorkspaceNameLabelProvider extends ColumnLabelProvider {

		/**
		 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element) {
			WorkspaceMetadata conf = (WorkspaceMetadata) element;
			return conf.getName();
		}
	}

	private class WorkspaceIsolatedLabelProvider extends ColumnLabelProvider {

		/**
		 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element) {
			return null;
		}

		/**
		 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getImage(java.lang.Object)
		 */
		@Override
		public Image getImage(Object element) {
			WorkspaceMetadata conf = (WorkspaceMetadata) element;
			return conf.isIsolated() ? CHECKED : UNCHECKED;
		}
	}

	private class WorkspaceNamespaceLabelProvider extends ColumnLabelProvider {

		/**
		 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element) {
			WorkspaceMetadata conf = (WorkspaceMetadata) element;
			return conf.getNamespaceUri();
		}
	}

	private class WorkspaceFeaturesLabelProvider extends ColumnLabelProvider {

		/**
		 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element) {
			WorkspaceMetadata conf = (WorkspaceMetadata) element;
			return Joiner.on(", ").join(conf.getFeatureTypes());
		}
	}

	private class WorkspaceNameEditingSupport extends EditingSupport {

		private final TableViewer viewer;
		private final CellEditor editor;

		/**
		 * @param viewer the table viewer
		 */
		public WorkspaceNameEditingSupport(TableViewer viewer) {
			super(viewer);
			this.viewer = viewer;
			this.editor = new TextCellEditor(viewer.getTable());
			this.editor.setValidator(new WorkspaceNameValidator());
			this.editor.addListener(new ICellEditorListener() {

				@Override
				public void editorValueChanged(boolean oldValidState, boolean newValidState) {
					WorkspaceConfigurationPage.this.setErrorMessage(editor.getErrorMessage());
				}

				@Override
				public void cancelEditor() {
					WorkspaceConfigurationPage.this.setErrorMessage(null);
				}

				@Override
				public void applyEditorValue() {
					WorkspaceConfigurationPage.this.setErrorMessage(null);
				}
			});
		}

		private WorkspaceMetadata asWorkspace(Object element) {
			return (WorkspaceMetadata) element;
		}

		/**
		 * @see org.eclipse.jface.viewers.EditingSupport#getCellEditor(java.lang.Object)
		 */
		@Override
		protected CellEditor getCellEditor(Object element) {
			return editor;
		}

		/**
		 * @see org.eclipse.jface.viewers.EditingSupport#canEdit(java.lang.Object)
		 */
		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		/**
		 * @see org.eclipse.jface.viewers.EditingSupport#getValue(java.lang.Object)
		 */
		@Override
		protected Object getValue(Object element) {
			return asWorkspace(element).getName();
		}

		/**
		 * @see org.eclipse.jface.viewers.EditingSupport#setValue(java.lang.Object,
		 *      java.lang.Object)
		 */
		@Override
		protected void setValue(Object element, Object value) {
			if (value != null) {
				String newName = String.valueOf(value);
				asWorkspace(element).setName(newName);
				viewer.update(element, null);
			}
		}
	}

	private class WorkspaceIsolatedEditingSupport extends EditingSupport {

		private final TableViewer viewer;

		/**
		 * @param viewer the table viewer
		 */
		public WorkspaceIsolatedEditingSupport(TableViewer viewer) {
			super(viewer);
			this.viewer = viewer;
		}

		private WorkspaceMetadata asWorkspace(Object element) {
			return (WorkspaceMetadata) element;
		}

		/**
		 * @see org.eclipse.jface.viewers.EditingSupport#getCellEditor(java.lang.Object)
		 */
		@Override
		protected CellEditor getCellEditor(Object element) {
			return new CheckboxCellEditor(null, SWT.CHECK | SWT.READ_ONLY);
		}

		/**
		 * @see org.eclipse.jface.viewers.EditingSupport#canEdit(java.lang.Object)
		 */
		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		/**
		 * @see org.eclipse.jface.viewers.EditingSupport#getValue(java.lang.Object)
		 */
		@Override
		protected Object getValue(Object element) {
			return asWorkspace(element).isIsolated();
		}

		/**
		 * @see org.eclipse.jface.viewers.EditingSupport#setValue(java.lang.Object,
		 *      java.lang.Object)
		 */
		@Override
		protected void setValue(Object element, Object value) {
			asWorkspace(element).setIsolated((Boolean) value);
			viewer.update(element, null);
		}

	}

	private class WorkspaceNameValidator implements ICellEditorValidator {

		/**
		 * @see org.eclipse.jface.viewers.ICellEditorValidator#isValid(java.lang.Object)
		 */
		@Override
		public String isValid(Object value) {
			if (value != null) {
				String newName = String.valueOf(value);
				if (newName.isEmpty()) {
					return "Workspace name cannot be empty.";
				}
				if (XML_NAME_PATTERN.matcher(newName).matches()) {
					return null;
				}
				else {
					return "Invalid characters contained in \"" + newName
							+ "\". Start with a letter, follow with letters, numbers, or .-_)";
				}
			}
			else {
				return "Null value provided.";
			}
		}
	}
}
