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

package eu.esdihumboldt.hale.io.jdbc.spatialite.reader.internal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.common.instance.io.util.InstanceReaderDecorator;
import eu.esdihumboldt.hale.io.jdbc.JDBCInstanceReader;
import eu.esdihumboldt.hale.io.jdbc.spatialite.SpatiaLiteJdbcIOSupplier;

/**
 * Reads instances from a SpatiaLite DB. Wraps {@link JDBCInstanceReader}.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class SpatiaLiteInstanceReader extends InstanceReaderDecorator<JDBCInstanceReader> {

	private LocatableInputSupplier<? extends InputStream> source;

	/**
	 * Default constructor.
	 */
	public SpatiaLiteInstanceReader() {
		super(new JDBCInstanceReader());
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
		internalProvider.setSource(new SpatiaLiteJdbcIOSupplier(new File(source.getLocation())));
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.ImportProvider#execute(eu.esdihumboldt.hale.common.core.io.ProgressIndicator,
	 *      java.lang.String)
	 */
	@Override
	public IOReport execute(ProgressIndicator progress, String resourceIdentifier)
			throws IOProviderConfigurationException, IOException, UnsupportedOperationException {
		// for now loading multiple instances from SpatiaLite DB is not
		// supported.
		throw new UnsupportedOperationException(
				"The operation is not supported for SpatiaLite DB multiple files!");
	}

}
