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

import java.net.URI;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.eclipse.core.runtime.content.IContentType;
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

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import eu.esdihumboldt.hale.ui.common.CommonSharedImages;
import eu.esdihumboldt.hale.ui.service.project.RecentProjectsMenu;
import eu.esdihumboldt.hale.ui.service.project.RecentResources;
import eu.esdihumboldt.hale.ui.util.io.URIFieldEditor;
import eu.esdihumboldt.util.Pair;

/**
 * Field editor for the {@link URLSource}.
 * 
 * @author Simon Templer
 */
public class URLSourceURIFieldEditor extends URIFieldEditor {

	private Button historyButton;
	private Predicate<? super URI> filter;

	/**
	 * @see URIFieldEditor#URIFieldEditor()
	 */
	public URLSourceURIFieldEditor() {
		super();
	}

	/**
	 * @see URIFieldEditor#URIFieldEditor(String, String, Composite)
	 */
	public URLSourceURIFieldEditor(String name, String labelText, Composite parent) {
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

		return super.getTextControl(parent);
	}

	/**
	 * Set a custom filter to apply to URIs. Should be called before any
	 * {@link #setContentTypes(Set)} call.
	 * 
	 * @param filter the filter
	 */
	public void setURIFilter(@Nullable Predicate<? super URI> filter) {
		this.filter = filter;
	}

	/**
	 * Set the allowed content types and enable the history button if
	 * applicable.
	 * 
	 * @param types the supported content types
	 */
	public void setContentTypes(Set<IContentType> types) {
		RecentResources rr = PlatformUI.getWorkbench().getService(RecentResources.class);
		if (rr != null) {
			Predicate<URI> selectUris = new Predicate<URI>() {

				@Override
				public boolean apply(URI uri) {
					// exclude files
					return !"file".equals(uri.getScheme());
				}
			};
			if (filter != null) {
				selectUris = Predicates.and(selectUris, filter);
			}

			final List<Pair<URI, IContentType>> locations = rr.getRecent(types, selectUris);

			if (!locations.isEmpty()) {
				historyButton.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						Menu filesMenu = new Menu(historyButton);
						for (Pair<URI, IContentType> pair : locations) {
							final URI location = pair.getFirst();
							final IContentType contentType = pair.getSecond();
							try {
								MenuItem item = new MenuItem(filesMenu, SWT.PUSH);
								item.setText(
										RecentProjectsMenu.shorten(location.toString(), 80, 20));
								item.addSelectionListener(new SelectionAdapter() {

									@Override
									public void widgetSelected(SelectionEvent e) {
										getTextControl().setText(location.toString());
										getTextControl().setFocus();
										valueChanged();
										onHistorySelected(location, contentType);
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
				});
				historyButton.setEnabled(true);
			}
		}
	}

	/**
	 * Called when an element from the recent resources was selected.
	 * 
	 * @param location the location
	 * @param contentType the content type
	 */
	protected void onHistorySelected(URI location, IContentType contentType) {
		// override me
	}

	@Override
	public int getNumberOfControls() {
		return super.getNumberOfControls() + 1;
	}

}
