/*
 * Copyright (c) 2021 wetransform GmbH
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import eu.esdihumboldt.util.io.InputSupplier;
import eu.esdihumboldt.util.io.OutputSupplier;

/**
 * I/O supplier based on a {@link File} and used when importing multiple schemas
 * or instances.
 * 
 * @author Kapil Agnihotri
 */
public class FilesIOSupplier
		implements LocatableInputSupplier<InputStream>, LocatableOutputSupplier<OutputStream> {

	private final File file;
	private final List<File> files;
	private final List<URI> usedURILocations;
	private final URI usedURI;

	/**
	 * Create a file I/O supplier.
	 * 
	 * @param file the file
	 */
	public FilesIOSupplier(File file) {
		this(file, file.toURI());
	}

	/**
	 * Create a file I/O supplier, which may return a relative URI on
	 * {@link #getLocation()}.
	 * 
	 * @param absoluteFile the file
	 * 
	 * @param usedURI the (relative) URI to use
	 * 
	 */
	public FilesIOSupplier(File absoluteFile, URI usedURI) {
		this(Arrays.asList(absoluteFile), Arrays.asList(usedURI));
	}

	/**
	 * Create a file I/O supplier, which may return a relative URI on
	 * {@link #getLocation()}.
	 * 
	 * @param files list of files
	 * @param uris list of the (relative) URI to use
	 * 
	 */
	public FilesIOSupplier(List<File> files, List<URI> uris) {
		super();
		this.file = files.get(0);
		this.usedURI = uris.get(0);
		this.files = files;
		this.usedURILocations = uris;
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
		FilesIOSupplier other = (FilesIOSupplier) obj;
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

	/**
	 * Getter
	 * 
	 * @return the list of files
	 */
	public List<File> getFiles() {
		return files;
	}

	/**
	 * Method to get a list of all the locations selected by the user during
	 * multiple import.
	 * 
	 * @return list of URIs.
	 */
	public List<URI> getUsedLocations() {
		return usedURILocations;
	}

	/**
	 * Get the locations
	 * 
	 * @return the list of locations, may be <code>null</code>
	 */
	public List<URI> getLocations() {
		return files.stream().map(f -> f.toURI()).collect(Collectors.toList());
	}

}
