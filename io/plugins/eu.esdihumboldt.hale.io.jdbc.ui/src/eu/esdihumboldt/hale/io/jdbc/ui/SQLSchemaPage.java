/*
 * Copyright (c) 2017 wetransform GmbH
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

package eu.esdihumboldt.hale.io.jdbc.ui;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;

import javax.xml.namespace.QName;

import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.jdbc.JDBCProvider;
import eu.esdihumboldt.hale.io.jdbc.JDBCUtil;
import eu.esdihumboldt.hale.io.jdbc.SQLSchemaReader;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.common.CommonSharedImages;
import eu.esdihumboldt.hale.ui.io.IOWizard;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;
import eu.esdihumboldt.hale.ui.service.project.ProjectVariablesContentProposalProvider;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;

/**
 * Configuration page for specifying SQL query and type name.
 * 
 * @author Simon Templer
 */
public class SQLSchemaPage
		extends AbstractConfigurationPage<ImportProvider, IOWizard<ImportProvider>> {

	private Text typeName;
	private Text sqlQuery;

	private final ProjectVariablesContentProposalProvider contentProposalProvider = new ProjectVariablesContentProposalProvider(
			true);

	/**
	 * Default constructor.
	 */
	public SQLSchemaPage() {
		super("sql.schema");

		setTitle("SQL Query");
		setDescription("Please specify an SQL query and a type name that should represent it");

		setPageComplete(false);
	}

	@Override
	public void enable() {
		// nothing to do
	}

	@Override
	public void disable() {
		// nothing to do
	}

	@Override
	public boolean updateConfiguration(ImportProvider provider) {
		if (typeName != null) {
			provider.setParameter(SQLSchemaReader.PARAM_TYPE_NAME, Value.of(typeName.getText()));
		}

		if (sqlQuery != null) {
			provider.setParameter(SQLSchemaReader.PARAM_SQL,
					Value.of(new eu.esdihumboldt.hale.common.core.io.Text(sqlQuery.getText())));
		}

		return updateState(true);
	}

	private boolean updateState(boolean runQuery) {
		boolean typeValid = false;
		boolean sqlValid = false;
		String error = null;
		String message = null;

		if (typeName != null) {
			// check type name
			String type = typeName.getText();

			typeValid = type != null && !type.isEmpty();

			if (typeValid) {
				// check if the name already exists in the source schema
				SchemaService schemas = HaleUI.getServiceProvider().getService(SchemaService.class);
				if (schemas != null) {
					TypeDefinition existing = schemas.getSchemas(SchemaSpaceID.SOURCE)
							.getType(new QName(SQLSchemaReader.NAMESPACE, type));
					if (existing != null) {
						typeValid = false;
						error = "An SQL query with this name already exists";
					}
				}

				// also test for specific characters?
			}
		}
		if (sqlQuery != null) {
			// check SQL query
			String sql = sqlQuery.getText();

			sqlValid = sql != null && !sql.isEmpty();

			if (sqlValid) {
				String processedQuery;
				try {
					processedQuery = JDBCUtil.replaceVariables(sql, HaleUI.getServiceProvider());
				} catch (Exception e) {
					error = e.getLocalizedMessage();
					sqlValid = false;
					processedQuery = null;
				}

				// check if processed SQL query can be executed
				if (runQuery && processedQuery != null) {
					ImportProvider provider = getWizard().getProvider();
					if (provider != null && provider instanceof JDBCProvider) {
						Connection connection = null;
						try {
							try {
								connection = ((JDBCProvider) provider).getConnection();
							} catch (SQLException e) {
								sqlValid = false;
								error = "Could not establish database connection: "
										+ e.getLocalizedMessage();
							}

							if (connection != null) {
								try {
									Statement statement = JDBCUtil.createReadStatement(connection,
											1);
									try {
										ResultSet result = statement.executeQuery(processedQuery);
										int columnCount = result.getMetaData().getColumnCount();
										if (columnCount <= 0) {
											sqlValid = false;
											error = "Query result does not have any columns";
										}
										else {
											if (columnCount == 1) {
												message = "Successfully tested query. It yields a result with a single column.";
											}
											else {
												message = MessageFormat.format(
														"Successfully tested query. It yields a result with {0} columns.",
														columnCount);
											}
										}
									} catch (SQLException e) {
										sqlValid = false;
										error = "Error querying database: " + e.getMessage();
									} finally {
										statement.close();
									}
								} catch (SQLException e) {
									sqlValid = false;
									error = "Could not create database statement: "
											+ e.getMessage();
								}
							}
						} finally {
							if (connection != null) {
								try {
									connection.close();
								} catch (SQLException e) {
									// ignore
								}
							}
						}
					}
				}
			}
		}

		boolean complete = typeValid && sqlValid;
		if (complete) {
			error = null;
		}
		else if (!typeValid && error == null) {
			error = "Please provide a name for the query";
		}
		else if (error == null) {
			error = "Please specify the SQL query to use";
		}
		setMessage(message);
		setErrorMessage(error);
		setPageComplete(complete);
		return complete;
	}

	@Override
	protected void createContent(Composite page) {
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(page);

		GridDataFactory labelFactory = GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER);

		// type name
		Label typeLabel = new Label(page, SWT.NONE);
		labelFactory.applyTo(typeLabel);
		typeLabel.setText("Query name:");

		typeName = new Text(page, SWT.SINGLE | SWT.BORDER);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false)
				.applyTo(typeName);
		typeName.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				updateState(false);
			}
		});

		// SQL query
		Label sqlLabel = new Label(page, SWT.NONE);
		labelFactory.applyTo(sqlLabel);
		sqlLabel.setText("SQL query:");

		sqlQuery = new Text(page, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(sqlQuery);
		sqlQuery.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				updateState(false);
			}
		});

		ContentProposalAdapter adapter = new ContentProposalAdapter(sqlQuery,
				new TextContentAdapter(), contentProposalProvider,
				ProjectVariablesContentProposalProvider.CTRL_SPACE, new char[] { '{' });
		adapter.setAutoActivationDelay(0);

		final ControlDecoration infoDeco = new ControlDecoration(sqlQuery, SWT.TOP | SWT.LEFT);
		infoDeco.setDescriptionText("Type Ctrl+Space for project variable content assistance");
		infoDeco.setImage(FieldDecorationRegistry.getDefault()
				.getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION).getImage());
		infoDeco.setShowOnlyOnFocus(true);

		// button for testing query
		Button button = new Button(page, SWT.BORDER | SWT.FLAT);
		GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).span(2, 1).applyTo(button);
		button.setImage(CommonSharedImages.getImageRegistry().get(CommonSharedImages.IMG_PLAY));
		button.setText("Test query");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				updateState(true);
			}
		});

		updateState(false);
	}

}
