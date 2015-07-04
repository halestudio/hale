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

package eu.esdihumboldt.hale.io.jdbc.spatialite.writer.internal;

import java.io.File;
import java.io.OutputStream;
import java.net.URI;
import java.util.Map;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableOutputSupplier;
import eu.esdihumboldt.hale.common.instance.io.util.InstanceWriterDecorator;
import eu.esdihumboldt.hale.io.jdbc.JDBCInstanceWriter;
import eu.esdihumboldt.hale.io.jdbc.spatialite.SpatiaLiteJdbcIOSupplier;

/**
 * Writes instances to a SpatiaLite DB. Wraps {@link JDBCInstanceWriter}.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class SpatiaLiteInstanceWriter extends InstanceWriterDecorator<JDBCInstanceWriter> {

	private LocatableOutputSupplier<? extends OutputStream> target;

	/**
	 * Default constructor.
	 */
	public SpatiaLiteInstanceWriter() {
		super(new JDBCInstanceWriter());
	}

	@Override
	public LocatableOutputSupplier<? extends OutputStream> getTarget() {
		return target;
	}

	@Override
	public void loadConfiguration(Map<String, Value> configuration) {
		super.loadConfiguration(configuration);

		Value target = configuration.get(PARAM_TARGET);
		if (target != null && !target.isEmpty()) {
			File file = new File(URI.create(target.as(String.class)));
			setTarget(new FileIOSupplier(file));
		}
	}

	@Override
	public void storeConfiguration(Map<String, Value> configuration) {
		super.storeConfiguration(configuration);

		// store original source
		if (target != null) {
			URI location = target.getLocation();
			if (location != null) {
				configuration.put(PARAM_TARGET, Value.of(location.toString()));
			}
		}
	}

	@Override
	public void setTarget(LocatableOutputSupplier<? extends OutputStream> target) {
		this.target = target;
		internalProvider.setTarget(new SpatiaLiteJdbcIOSupplier(new File(target.getLocation())));
	}
}
