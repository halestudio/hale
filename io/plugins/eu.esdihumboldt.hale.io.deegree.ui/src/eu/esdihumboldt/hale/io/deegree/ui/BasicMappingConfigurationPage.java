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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.deegree.ui;

import java.util.Optional;

import org.deegree.cs.coordinatesystems.ICRS;
import org.deegree.cs.persistence.CRSManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import eu.esdihumboldt.hale.common.config.ProviderConfig;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.io.deegree.mapping.GenericMappingConfiguration;
import eu.esdihumboldt.hale.io.deegree.mapping.GenericMappingConfiguration.DatabaseType;
import eu.esdihumboldt.hale.io.deegree.mapping.MappingConfiguration;
import eu.esdihumboldt.hale.io.deegree.mapping.MappingMode;
import eu.esdihumboldt.hale.ui.io.IOWizard;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;
import eu.esdihumboldt.hale.ui.util.viewer.EnumContentProvider;
import eu.esdihumboldt.util.config.Config;

/**
 * Configuration page for basic {@link MappingConfiguration} settings.
 * 
 * @author Simon Templer
 */
public class BasicMappingConfigurationPage
		extends AbstractConfigurationPage<IOProvider, IOWizard<IOProvider>> {

	private final GenericMappingConfiguration mappingConfig = new GenericMappingConfiguration(
			new Config());

	private Button useIntegerIDs;
	private Button useNamespacePrefix;
	private Text connId;

	private ComboViewer dbType;

	private Text dbVersion;

	private StructuredViewer mode;

	private Spinner nameLength;

	private Text crsRef;

	private Spinner dimension;

	private Text srid;

	/**
	 * Default constructor
	 */
	public BasicMappingConfigurationPage() {
		super("deegree.mapping.basic");

		setTitle("deegree Feature Store Mapping");
		setDescription("Basic deegree Feature Store Mapping settings");
	}

	@Override
	protected void onShowPage(boolean firstShow) {
		super.onShowPage(firstShow);

		if (firstShow) {
			// update configuration from provider
			// XXX do this every time?
			mappingConfig.setInternalConfig(ProviderConfig.get(getWizard().getProvider()));

			// fill with defaults if empty
			if (mappingConfig.getInternalConfig().asMap().isEmpty()) {
				mappingConfig.fillDefaults();
			}

			updateFromConfig();
		}
	}

	@Override
	public void loadPreSelection(IOConfiguration conf) {
		super.loadPreSelection(conf);

		// load from configuration
		mappingConfig.setInternalConfig(ProviderConfig.get(conf));

		updateFromConfig();
	}

	@Override
	public boolean updateConfiguration(IOProvider provider) {
		// database

		mappingConfig.setJDBCConnectionId(connId.getText());

		ISelection dbTypeSel = dbType.getSelection();
		DatabaseType type = GenericMappingConfiguration.DEFAULT_DATABASE_TYPE;
		if (!dbTypeSel.isEmpty() && dbTypeSel instanceof IStructuredSelection) {
			type = (DatabaseType) ((IStructuredSelection) dbTypeSel).getFirstElement();
		}
		String version = dbVersion.getText();
		if (version != null && version.isEmpty()) {
			version = null;
		}
		mappingConfig.setDatabaseType(type, version);

		ISelection modeSel = mode.getSelection();
		MappingMode mode = GenericMappingConfiguration.DEFAULT_MAPPING_MODE;
		if (!modeSel.isEmpty() && modeSel instanceof IStructuredSelection) {
			mode = (MappingMode) ((IStructuredSelection) modeSel).getFirstElement();
		}
		mappingConfig.setMappingMode(mode);

		mappingConfig.setMaxNameLength(Optional.of(nameLength.getSelection()).filter(x -> x > 0));

		mappingConfig.setUseNamespacePrefixForTableNames(useNamespacePrefix.getSelection());

		mappingConfig.setUseIntegerIDs(useIntegerIDs.getSelection());

		// CRS

		mappingConfig.setCRSIdentifier(crsRef.getText());

		mappingConfig.setDimension(Optional.of(dimension.getSelection()).filter(x -> x > 0));

		mappingConfig.setSRID(Optional.ofNullable(srid.getText()).filter(x -> !x.isEmpty()));

		// overall

		ProviderConfig.set(mappingConfig.getInternalConfig(), provider);

		return true;
	}

	@Override
	protected void createContent(Composite page) {
		page.setLayout(new GridLayout(1, false));
		GridDataFactory groupData = GridDataFactory.fillDefaults().grab(true, false);

		GridDataFactory defLabel = GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER);
		GridDataFactory longField = GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER)
				.span(2, 1).grab(true, false);

		// Database group

		Group database = new Group(page, SWT.NONE);
		database.setLayout(new GridLayout(3, false));
		database.setText("Database");
		groupData.applyTo(database);

		// connection ID
		Label connIdLabel = new Label(database, SWT.NONE);
		connIdLabel.setText("Connection ID");
		defLabel.applyTo(connIdLabel);
		connId = new Text(database, SWT.SINGLE | SWT.BORDER);
		connId.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				updateState();
			}
		});
		longField.applyTo(connId);

		// database type
		Label dbTypeLabel = new Label(database, SWT.NONE);
		dbTypeLabel.setText("Database type");
		defLabel.applyTo(dbTypeLabel);
		dbType = new ComboViewer(database);
		dbType.setContentProvider(EnumContentProvider.getInstance());
		dbType.setInput(DatabaseType.class);
		longField.applyTo(dbType.getControl());

		// and version
		Label dbVersionLabel = new Label(database, SWT.NONE);
		dbVersionLabel.setText("Database version");
		defLabel.applyTo(dbVersionLabel);
		dbVersion = new Text(database, SWT.SINGLE | SWT.BORDER);
		longField.applyTo(dbVersion);

		// mapping mode
		Label modeLabel = new Label(database, SWT.NONE);
		modeLabel.setText("Mapping mode");
		defLabel.applyTo(modeLabel);
		mode = new ComboViewer(database);
		mode.setContentProvider(EnumContentProvider.getInstance());
		mode.setInput(MappingMode.class);
		longField.applyTo(mode.getControl());

		// max name length
		Label nameLengthLabel = new Label(database, SWT.NONE);
		nameLengthLabel.setText("Max name length");
		defLabel.applyTo(nameLengthLabel);
		nameLength = new Spinner(database, SWT.NONE);
		nameLength.setMinimum(0);
		nameLength.setMaximum(1000);
		nameLength.setIncrement(1);
		nameLength.setPageIncrement(10);
		Label nameLengthDescr = new Label(database, SWT.NONE);
		nameLengthDescr.setText("(setting 0 uses a default value)");

		// namespace prefix
		useNamespacePrefix = new Button(database, SWT.CHECK);
		useNamespacePrefix.setText("Use namespace prefix for names");
		GridDataFactory.swtDefaults().span(3, 1).applyTo(useNamespacePrefix);

		// integer IDs
		useIntegerIDs = new Button(database, SWT.CHECK);
		useIntegerIDs.setText("Use integer IDs for GML IDs");
		GridDataFactory.swtDefaults().span(3, 1).applyTo(useIntegerIDs);

		// CRS group

		Group crs = new Group(page, SWT.NONE);
		crs.setLayout(new GridLayout(3, false));
		crs.setText("Storage CRS");
		groupData.applyTo(crs);

		// CRS reference
		Label crsRefLabel = new Label(crs, SWT.NONE);
		crsRefLabel.setText("CRS Reference");
		defLabel.applyTo(crsRefLabel);
		crsRef = new Text(crs, SWT.SINGLE | SWT.BORDER);
		crsRef.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				updateState();
			}
		});
		longField.applyTo(crsRef);

		// dimension
		Label dimensionLabel = new Label(crs, SWT.NONE);
		dimensionLabel.setText("Dimension");
		defLabel.applyTo(dimensionLabel);
		dimension = new Spinner(crs, SWT.NONE);
		dimension.setMinimum(0);
		dimension.setMaximum(3);
		dimension.setIncrement(1);
		dimension.setPageIncrement(1);
		Label dimensionDescr = new Label(crs, SWT.NONE);
		dimensionDescr.setText("(setting 0 uses the CRS dimension)");

		// SRID
		Label sridLabel = new Label(crs, SWT.NONE);
		sridLabel.setText("Database SRID");
		defLabel.applyTo(sridLabel);
		srid = new Text(crs, SWT.NONE);
		longField.applyTo(srid);
	}

	/**
	 * Update UI components to configuration state.
	 */
	private void updateFromConfig() {
		// database

		String connIdText = mappingConfig.getJDBCConnectionId();
		if (connIdText == null) {
			connIdText = "";
		}
		connId.setText(connIdText);

		dbType.setSelection(new StructuredSelection(mappingConfig.getDatabaseType()));
		String dbVersionText = mappingConfig.getDatabaseVersion();
		if (dbVersionText == null) {
			dbVersionText = "";
		}
		dbVersion.setText(dbVersionText);

		mode.setSelection(new StructuredSelection(mappingConfig.getMode()));

		nameLength.setSelection(mappingConfig.getMaxNameLength().orElse(0));

		useNamespacePrefix.setSelection(mappingConfig.useNamespacePrefixForTableNames());

		useIntegerIDs.setSelection(mappingConfig.useIntegerIDs());

		// CRS

		String crsRefText = mappingConfig.getCRSIdentifier();
		if (crsRefText == null) {
			crsRefText = "";
		}
		crsRef.setText(crsRefText);

		dimension.setSelection(mappingConfig.getDimension().orElse(0));

		String sridText = mappingConfig.getSRID().orElse("");
		srid.setText(sridText);

		// update page state
		updateState();
	}

	/**
	 * Update the page state.
	 */
	private void updateState() {
		boolean ok = true;

		try {

			// connection ID
			String connIdText = connId.getText();
			if (connIdText == null || connIdText.isEmpty()) {
				setErrorMessage("JDBC connection ID for deegree must be specified");
				ok = false;
			}

			// CRS ref
			if (ok) {
				String crsRefText = crsRef.getText();
				if (crsRefText == null || crsRefText.isEmpty()) {
					setErrorMessage("Reference for storage CRS must be specified");
					ok = false;
				}
				else {
					ICRS crs = CRSManager.lookup(crsRefText);
					if (crs == null) {
						setErrorMessage("Reference for storage CRS is not valid");
						ok = false;
					}
					else {
						String name = crs.getName();
						if (name != null) {
							setMessage("CRS: " + name);
						}
					}
				}
			}

		} catch (Exception e) {
			ok = false;
			setErrorMessage(e.getMessage());
		}

		if (ok) {
			setErrorMessage(null);
		}
		setPageComplete(ok);
	}

	@Override
	public void disable() {
		// do nothing
	}

	@Override
	public void enable() {
		// do nothing
	}

}
