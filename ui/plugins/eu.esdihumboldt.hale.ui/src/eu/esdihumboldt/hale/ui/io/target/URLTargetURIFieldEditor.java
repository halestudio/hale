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

package eu.esdihumboldt.hale.ui.io.target;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

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
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.service.project.RecentProjectsMenu;
import eu.esdihumboldt.hale.ui.util.io.URIFieldEditor;

/**
 * Field editor for the {@link URLTarget}.
 * 
 * @author Simon Templer
 */
public class URLTargetURIFieldEditor extends URIFieldEditor {

	private static final String SETTING_URL_HISTORY = "target.url.history";

	private static final int HISTORY_MAX_SIZE = 20;

	private Button historyButton;

	/**
	 * @see URIFieldEditor#URIFieldEditor()
	 */
	public URLTargetURIFieldEditor() {
		super();
	}

	/**
	 * @see URIFieldEditor#URIFieldEditor(String, String, Composite)
	 */
	public URLTargetURIFieldEditor(String name, String labelText, Composite parent) {
		super(name, labelText, parent);
	}

	// recent resources support

	@Override
	protected void adjustForNumColumns(int numColumns) {
		((GridData) getTextControl().getLayoutData()).horizontalSpan = numColumns - 1;
	}

	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		super.doFillIntoGrid(parent, numColumns - 1);
	}

	@Override
	public Text getTextControl(Composite parent) {
		// ensure resource control is added before the text control
		historyButton = new Button(parent, SWT.PUSH | SWT.FLAT);
		historyButton.setToolTipText("Choose from recent URLs");
		historyButton.setImage(
				CommonSharedImages.getImageRegistry().get(CommonSharedImages.IMG_HISTORY));
		historyButton.setEnabled(false);

		historyButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ProjectService ps = PlatformUI.getWorkbench().getService(ProjectService.class);
				final List<String> locations = ps.getConfigurationService()
						.getList(SETTING_URL_HISTORY);
				if (locations != null && !locations.isEmpty()) {
					Menu filesMenu = new Menu(historyButton);
					for (String locationString : locations) {
						try {
							final URI location = URI.create(locationString);
							MenuItem item = new MenuItem(filesMenu, SWT.PUSH);
							item.setText(RecentProjectsMenu.shorten(location.toString(), 80, 20));
							item.addSelectionListener(new SelectionAdapter() {

								@Override
								public void widgetSelected(SelectionEvent e) {
									getTextControl().setText(location.toString());
									getTextControl().setFocus();
									valueChanged();
									onHistorySelected(location);
								}
							});
						} catch (Exception e1) {
							// ignore
						}
					}

					Point histLoc = historyButton.getParent()
							.toDisplay(historyButton.getLocation());
					filesMenu.setLocation(histLoc.x, histLoc.y + historyButton.getSize().y);
					filesMenu.setVisible(true);
				}
			}
		});

		return super.getTextControl(parent);
	}

	/**
	 * Update the URL history (project specific).
	 */
	public void updateHistory() {
		ProjectService ps = PlatformUI.getWorkbench().getService(ProjectService.class);
		final List<String> locations = ps.getConfigurationService().getList(SETTING_URL_HISTORY);

		if (locations != null && !locations.isEmpty()) {
			historyButton.setEnabled(true);
		}
		else {
			historyButton.setEnabled(false);
		}
	}

	/**
	 * Called when an element from the recent resources was selected.
	 * 
	 * @param location the location
	 */
	protected void onHistorySelected(URI location) {
		// override me
	}

	@Override
	public int getNumberOfControls() {
		return super.getNumberOfControls() + 1;
	}

	/**
	 * Get the entered URI.
	 * 
	 * @param store if the URI should be stored in the history
	 * @return the URI or <code>null</code>
	 */
	@Nullable
	public URI getURI(boolean store) {
		URI uri = super.getURI();
		if (uri != null && store) {
			String value = uri.toString();
			// store the URI in the history list
			ProjectService ps = PlatformUI.getWorkbench().getService(ProjectService.class);
			List<String> history = ps.getConfigurationService().getList(SETTING_URL_HISTORY);
			if (history != null) {
				List<String> newHistory = new ArrayList<>(history);

				boolean unchanged = history.size() >= 1 && history.get(0).equals(value);

				if (!unchanged) {
					// remove any occurrences
					while (newHistory.remove(value)) {
						// nothing to do
					}
					// insert at beginning
					newHistory.add(0, value);
					while (newHistory.size() > HISTORY_MAX_SIZE) {
						newHistory.remove(newHistory.size() - 1);
					}
					ps.getConfigurationService().setList(SETTING_URL_HISTORY, newHistory);
				}
			}
			else {
				// just store the new value
				ps.getConfigurationService().setList(SETTING_URL_HISTORY,
						Collections.singletonList(value));
			}

			updateHistory();
		}
		return uri;
	}

}
