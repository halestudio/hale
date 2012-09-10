/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.common.core.io.supplier;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

import com.google.common.io.InputSupplier;
import com.google.common.io.OutputSupplier;

/**
 * I/O supplier based on a {@link File}
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2
 */
public class FileIOSupplier implements LocatableInputSupplier<FileInputStream>,
		LocatableOutputSupplier<FileOutputStream> {

	private File file;

	/**
	 * Create a file I/O supplier
	 * 
	 * @param file the file
	 */
	public FileIOSupplier(File file) {
		super();
		this.file = file;
	}

	/**
	 * @see InputSupplier#getInput()
	 */
	@Override
	public FileInputStream getInput() throws IOException {
		return new FileInputStream(file);
	}

	/**
	 * @see OutputSupplier#getOutput()
	 */
	@Override
	public FileOutputStream getOutput() throws IOException {
		return new FileOutputStream(file);
	}

	/**
	 * @see Locatable#getLocation()
	 */
	@Override
	public URI getLocation() {
		return file.toURI();
	}

}
