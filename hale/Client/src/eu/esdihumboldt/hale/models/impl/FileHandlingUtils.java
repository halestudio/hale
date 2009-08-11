/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.models.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Collection of basic file handling utils; was used in some schema importing 
 * techniques.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class FileHandlingUtils {

	/**
	 * Copies a file; used before reading imports
	 * 
	 * @param in
	 * @param out
	 * @throws Exception
	 */
	public static void copyFile(File in, File out) throws Exception {
		FileInputStream fis = new FileInputStream(in);
		FileOutputStream fos = new FileOutputStream(out);
		try {
			byte[] buf = new byte[1024];
			int i = 0;
			while ((i = fis.read(buf)) != -1) {
				fos.write(buf, 0, i);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (fis != null)
				fis.close();
			if (fos != null)
				fos.close();
		}
	}

	public static String getFileContent(File in) throws Exception {
		FileInputStream fis = new FileInputStream(in);
		StringBuffer content = new StringBuffer();
		try {
			byte[] buf = new byte[1024];
			int i = 0;
			while ((i = fis.read(buf)) != -1) {

				content.append(new String(buf, 0, i));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (fis != null)
				fis.close();
		}
		return content.toString();
	}

	public static void writeFileContent(File out, String content)
			throws Exception {
		FileOutputStream fos = new FileOutputStream(out);
		try {
			fos.write(content.getBytes());
		} catch (Exception e) {
			throw e;
		} finally {
			if (fos != null)
				fos.close();
		}
	}	
	
}
