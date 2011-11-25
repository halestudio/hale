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

package eu.esdihumboldt.util.io;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;

/**
 * Helper class for IO
 * 
 * @author Kai Schwierczek
 */
public final class IOUtils {
	/**
	 * Static class, constructor private.
	 */
	private IOUtils() {
	}

	/**
	 * Tests whether a InputStream to the given URI can be opened. <br>
	 * In case of a file it instead tests File.isFile and File.canRead() because
	 * it is a lot faster.
	 * 
	 * @param uri the URI to test
	 * @return true, if a InputStream to the URI could be opened.
	 */
	public static boolean testStream(URI uri) {
		if ("file".equalsIgnoreCase(uri.getScheme())) {
			File file = new File(uri);
			if (file.isFile() && file.canRead())
				return true;
			return false;
		}
		// could be further enhanced to check for example for http response codes like 404.
		try {
			uri.toURL().openConnection().getInputStream().close();
		} catch (MalformedURLException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		return true;
	}
}
