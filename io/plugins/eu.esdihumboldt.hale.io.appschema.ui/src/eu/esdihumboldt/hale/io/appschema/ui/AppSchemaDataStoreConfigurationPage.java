package eu.esdihumboldt.hale.io.appschema.ui;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import eu.esdihumboldt.hale.common.core.io.impl.ComplexValue;
import eu.esdihumboldt.hale.io.appschema.AppSchemaIO;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.SourceDataStoresPropertyType.DataStore;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.SourceDataStoresPropertyType.DataStore.Parameters;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.SourceDataStoresPropertyType.DataStore.Parameters.Parameter;
import eu.esdihumboldt.hale.io.appschema.writer.AbstractAppSchemaConfigurator;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;

/**
 * 
 * Configuration page for source DataStore.
 * 
 * <p>
 * Current implementation can handle just a single PostGIS datastore.
 * </p>
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class AppSchemaDataStoreConfigurationPage extends
		AbstractConfigurationPage<AbstractAppSchemaConfigurator, AppSchemaAlignmentExportWizard> {

	private static final String DEFAULT_MESSAGE = " Specify PostGIS datastore parameters";
	private static final Parameter DBTYPE_PARAMETER = new Parameter();
	private static final String HAS_WHITESPACE = ".*\\s.*";

	static {
		DBTYPE_PARAMETER.setName("dbtype");
		DBTYPE_PARAMETER.setValue("postgis");
	}

	private Text host;

	private Text database;

	private Text schema;

	private Text user;

	private Text password;

	private Button exposePK;

	/**
	 * Default constructor.
	 */
	public AppSchemaDataStoreConfigurationPage() {
		super("datastore.conf");
		setTitle("App-Schema DataStore configuration");
		resetMessage();
	}

	@Override
	public void enable() {
		// do nothing
	}

	@Override
	public void disable() {
		// do nothing
	}

	@Override
	public boolean updateConfiguration(AbstractAppSchemaConfigurator provider) {
		if (!validateHost()) {
			updateMessage(Field.HOST);
			return false;
		}
		if (!validateDatabase()) {
			updateMessage(Field.DATABASE);
			return false;
		}
		if (!validateSchema()) {
			updateMessage(Field.SCHEMA);
			return false;
		}

		DataStore dataStoreParam = provider.getParameter(AppSchemaIO.PARAM_DATASTORE).as(
				DataStore.class);
		if (dataStoreParam == null) {
			dataStoreParam = new DataStore();
		}
		if (dataStoreParam.getParameters() == null) {
			dataStoreParam.setParameters(new Parameters());
		}

		String hostValue = host.getText();
		Parameter hostParam = new Parameter();
		hostParam.setName("host");
		hostParam.setValue(extractHost(hostValue));
		dataStoreParam.getParameters().getParameter().add(hostParam);

		Integer portValue = extractPort(hostValue);
		if (portValue != null) {
			Parameter portParam = new Parameter();
			portParam.setName("port");
			portParam.setValue(portValue.toString());
			dataStoreParam.getParameters().getParameter().add(portParam);
		}

		Parameter databaseParam = new Parameter();
		databaseParam.setName("database");
		databaseParam.setValue(database.getText());
		dataStoreParam.getParameters().getParameter().add(databaseParam);

		Parameter schemaParam = new Parameter();
		schemaParam.setName("schema");
		schemaParam.setValue(schema.getText());
		dataStoreParam.getParameters().getParameter().add(schemaParam);

		Parameter userParam = new Parameter();
		userParam.setName("user");
		userParam.setValue(user.getText());
		dataStoreParam.getParameters().getParameter().add(userParam);

		Parameter passwordParam = new Parameter();
		passwordParam.setName("passwd");
		passwordParam.setValue(password.getText());
		dataStoreParam.getParameters().getParameter().add(passwordParam);

		Parameter exposePKParam = new Parameter();
		exposePKParam.setName("Expose primary keys");
		exposePKParam.setValue(Boolean.toString(exposePK.getSelection()));
		dataStoreParam.getParameters().getParameter().add(exposePKParam);

		// TODO: only "postgis" dbtype is supported so far
		dataStoreParam.getParameters().getParameter().add(DBTYPE_PARAMETER);

		provider.setParameter(AppSchemaIO.PARAM_DATASTORE, new ComplexValue(dataStoreParam));

		return true;
	}

	private void updateMessage(Field updatedField) {
		switch (updatedField) {
		case HOST:
			if (!validateHost()) {
				setErrorMessage("Invalid host specified.");
			}
			else {
				resetMessage();
			}
			break;
		case DATABASE:
			if (!validateDatabase()) {
				setErrorMessage("Invalid database specified.");
			}
			else {
				resetMessage();
			}
			break;
		case SCHEMA:
			if (!validateSchema()) {
				setErrorMessage("Invalid schema specified.");
			}
			else {
				resetMessage();
			}
			break;
		default:
			break;
		}
	}

	private void resetMessage() {
		setErrorMessage(null);
		setMessage(DEFAULT_MESSAGE, IMessageProvider.INFORMATION);
	}

	private boolean validateHost() {
		String hostValue = host.getText();
		if (hostValue == null || hostValue.trim().isEmpty()) {
			return false;
		}

		// validate port, if any was specified
		String[] hostAndPort = hostValue.split(":");
		if (hostAndPort.length > 2) {
			return false;
		}
		else if (hostAndPort.length == 2) {
			return extractPort(hostValue) != null;
		}
		else {
			// TODO: validate hostname/IP address?
			return true;
		}
	}

	private String extractHost(String host) {
		if (host == null || host.trim().isEmpty()) {
			return null;
		}

		String[] hostAndPort = host.split(":");
		return hostAndPort[0];
	}

	private Integer extractPort(String host) {
		if (host == null || host.trim().isEmpty()) {
			return null;
		}

		String[] hostAndPort = host.split(":");
		if (hostAndPort.length == 2) {
			try {
				return Integer.valueOf(hostAndPort[1]);
			} catch (NumberFormatException e) {
				// ignore exception
			}
		}

		return null;
	}

	private boolean validateDatabase() {
		String databaseValue = database.getText();
		// database name shall be specified and shall not contain spaces
		return databaseValue != null && !databaseValue.matches(HAS_WHITESPACE);
	}

	private boolean validateSchema() {
		String schemaValue = schema.getText();
		// if specified, schema name shall not contain spaces
		return (schemaValue == null) || (!schemaValue.matches(HAS_WHITESPACE));
	}

	@Override
	protected void createContent(Composite page) {
		page.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());

		GridDataFactory labelData = GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER);
		GridDataFactory compData = GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER)
				.grab(true, false);

		// host
		Label labelHost = new Label(page, SWT.NONE);
		labelHost.setText("Host(:Port)");
		labelData.applyTo(labelHost);

		host = new Text(page, SWT.BORDER | SWT.SINGLE);
		host.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				updateMessage(Field.HOST);
			}
		});
		compData.applyTo(host);

		// database
		Label labelDatabase = new Label(page, SWT.NONE);
		labelDatabase.setText("Database");
		labelData.applyTo(labelDatabase);

		database = new Text(page, SWT.BORDER | SWT.SINGLE);
		database.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				updateMessage(Field.DATABASE);
			}
		});
		compData.applyTo(database);

		// schema
		Label labelSchema = new Label(page, SWT.NONE);
		labelSchema.setText("Schema");
		labelData.applyTo(labelSchema);

		schema = new Text(page, SWT.BORDER | SWT.SINGLE);
		schema.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				updateMessage(Field.SCHEMA);
			}
		});
		compData.applyTo(schema);

		// user
		Label labelUser = new Label(page, SWT.NONE);
		labelUser.setText("Username");
		labelData.applyTo(labelUser);

		user = new Text(page, SWT.BORDER | SWT.SINGLE);
		compData.applyTo(user);

		// password
		Label labelPassword = new Label(page, SWT.NONE);
		labelPassword.setText("Password");
		labelData.applyTo(labelPassword);

		password = new Text(page, SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
		compData.applyTo(password);

		// expose primary keys
		Label labelExposePK = new Label(page, SWT.NONE);
		labelExposePK.setText("Expose primary keys");
		labelData.applyTo(labelExposePK);

		exposePK = new Button(page, SWT.CHECK);
		compData.applyTo(exposePK);
		// set initial value to true
		exposePK.setSelection(true);
	}

	private enum Field {
		HOST, DATABASE, SCHEMA
	}
}
