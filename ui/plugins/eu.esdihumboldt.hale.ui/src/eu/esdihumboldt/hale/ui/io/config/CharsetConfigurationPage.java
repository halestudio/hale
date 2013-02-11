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

package eu.esdihumboldt.hale.ui.io.config;

import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.ui.io.IOWizard;

/**
 * Configuration page for the character encoding.
 * 
 * @author Simon Templer
 */
public class CharsetConfigurationPage extends
		AbstractConfigurationPage<IOProvider, IOWizard<IOProvider>> {

	private Combo charsetCombo;

	/**
	 * Default constructor.
	 */
	public CharsetConfigurationPage() {
		super("charset");

		setTitle("Character encoding");
		setDescription("Select a character encoding.");
	}

	@Override
	public void enable() {
		// select I/O provider default charset
		IOProvider pro = getWizard().getProvider();
		if (pro != null) {
			Charset cs = pro.getCharset();
			if (cs != null) {
				charsetCombo.setText(cs.name());
				update();
			}
		}
	}

	@Override
	public void disable() {
		// nothing to do
	}

	@Override
	public boolean updateConfiguration(IOProvider provider) {
		try {
			Charset cs = Charset.forName(charsetCombo.getText());
			if (cs != null) {
				provider.setCharset(cs);
				return true;
			}
		} catch (Exception e) {
			// ignore
		}

		return false;
	}

	@Override
	protected void createContent(Composite page) {
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(page);

		Label clabel = new Label(page, SWT.NONE);
		clabel.setText("Charset: ");

		charsetCombo = new Combo(page, SWT.NONE);
		charsetCombo.setItems(Charset.availableCharsets().keySet()
				.toArray(new String[Charset.availableCharsets().size()]));
		charsetCombo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				update();
			}

		});
		charsetCombo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				update();
			}
		});

		update();
	}

	/**
	 * Update the page state.
	 */
	private void update() {
		String name = charsetCombo.getText();
		if (name != null && !name.isEmpty()) {
			try {
				Charset cs = Charset.forName(name);
				setMessage(cs.displayName(), INFORMATION);
				setPageComplete(true);
				return;
			} catch (UnsupportedCharsetException e) {
				setMessage("Charset not supported", ERROR);
			} catch (IllegalCharsetNameException e) {
				setMessage("Illegal charset name", ERROR);
			}
		}
		else {
			setMessage("Please specify a character encoding", INFORMATION);
		}

		setPageComplete(false);
	}
}
