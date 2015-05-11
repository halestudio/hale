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

import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableOutputSupplier;
import eu.esdihumboldt.hale.common.instance.io.util.InstanceWriterDecorator;
import eu.esdihumboldt.hale.io.jdbc.JDBCInstanceWriter;
import eu.esdihumboldt.hale.io.jdbc.spatialite.SpatiaLiteJdbcIOSupplier;

/**
 * TODO Type description
 * 
 * @author stefano
 */
public class SpatiaLiteInstanceWriter extends InstanceWriterDecorator<JDBCInstanceWriter> {

	public SpatiaLiteInstanceWriter() {
		super(new JDBCInstanceWriter());
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.util.ExportProviderDecorator#getTarget()
	 */
	@Override
	public LocatableOutputSupplier<? extends OutputStream> getTarget() {
		SpatiaLiteJdbcIOSupplier target = (SpatiaLiteJdbcIOSupplier) internalProvider.getTarget();
		return new FileIOSupplier(new File(target.getDatabaseFilePath()));
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.util.ExportProviderDecorator#setTarget(eu.esdihumboldt.hale.common.core.io.supplier.LocatableOutputSupplier)
	 */
	@Override
	public void setTarget(LocatableOutputSupplier<? extends OutputStream> target) {
		internalProvider.setTarget(new SpatiaLiteJdbcIOSupplier(new File(target.getLocation())));
	}
}
