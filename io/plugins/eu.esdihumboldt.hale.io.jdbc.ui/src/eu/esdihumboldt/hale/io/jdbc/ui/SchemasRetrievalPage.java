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

package eu.esdihumboldt.hale.io.jdbc.ui;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.io.jdbc.JDBCConnection;
import eu.esdihumboldt.hale.io.jdbc.JDBCConstants;
import eu.esdihumboldt.hale.io.jdbc.extension.DriverConfiguration;
import eu.esdihumboldt.hale.io.jdbc.extension.DriverConfigurationExtension;
import eu.esdihumboldt.hale.io.jdbc.extension.SchemaSelector;
import eu.esdihumboldt.hale.ui.io.IOWizard;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;

/**
 * This retrieves all the schemas from the database
 * 
 * @author Sameer Sheikh, Arun Varma
 */
public class SchemasRetrievalPage
		extends AbstractConfigurationPage<ImportProvider, IOWizard<ImportProvider>>
		implements JDBCConstants {

	private static final ALogger log = ALoggerFactory.getLogger(SchemasRetrievalPage.class);

	private CheckboxTableViewer schemaTable;
	private final List<String> schemas = new ArrayList<String>();

//	private Composite innerPage;
	private Composite page;

	private boolean isEnable = false;
	private boolean multipleSelection = true;
	private SchemaSelector customSelector = null;
	private DriverConfiguration config = null;

	/**
	 * 
	 */
	public SchemasRetrievalPage() {
		super("schemaRetrieval", "Schemas Retrieval", null);
		setDescription("Please select one or multiple schemas.");
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage#enable()
	 */
	@Override
	public void enable() {
		// Do nothing

	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage#disable()
	 */
	@Override
	public void disable() {
		// Do nothing

	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.IOWizardPage#updateConfiguration(eu.esdihumboldt.hale.common.core.io.IOProvider)
	 */
	@Override
	public boolean updateConfiguration(ImportProvider provider) {
		if (isEnable) {
			Object[] values = schemaTable.getCheckedElements();
			StringBuilder selSchemas = new StringBuilder();
			if (values.length > 0) {
				for (int i = 0; i < values.length; i++) {
					selSchemas.append("," + (String) values[i]);
				}
				provider.setParameter(SCHEMAS, Value.of(selSchemas));
				return true;
			}
			return false;
		}
		return true;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#createContent(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		page.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).create());
		this.page = page;
		// For oracle, by default no schema is selected, so need to set
		// pageComplete false when it created..
		setPageComplete(false);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#onShowPage(boolean)
	 */
	@Override
	protected void onShowPage(boolean firstShow) {

		// load configuration
		loadConfiguration();

		// if page is enable for driver
		if (!isEnable) {
			disposeControl();
			// message
			Label label = new Label(page, SWT.WRAP);
			label.setText("Restricting the tables to load from specific database schemas, is not "
					+ "supported for this database driver. All database tables will be loaded.");
			label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
			page.layout(true, true);
			setMessage("");
			setPageComplete(true);
			return;
		}

		Connection conn = null;
		try {
			conn = getConnection();
		} catch (SQLException e) {
			// Exception occurred in database connection
			// So remove all controls and showing an error with exception
			log.error(e.getMessage(), e);
			disposeControl();
			Label label = new Label(page, SWT.WRAP);
			label.setText("Could not establish connection with database: " + e.getMessage());
			label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
			page.layout(true, true);
			setErrorMessage("Connection error!");
			setPageComplete(false);
			return;
		}

		setErrorMessage(null);
		if (firstShow) {
			// when first time loaded
			createComponent();
		}
		else {
			// When user come to page again
			disposeControl();
			schemas.clear();
			createComponent();
		}
		try {
			// Get schemas using selected database driver
			getSchemas(conn);
			schemaTable.setInput(schemas);

			if (multipleSelection) {
				// if multiple Selection is enabled for driver then by default
				// select all.
				schemaTable.setAllChecked(true);
				setPageComplete(true);
				setMessage("Please select one or multiple schemas.");
			}
			else {
				setMessage("Please select one schema.");
			}
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		}
	}

	private void createComponent() {
		if (multipleSelection)
			schemaTable = CheckboxTableViewer.newCheckList(page,
					SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		else
			schemaTable = CheckboxTableViewer.newCheckList(page,
					SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);

		schemaTable.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				return element.toString();
			}

		});
		schemaTable.setContentProvider(ArrayContentProvider.getInstance());
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
		layoutData.widthHint = SWT.DEFAULT;
		layoutData.heightHint = 8 * schemaTable.getTable().getItemHeight();
		schemaTable.getControl().setLayoutData(layoutData);

		schemaTable.addCheckStateListener(new ICheckStateListener() {

			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				if (event.getChecked() && (!multipleSelection)) {
					schemaTable.setAllChecked(false);
					schemaTable.setCheckedElements(new Object[] { event.getElement() });
				}
				setPageComplete(validate());
			}
		});

		setPageComplete(false);
		page.layout(true, true);
	}

	private void getSchemas(Connection conn) throws SQLException {
		try {
			if (customSelector != null)
				schemas.addAll(customSelector.getSchemas(conn));
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	private Connection getConnection() throws SQLException {
		return JDBCConnection.getConnection(getWizard().getProvider());
	}

	private void loadConfiguration() {
		config = getDriverConfiguration();
		if (config != null) {
			isEnable = config.isSchemaSelectionEnable();
			multipleSelection = config.isMultipleSchemaSelection();
			customSelector = config.getGetSchemaSelector();
		}
	}

	private DriverConfiguration getDriverConfiguration() {
		return DriverConfigurationExtension.getInstance()
				.findDriver(getWizard().getProvider().getSource().getLocation());
	}

	private void disposeControl() {
		for (Control control : page.getChildren()) {
			control.dispose();
		}
	}

	private boolean validate() {
		return (schemaTable.getCheckedElements().length > 0);
	}

}
