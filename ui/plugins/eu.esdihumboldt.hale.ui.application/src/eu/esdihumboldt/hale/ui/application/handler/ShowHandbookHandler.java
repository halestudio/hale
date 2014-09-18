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
