/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.core.io.supplier;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import eu.esdihumboldt.util.io.InputSupplier;
import eu.esdihumboldt.util.io.OutputSupplier;

/**
 * I/O supplier based on a {@link File}
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2
 */
public class FileIOSupplier
		implements LocatableInputSupplier<InputStream>, LocatableOutputSupplier<OutputStream> {

	private final File file;
	private final URI usedURI;

	/**
	 * Create a file I/O supplier.
	 * 
	 * @param file the file
	 */
	public FileIOSupplier(File file) {
		this(file, file.toURI());
	}

	/**
	 * Create a file I/O supplier, which may return a relative URI on
	 * {@link #getLocation()}.
	 * 
	 * @param absoluteFile the file
	 * @param usedURI the (relative) URI to use
	 */
	public FileIOSupplier(File absoluteFile, URI usedURI) {
		super();
		this.file = absoluteFile;
		this.usedURI = usedURI;
	}

	/**
	 * @see InputSupplier#getInput()
	 */
	@Override
	public InputStream getInput() throws IOException {
		return new BufferedInputStream(new FileInputStream(file));
	}

	/**
	 * @see OutputSupplier#getOutput()
	 */
	@Override
	public OutputStream getOutput() throws IOException {
		return new BufferedOutputStream(new FileOutputStream(file));
	}

	/**
	 * @see Locatable#getLocation()
	 */
	@Override
	public URI getLocation() {
		return file.toURI();
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((file == null) ? 0 : file.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FileIOSupplier other = (FileIOSupplier) obj;
		if (file == null) {
			if (other.file != null)
				return false;
		}
		else if (!file.equals(other.file))
			return false;
		return true;
	}

	@Override
	public URI getUsedLocation() {
		return usedURI;
	}

}
