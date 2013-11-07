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

package eu.esdihumboldt.hale.io.jdbc;

import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.instance.io.impl.AbstractInstanceReader;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ext.impl.PerTypeInstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.impl.MultiInstanceCollection;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Reads instances from a JDBC database.
 * 
 * @author Simon Templer
 */
public class JDBCInstanceReader extends AbstractInstanceReader implements JDBCConstants {

	private MultiInstanceCollection collection;

	/**
	 * Default constructor.
	 */
	public JDBCInstanceReader() {
		super();

		addSupportedParameter(PARAM_PASSWORD);
		addSupportedParameter(PARAM_USER);
	}

	@Override
	public InstanceCollection getInstances() {
		return collection;
	}

	@Override
	public boolean isCancelable() {
		return false;
	}

	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		progress.begin("Configure database connection", ProgressIndicator.UNKNOWN);
		try {
			// test connection
			try (Connection connection = JDBCConnection.getConnection(this)) {
				connection.createStatement().executeQuery("SELECT 1;");
			}

			String user = getParameter(PARAM_USER).as(String.class);
			String password = getParameter(PARAM_PASSWORD).as(String.class);

			Map<TypeDefinition, InstanceCollection> collections = new HashMap<>();

			// only load instances for mapping relevant types
			for (TypeDefinition type : getSourceSchema().getMappingRelevantTypes()) {
				// TODO test if table exists in DB?

				collections.put(type, new JDBCTableCollection(type, getSource().getLocation(),
						user, password));
			}

			collection = new PerTypeInstanceCollection(collections);
			reporter.setSuccess(true);
		} catch (Exception e) {
			reporter.error(new IOMessageImpl("Error configuring database connection", e));
			reporter.setSuccess(false);
		} finally {
			progress.end();
		}
		return reporter;
	}

	@Override
	protected String getDefaultTypeName() {
		return "Database";
	}

}
