/*
 * Copyright (c) 2016 wetransform GmbH
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

package eu.esdihumboldt.hale.io.jdbc.msaccess;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.common.schema.io.util.SchemaReaderDecorator;
import eu.esdihumboldt.hale.io.jdbc.JDBCSchemaReader;

/**
 * Reads a schema from a MS Access DB. Wraps {@link JDBCSchemaReader}.
 * 
 * @author Arun
 */
public class MsAccessSchemaReader extends SchemaReaderDecorator<JDBCSchemaReader> {

	private LocatableInputSupplier<? extends InputStream> source;

	/**
	 * Default Constructor
	 */
	public MsAccessSchemaReader() {
		super(new JDBCSchemaReader());

		// remove quotation from Schema and Table name in query.
		internalProvider.setUseQuotes(false);
	}

	@Override
	public LocatableInputSupplier<? extends InputStream> getSource() {
		return source;
	}

	@Override
	public void loadConfiguration(Map<String, Value> configuration) {
		super.loadConfiguration(configuration);

		Value source = configuration.get(PARAM_SOURCE);
		if (source != null && !source.isEmpty()) {
			setSource(new DefaultInputSupplier(URI.create(source.as(String.class))));
		}
	}

	@Override
	public void storeConfiguration(Map<String, Value> configuration) {
		super.storeConfiguration(configuration);

		// store original source
		if (source != null) {
			URI location = source.getUsedLocation();
			if (location != null) {
				configuration.put(PARAM_SOURCE, Value.of(location.toString()));
			}
		}
	}

	@Override
	public void setSource(LocatableInputSupplier<? extends InputStream> source) {
		this.source = source;
		internalProvider.setSource(new MsAccessJdbcIOSupplier(new File(source.getLocation())));
	}
}
