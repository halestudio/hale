/*
 * Copyright (c) 2018 wetransform GmbH
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

package eu.esdihumboldt.hale.io.groovy.ui.snippets;

import java.net.URI;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import eu.esdihumboldt.hale.io.groovy.snippets.SnippetReader;
import eu.esdihumboldt.hale.io.groovy.snippets.SnippetService;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;

/**
 * Snippet configuration page.
 * 
 * @author Simon Templer
 */
public class SnippetConfigurationPage
		extends AbstractConfigurationPage<SnippetReader, SnippetImportWizard> {

	private Text identifierText;

	/**
	 * Default constructor.
	 */
	public SnippetConfigurationPage() {
		super("snippet");

		setTitle("GeoJSON geometry configuration");
		setDescription("Please select the geometries to use for the GeoJSON export");
	}

	@Override
	public void enable() {
		// nothing to do
	}

	@Override
	public void disable() {
		// nothing to do
	}

	@Override
	protected void onShowPage(boolean firstShow) {
		super.onShowPage(firstShow);

		if (firstShow) {
			// determine default for identifier from file name

			URI loc = null;
			try {
				loc = getWizard().getProvider().getSource().getLocation();
			} catch (NullPointerException e) {
				// ignore
			}
			if (loc != null) {
				String baseName = FilenameUtils.getBaseName(loc.getPath());
				if (baseName != null && !baseName.isEmpty()) {
					baseName = baseName.replaceAll("[^\\w]", "_");
					identifierText.setText(baseName);
					updateState();
				}
			}
		}
	}

	@Override
	protected void createContent(Composite page) {
		page.setLayout(new GridLayout(2, false));

		Label identifierLabel = new Label(page, SWT.NONE);
		identifierLabel.setText("Snippet identifier:");

		identifierText = new Text(page, SWT.BORDER | SWT.SINGLE);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false)
				.applyTo(identifierText);
		identifierText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				updateState();
			}
		});

		Label description = new Label(page, SWT.NONE);
		description.setText(
				"The snippet identifier serves to identify the snippet when referenced from a Groovy script.");
		GridDataFactory.swtDefaults().span(2, 1).applyTo(description);

		updateState();
	}

	/**
	 * Update the page state.
	 */
	protected void updateState() {
		if (identifierText == null) {
			setPageComplete(false);
			return;
		}

		setErrorMessage(null);
//		setMessage(null);
		String id = identifierText.getText();
		boolean valid = false;
		if (id == null || id.isEmpty()) {
			setErrorMessage("Please provide an identifier");
		}
		else {
			valid = true;

			SnippetService ss = HaleUI.getServiceProvider().getService(SnippetService.class);
			if (ss != null) {
				if (ss.getSnippet(id).isPresent()) {
					setErrorMessage("There is already a snippet with the given identifer");
					valid = false;
				}
			}
		}

		setPageComplete(valid);
	}

	@Override
	public boolean updateConfiguration(SnippetReader provider) {
		String id = identifierText.getText();
		if (id == null || id.isEmpty()) {
			return false;
		}
		provider.setIdentifier(id);
		return true;
	}

}
