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

package eu.esdihumboldt.hale.ui.io.source;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
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
import eu.esdihumboldt.hale.ui.util.io.ExtendedFileFieldEditor;
import eu.esdihumboldt.hale.ui.util.io.OpenFileFieldEditor;
import eu.esdihumboldt.util.Pair;
import eu.esdihumboldt.util.io.IOUtils;

/**
 * Abstract class to have common functionalities for the implementation of
 * {@link OpenFileFieldEditor} with support for relative URIs with regard to the
 * current project's location. Applicable when importing multiple files.
 * 
 * @author Kapil Agnihotri
 */
public abstract class AbstractMultipleFilesSourceFileFieldEditor extends OpenFileFieldEditor {

	private final URI projectURI;
	private boolean useRelative;
	private Button historyButton;
	private Text textField;
	private final int validationStrategy;

	/**
	 * @see ExtendedFileFieldEditor#ExtendedFileFieldEditor(String, String,
	 *      boolean, int, Composite, int )
	 */
	@SuppressWarnings("javadoc")
	public AbstractMultipleFilesSourceFileFieldEditor(String name, String labelText,
			int validationStrategy, Composite parent, URI projectURI, int style) {
		super(name, labelText, false, validationStrategy, parent, style);
		this.validationStrategy = validationStrategy;
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
		String filepaths = getTextControl().getText();
		List<String> filepathsAsList = getFilepathsAsList(filepaths);
		File f = null;
		if (filepathsAsList != null && filepathsAsList.size() > 0) {
			f = new File(filepaths);
			f = resolve(f);
		}
		List<File> d = getFiles(f);
		StringBuffer sb = new StringBuffer();
		List<String> processedFiles = processFiles(d);
		if (processedFiles.size() == 1) {
			sb.append(processedFiles.get(0));
		}
		else {
			processedFiles.forEach(k -> sb.append(k).append("\n"));
		}
		return sb.toString();
	}

	/**
	 * Process a selected file and produce the path to use.
	 * 
	 * @param files List of files to be processed.
	 * 
	 * @return the list of paths.
	 */
	protected List<String> processFiles(List<File> files) {
		if (files == null) {
			return null;
		}
		List<String> paths = new ArrayList<String>();
		if (useRelative) {
			files.forEach(k -> {
				File d = k.getAbsoluteFile();
				URI absoluteSelected = d.toURI();
				URI relativeSelected = IOUtils.getRelativePath(absoluteSelected, projectURI);
				if (!relativeSelected.isAbsolute()) {
					File file = new File(relativeSelected.toString());
					paths.add(file.getAbsolutePath());
				}
			});
		}
		else {
			files.forEach(k -> {
				File d = k.getAbsoluteFile();
				paths.add(d.getPath());
			});
		}
		return paths;
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
			String errorMsg = restrictExtensions(path);
			if (errorMsg != null && !errorMsg.isEmpty()) {
				showErrorMessage(errorMsg);
				return false;
			}
			Optional<String> findAny = getFilepathsAsList(path).stream()
					.filter(k -> ((new File(k)) == null) || !(new File(k)).isFile()).findAny();
			if (findAny.isPresent()) {
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

	/**
	 * Method to restrict files with specific extensions.
	 * 
	 * @param path file path.
	 * @return response message based on the checks.
	 */
	protected String restrictExtensions(String path) {
		String msg = null;
		List<String> filepaths = getFilepathsAsList(path);

		// check if the selected files are with different extensions.
		Set<String> allSelectedFileExtensions = filepaths.stream()
				.map(p -> p.substring(p.lastIndexOf("."))).collect(Collectors.toSet());
		if (allSelectedFileExtensions.size() > 1) {
			msg = "All files must be of the same format!";
			return msg;
		}
		return msg;

	}

	/**
	 * Resolve a files existence.
	 * 
	 * @param f file.
	 * @return null if file does not exists else the file.
	 */
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

		if (textField == null) {
			textField = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
			textField.setFont(parent.getFont());

			switch (validationStrategy) {
			case VALIDATE_ON_KEY_STROKE:
				textField.addKeyListener(new KeyAdapter() {

					@Override
					public void keyReleased(KeyEvent e) {
						valueChanged();
					}
				});
				textField.addFocusListener(new FocusAdapter() {

					// Ensure that the value is checked on focus loss in case we
					// missed a keyRelease or user hasn't released key.
					// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=214716
					@Override
					public void focusLost(FocusEvent e) {
						valueChanged();
					}
				});

				break;
			case VALIDATE_ON_FOCUS_LOST:
				textField.addKeyListener(new KeyAdapter() {

					@Override
					public void keyPressed(KeyEvent e) {
						clearErrorMessage();
					}
				});
				textField.addFocusListener(new FocusAdapter() {

					@Override
					public void focusGained(FocusEvent e) {
						refreshValidState();
					}

					@Override
					public void focusLost(FocusEvent e) {
						valueChanged();
					}
				});
				break;
			default:
				Assert.isTrue(false, "Unknown validate strategy");//$NON-NLS-1$
			}
			textField.addDisposeListener(event -> textField = null);
			textField.setTextLimit(UNLIMITED);

		}
		else {
			checkParent(textField, parent);
		}

		return textField;
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
											List<String> texts = processFiles(Arrays.asList(file));
											if (texts != null) {
												textField.append(texts.get(0));
												textField.append("\n");
												textField.setFocus();
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

	/**
	 * @return the historyButton
	 */
	public Button getHistoryButton() {
		return historyButton;
	}

	/**
	 * // * Method to return list of filepaths from filepath string delimited by
	 * "\n".
	 * 
	 * @param filepaths file path string, delimited by \n.
	 * @return list of file paths after splitting.
	 */
	protected List<String> getFilepathsAsList(String filepaths) {
		String stringValue = getStringValue();
		String[] split = stringValue.split("\n");

		List<String> collect = Arrays.asList(split).stream().filter(s -> !s.isEmpty())
				.collect(Collectors.toList());
		return collect;

	}

}
