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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.io.jdbc.JDBCConnection;
import eu.esdihumboldt.hale.io.jdbc.JDBCConstants;
import eu.esdihumboldt.hale.ui.io.IOWizard;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;

/**
 * This retrieves all the schemas from the database
 * 
 * @author Sameer Sheikh
 */
public class SchemasRetrievalPage extends
		AbstractConfigurationPage<ImportProvider, IOWizard<ImportProvider>> implements
		JDBCConstants {

	private ListViewer schemaList;
	private final List<String> schemas = new ArrayList<String>();

	/**
	 * 
	 */
	public SchemasRetrievalPage() {
		super("Schemas Retrieval");
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
		ISelection sel = schemaList.getSelection();
		StringBuilder selSchemas = new StringBuilder();
		if (!sel.isEmpty()) {

			@SuppressWarnings("unchecked")
			Iterator<String> selected = ((IStructuredSelection) sel).iterator();
			selSchemas.append(selected.next());
			while (selected.hasNext()) {
				selSchemas.append("," + selected.next());
			}
			provider.setParameter(SCHEMAS, Value.of(selSchemas));
			return true;
		}
		return false;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#createContent(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		page.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).create());
		schemaList = new ListViewer(page, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
//		schemaList.setFilters(filters);
		schemaList.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				return element.toString();
			}

		});
		schemaList.setContentProvider(ArrayContentProvider.getInstance());
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
		layoutData.widthHint = SWT.DEFAULT;
		layoutData.heightHint = 8 * schemaList.getList().getItemHeight();
		schemaList.getControl().setLayoutData(layoutData);
		setPageComplete(false);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#onShowPage(boolean)
	 */
	@Override
	protected void onShowPage(boolean firstShow) {
		if (firstShow) {
			setPageComplete(true);
		}
		try {
			getSchemas();
			schemaList.setInput(schemas);
		} catch (SQLException e) {

			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @throws SQLException this
	 * 
	 */
	private void getSchemas() throws SQLException {
		Connection conn = null;
		Statement statemnt = null;
		String sql = "select USERNAME from SYS.ALL_USERS order by USERNAME";
		ResultSet rs;
		try {
			conn = JDBCConnection.getConnection(getWizard().getProvider());
			statemnt = conn.createStatement();
			rs = statemnt.executeQuery(sql);
			while (rs.next()) {
				schemas.add(rs.getString("USERNAME"));
			}
		} catch (SQLException e) {

			e.printStackTrace();
		} finally {
			if (statemnt != null) {
				statemnt.close();
			}
			if (conn != null) {
				conn.close();
			}
		}

	}

}
