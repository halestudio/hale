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
import java.io.InputStream;

import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.common.schema.io.util.SchemaReaderDecorator;
import eu.esdihumboldt.hale.io.jdbc.JDBCSchemaReader;
import eu.esdihumboldt.hale.io.jdbc.spatialite.SpatiaLiteJdbcIOSupplier;

/**
 * Reads a schema from a SpatiaLite DB. Wraps {@link JDBCSchemaReader}.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class SpatiaLiteSchemaReader extends SchemaReaderDecorator<JDBCSchemaReader> {

	/**
	 * Default constructor.
	 */
	public SpatiaLiteSchemaReader() {
		super(new JDBCSchemaReader());
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.util.ImportProviderDecorator#getSource()
	 */
	@Override
	public LocatableInputSupplier<? extends InputStream> getSource() {
		SpatiaLiteJdbcIOSupplier source = (SpatiaLiteJdbcIOSupplier) internalProvider.getSource();
		return new FileIOSupplier(new File(source.getDatabaseFilePath()));
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.util.ImportProviderDecorator#setSource(eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier)
	 */
	@Override
	public void setSource(LocatableInputSupplier<? extends InputStream> source) {
		internalProvider.setSource(new SpatiaLiteJdbcIOSupplier(new File(source.getLocation())));
	}
}
