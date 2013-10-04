/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.io.source;

import java.io.File;
import java.net.URI;

import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.ui.io.util.OpenFileFieldEditor;
import eu.esdihumboldt.util.io.IOUtils;

/**
 * A {@link OpenFileFieldEditor} with support for relative URIs with regard to
 * the current project's location.
 * 
 * @author Kai Schwierczek
 */
public class FileSourceFileFieldEditor extends OpenFileFieldEditor {

	private final URI projectURI;
	private boolean useRelative;

	/**
	 * Default constructor.
	 */
	public FileSourceFileFieldEditor() {
		this(null);
	}

	/**
	 * Constructor with the specified project URI. Can be used in conjunction
	 * with {@link #setUseRelativeIfPossible(boolean)}.
	 * 
	 * @param projectURI the project URI to use
	 */
	public FileSourceFileFieldEditor(URI projectURI) {
		super();
		this.projectURI = projectURI;
	}

	/**
	 * @see FileFieldEditor#FileFieldEditor(String, String, Composite)
	 * @see #FileSourceFileFieldEditor(URI)
	 */
	public FileSourceFileFieldEditor(String name, String labelText, Composite parent, URI projectURI) {
		super(name, labelText, parent);
		this.projectURI = projectURI;
	}

	/**
	 * @see FileFieldEditor#FileFieldEditor(String, String, boolean, int,
	 *      Composite)
	 * @see #FileSourceFileFieldEditor(URI)
	 */
	public FileSourceFileFieldEditor(String name, String labelText, int validationStrategy,
			Composite parent, URI projectURI) {
		super(name, labelText, false, validationStrategy, parent);
		this.projectURI = projectURI;
	}

	/**
	 * Sets the editor to allow relative values. The projectURI has to be
	 * supplied during construction to support this feature.
	 * 
	 * @param useRelative the new value
	 */
	public void setUseRelativeIfPossible(boolean useRelative) {
		if (this.useRelative && !useRelative) {
			File f = new File(getTextControl().getText());
			f = resolve(f);
			if (f != null)
				getTextControl().setText(f.getAbsolutePath());
			this.useRelative = false;
		}
		else if (!this.useRelative && useRelative && projectURI != null) {
			this.useRelative = true;
			File f = new File(getTextControl().getText());
			URI absoluteSelected = f.toURI();
			URI relativeSelected = IOUtils.getRelativePath(absoluteSelected, projectURI);
			if (!relativeSelected.isAbsolute())
				f = new File(relativeSelected.toString());
			getTextControl().setText(f.getPath());
		}
	}

	/**
	 * @see FileFieldEditor#changePressed()
	 */
	@Override
	protected String changePressed() {
		File f = new File(getTextControl().getText());
		f = resolve(f);
		File d = getFile(f);
		if (d == null) {
			return null;
		}

		if (useRelative) {
			d = d.getAbsoluteFile();
			URI absoluteSelected = d.toURI();
			URI relativeSelected = IOUtils.getRelativePath(absoluteSelected, projectURI);
			if (!relativeSelected.isAbsolute())
				d = new File(relativeSelected.toString());
		}
		return d.getPath();
	}

	@Override
	protected boolean checkState() {
		String msg = null;

		String path = getTextControl().getText();
		if (path != null) {
			path = path.trim();
		}
		else {
			path = "";//$NON-NLS-1$
		}
		if (path.length() == 0) {
			if (!isEmptyStringAllowed()) {
				msg = getErrorMessage();
			}
		}
		else {
			File file = resolve(new File(path));
			if (file == null || !file.isFile()) {
				msg = getErrorMessage();
			}
		}

		if (msg != null) { // error
			showErrorMessage(msg);
			return false;
		}

		if (doCheckState()) { // OK!
			clearErrorMessage();
			return true;
		}
		msg = getErrorMessage(); // subclass might have changed it in the
									// #doCheckState()
		if (msg != null) {
			showErrorMessage(msg);
		}
		return false;
	}

	private File resolve(File f) {
		// first find absolute
		File resolved;
		if (f.isAbsolute())
			resolved = f;
		else if (useRelative)
			resolved = new File(projectURI.resolve(IOUtils.relativeFileToURI(f)));
		else
			resolved = null;

		// then check existence
		if (resolved != null && resolved.exists())
			return resolved;
		else
			return null;
	}
}
