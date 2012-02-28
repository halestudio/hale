/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.ui.application.handler;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;

import edu.umd.cs.findbugs.annotations.SuppressWarnings;
import eu.esdihumboldt.hale.ui.application.internal.HALEApplicationPlugin;
import eu.esdihumboldt.hale.ui.util.ExceptionHelper;

/**
 * This class is the handler for opening a PDF-manual.
 * 
 * @author Michel Krämer
 */
public class ShowHandbookHandler extends AbstractHandler implements IHandler {
	
	/**
	 * The name of the PDF file to open
	 */
	private static final String PDFFILE = "hale_manual_en.pdf"; //$NON-NLS-1$

	/**
	 * @see IHandler#execute(ExecutionEvent)
	 */
	@SuppressWarnings(value = "RV_RETURN_VALUE_IGNORED_BAD_PRACTICE", justification = "mkdirs seems to often report a wrong result, e.g. on Windows")
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		String tempDirName = System.getProperty("java.io.tmpdir"); //$NON-NLS-1$
		if (!tempDirName.endsWith("/")) { //$NON-NLS-1$
			tempDirName = tempDirName + "/"; //$NON-NLS-1$
		}
		tempDirName = tempDirName + HALEApplicationPlugin.PLUGIN_ID + "/"; //$NON-NLS-1$

		File tempDir = new File(tempDirName);
		tempDir.mkdirs();
		tempDir.deleteOnExit();

		File pdfFile = new File(tempDirName + PDFFILE);
		if (pdfFile.exists()) {
			FileUtils.deleteQuietly(pdfFile);
		}
		
		pdfFile.deleteOnExit();

		URL pdfUrl = this.getClass().getResource("/documentation/" + PDFFILE); //$NON-NLS-1$
		if (pdfUrl == null) {
			throw new RuntimeException("Manual could not be retrieved."); //$NON-NLS-1$
		}

		InputStream in;
		try {
			in = pdfUrl.openStream();
		} catch (IOException e) {
			ExceptionHelper.handleException("Could not open Streaming.", //$NON-NLS-1$
					HALEApplicationPlugin.PLUGIN_ID, e);
			return null;
		}

		FileOutputStream fos = null;
		byte[] buffer = new byte[4096];
		int read;
		try {
			fos = new FileOutputStream(pdfFile);

			while ((read = in.read(buffer)) != -1) {
				fos.write(buffer, 0, read);
			}
		} catch (IOException e) {
			ExceptionHelper.handleException("Error while reading the file.", //$NON-NLS-1$
					HALEApplicationPlugin.PLUGIN_ID, e);
			return null;
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
				in.close();
			} catch (IOException e) {
				// ignore
			}
		}

		try {
			Desktop.getDesktop().open(pdfFile);
		} catch (IOException e) {
			ExceptionHelper.handleException("The file could not be opened", //$NON-NLS-1$
					HALEApplicationPlugin.PLUGIN_ID, e);
		}

		return null;
	}

}
