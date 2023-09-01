/*
 * Copyright (c) 2023 wetransform GmbH
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

package eu.esdihumboldt.hale.common.core.io.supplier;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

/**
 * I/O supplier based on a {@link ByteArrayOutputStream}
 * 
 * @author EmanuelaEpure
 */
public class ByteArrayOutputStreamSupplier
		implements LocatableInputSupplier<InputStream>, LocatableOutputSupplier<OutputStream> {

	private final ByteArrayOutputStream byteArrayOutputStream;

	/**
	 * Create a ByteArrayOutputStream I/O supplier.
	 * 
	 * @param byteArrayOutputStream the byteArrayOutputStream
	 * 
	 */
	public ByteArrayOutputStreamSupplier(ByteArrayOutputStream byteArrayOutputStream) {
		this.byteArrayOutputStream = byteArrayOutputStream;
	}

	/**
	 * @see eu.esdihumboldt.util.io.InputSupplier#getInput()
	 */
	@Override
	public InputStream getInput() throws IOException {
		return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.supplier.Locatable#getLocation()
	 */
	@Override
	public URI getLocation() {
		return null;
	}

	/**
	 * @see eu.esdihumboldt.util.io.OutputSupplier#getOutput()
	 */
	@Override
	public OutputStream getOutput() throws IOException {
		return byteArrayOutputStream;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier#getUsedLocation()
	 */
	@Override
	public URI getUsedLocation() {
		return null;
	}

}
