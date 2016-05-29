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
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.ui.common.CommonSharedImages;
import eu.esdihumboldt.hale.ui.service.project.RecentProjectsMenu;
import eu.esdihumboldt.hale.ui.service.project.RecentResources;
import eu.esdihumboldt.hale.ui.util.io.OpenFileFieldEditor;
import eu.esdihumboldt.util.Pair;
import eu.esdihumboldt.util.io.IOUtils;

/**
 * A {@link OpenFileFieldEditor} with support for relative URIs with regard to
 * the current project's location.
 * 
 * @author Kai Schwierczek
 * @author Simon Templer
 */
public class FileSourceFileFieldEditor extends OpenFileFieldEditor {

	private final URI projectURI;
	private boolean useRelative;
	private Button historyButton;

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
	@SuppressWarnings("javadoc")
	public FileSourceFileFieldEditor(String name, String labelText, Composite parent,
			URI projectURI) {
		super(name, labelText, parent);
		this.projectURI = projectURI;
	}

	/**
	 * @see FileFieldEditor#FileFieldEditor(String, String, boolean, int,
	 *      Composite)
	 * @see #FileSourceFileFieldEditor(URI)
	 */
	@SuppressWarnings("javadoc")
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

		return processFile(d);
	}

	/**
	 * Process a selected file and produce the path to use.
	 * 
	 * @param file the file
	 * @return the path
	 */
	protected String processFile(File file) {
		if (file == null) {
			return null;
		}

		if (useRelative) {
			file = file.getAbsoluteFile();
			URI absoluteSelected = file.toURI();
			URI relativeSelected = IOUtils.getRelativePath(absoluteSelected, projectURI);
			if (!relativeSelected.isAbsolute())
				file = new File(relativeSelected.toString());
		}
		return file.getPath();
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

	// recent resources support

	@Override
	protected void adjustForNumColumns(int numColumns) {
		((GridData) getTextControl().getLayoutData()).horizontalSpan = numColumns - 2;
	}

	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		super.doFillIntoGrid(parent, numColumns - 1);
	}

	@Override
	public Text getTextControl(Composite parent) {
		// ensure resource control is added before the text control
		historyButton = new Button(parent, SWT.PUSH | SWT.FLAT);
		historyButton.setToolTipText("Choose from recent files");
		historyButton.setImage(
				CommonSharedImages.getImageRegistry().get(CommonSharedImages.IMG_HISTORY));
		historyButton.setEnabled(false);

		return super.getTextControl(parent);
	}

	@Override
	public void setContentTypes(Set<IContentType> types) {
		super.setContentTypes(types);

		RecentResources rr = PlatformUI.getWorkbench().getService(RecentResources.class);
		if (rr != null) {
			final List<Pair<URI, IContentType>> files = rr.getRecent(types, true);

			if (!files.isEmpty()) {
				historyButton.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						Menu filesMenu = new Menu(historyButton);
						for (Pair<URI, IContentType> pair : files) {
							final File file;
							try {
								file = new File(pair.getFirst());
								if (file.exists()) {
									// only offer existing files

									MenuItem item = new MenuItem(filesMenu, SWT.PUSH);
									item.setText(RecentProjectsMenu.shorten(file.toString(), 80,
											file.getName().length()));
									item.addSelectionListener(new SelectionAdapter() {

										@Override
										public void widgetSelected(SelectionEvent e) {
											String text = processFile(file);
											if (text != null) {
												getTextControl().setText(text);
												getTextControl().setFocus();
												valueChanged();
											}
										}
									});
								}
							} catch (Exception e1) {
								// ignore
							}
						}

						Point histLoc = historyButton.getParent()
								.toDisplay(historyButton.getLocation());
						filesMenu.setLocation(histLoc.x, histLoc.y + historyButton.getSize().y);
						filesMenu.setVisible(true);
					}
				});
				historyButton.setEnabled(true);
			}
		}
	}

	@Override
	public int getNumberOfControls() {
		return super.getNumberOfControls() + 1;
	}
}
