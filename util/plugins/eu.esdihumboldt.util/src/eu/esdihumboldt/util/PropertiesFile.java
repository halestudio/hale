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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Properties bound to a file. The file is assumed to use the ISO 8859-1
 * character encoding; that is each byte is one Latin1 character. Characters not
 * in Latin1, and certain special characters, are represented in keys and
 * elements using Unicode escapes.
 * 
 * @author Simon Templer
 */
public class PropertiesFile extends Properties {

	private static final long serialVersionUID = 7287298262633854843L;

	/**
	 * The properties file.
	 */
	private final File file;

	/**
	 * Create or load the given properties file.
	 * 
	 * @param file the properties file
	 * 
	 * @throws IOException if creating or loading the file fails
	 */
	public PropertiesFile(File file) throws IOException {
		this(file, null);
	}

	/**
	 * Create or load the given properties file.
	 * 
	 * @param file the properties file
	 * @param defaults the default properties
	 * 
	 * @throws IOException if creating or loading the file fails
	 */
	public PropertiesFile(File file, Properties defaults) throws IOException {
		super(defaults);

		this.file = file;

		if (!file.exists()) {
			file.createNewFile();
		}
		else {
			reload();
		}
	}

	/**
	 * Reload the properties from the file.
	 * 
	 * @throws IOException if loading the file fails
	 */
	public void reload() throws IOException {
		clear();
		load();
	}

	private void load() throws IOException {
		clear();
		InputStream in = new BufferedInputStream(new FileInputStream(file));
		try {
			load(in);
		} finally {
			in.close();
		}
	}

	/**
	 * Save the properties to the file, overwriting it.
	 * 
	 * @throws IOException if saving the file fails
	 */
	public void save() throws IOException {
		OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
		try {
			store(out, null);
		} finally {
			out.close();
		}
	}

	/**
	 * Sync the properties with the file. For duplicate properties you can
	 * either prefer the runtime property values or the file property values.
	 * 
	 * @param preferRuntime if the runtime properties should be preferred
	 * @throws IOException if loading or saving the file fails
	 */
	public void sync(boolean preferRuntime) throws IOException {
		if (!file.exists()) {
			file.createNewFile();
		}

		if (!preferRuntime) {
			// just load
			load();
		}
		else {
			PropertiesFile fileProperties = new PropertiesFile(file);
			for (String property : fileProperties.stringPropertyNames()) {
				if (!containsKey(property)) {
					// set the property in the runtime properties if there is no
					// value set
					setProperty(property, fileProperties.getProperty(property));
				}
			}
		}

		// and save
		save();
	}

}
