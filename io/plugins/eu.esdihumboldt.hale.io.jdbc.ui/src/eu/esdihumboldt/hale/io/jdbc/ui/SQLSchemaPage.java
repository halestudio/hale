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

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.io.jdbc.JDBCUtil;
import eu.esdihumboldt.hale.io.jdbc.SQLSchemaReader;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.io.IOWizard;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;

/**
 * Configuration page for specifying SQL query and type name.
 * 
 * @author Simon Templer
 */
public class SQLSchemaPage
		extends AbstractConfigurationPage<ImportProvider, IOWizard<ImportProvider>> {

	private Text typeName;
	private Text sqlQuery;

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

		return true;
	}

	private void updateState() {
		boolean typeValid = false;
		boolean sqlValid = false;
		String error = null;

		if (typeName != null) {
			// check type name
			String type = typeName.getText();

			typeValid = type != null && !type.isEmpty();

			// also test for specific characters?
		}
		if (sqlQuery != null) {
			// check SQL query
			String sql = sqlQuery.getText();

			sqlValid = sql != null && !sql.isEmpty();

			if (sqlValid) {
				@SuppressWarnings("unused")
				String processedQuery;
				try {
					processedQuery = JDBCUtil.replaceVariables(sql, HaleUI.getServiceProvider());
				} catch (Exception e) {
					error = e.getLocalizedMessage();
					sqlValid = false;
					processedQuery = null;
				}

				// TODO check if processed SQL query can be executed
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
		setErrorMessage(error);
		setPageComplete(complete);
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
				updateState();
			}
		});

		// SQL query
		Label sqlLabel = new Label(page, SWT.NONE);
		labelFactory.applyTo(sqlLabel);
		sqlLabel.setText("SQL query:");

		sqlQuery = new Text(page, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(sqlQuery);
		sqlQuery.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				updateState();
			}
		});

		// TODO allow testing the query!

		updateState();
	}

}
