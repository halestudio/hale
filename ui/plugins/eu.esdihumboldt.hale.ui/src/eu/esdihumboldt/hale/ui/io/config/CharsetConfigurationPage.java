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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.text.MessageFormat;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.ui.io.IOWizard;

/**
 * Configuration page for the character encoding.
 * 
 * @param <W> the concrete I/O wizard type
 * @param <P> the {@link IOProvider} type used in the wizard
 * 
 * @author Simon Templer
 */
public class CharsetConfigurationPage<P extends IOProvider, W extends IOWizard<P>>
		extends AbstractConfigurationPage<P, W> {

	/**
	 * Configuration pages modes.
	 */
	public enum Mode {
		/**
		 * User must specify character set manually.
		 */
		manual,
		/**
		 * User can trigger detection of encoding on the stream through a
		 * button. Only valid for import providers.
		 */
		manualAllowDetect,
		/**
		 * Detection of encoding is triggered automatically. Only valid for
		 * import providers.
		 */
		autoDetect
	}

	private static final ALogger log = ALoggerFactory.getLogger(CharsetConfigurationPage.class);

	private Combo charsetCombo;

	private ControlDecoration charsetComboDecoration;

	private Button detectButton;

	private final Mode mode;

	/**
	 * Default constructor.
	 */
	public CharsetConfigurationPage() {
		this(Mode.manual);
	}

	/**
	 * Create a character set configuration page with a specific mode.
	 * 
	 * @param mode the mode
	 */
	protected CharsetConfigurationPage(Mode mode) {
		super("charset");

		this.mode = mode;

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
				setCharset(cs.name());
				update();
			}

			if (detectButton != null) {
				detectButton.setEnabled(pro instanceof ImportProvider);
			}
		}
	}

	@Override
	public void disable() {
		// nothing to do
	}

	@Override
	protected void onShowPage(boolean firstShow) {
		super.onShowPage(firstShow);

		if (firstShow && mode == Mode.autoDetect) {
			// try to auto detect encoding
			ImportProvider pro = (ImportProvider) getWizard().getProvider();
			if (pro.getSource() != null) {
				try {
					detectCharset(pro.getSource());
				} catch (IOException e) {
					log.error("Character encoding detection failed.", e);
				}
			}
		}
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
		boolean showDetectButton = mode == Mode.manualAllowDetect || mode == Mode.autoDetect;

		GridLayoutFactory.swtDefaults().numColumns((showDetectButton) ? (3) : (2)).applyTo(page);

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

		if (showDetectButton) {
			detectButton = new Button(page, SWT.NONE);
			detectButton.setText("Detect");
			detectButton.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					ImportProvider pro = (ImportProvider) getWizard().getProvider();
					if (pro.getSource() != null) {
						try {
							detectCharset(pro.getSource());
						} catch (IOException e1) {
							log.userError("Character encoding detection failed.", e1);
						}
					}
					else {
						log.userError("Source on import provider not set, cannot detect encoding.");
					}
				}
			});
		}

		update();
	}

	/**
	 * Try to detect the character encoding.
	 * 
	 * @param source the source
	 * @throws IOException if the resource cannot be read
	 */
	protected void detectCharset(LocatableInputSupplier<? extends InputStream> source)
			throws IOException {
		InputStream input = source.getInput();

		CharsetDetector cd = new CharsetDetector();
		cd.setText(input);
		CharsetMatch cm = cd.detect();

		if (cm != null) {
			charsetCombo.setText(cm.getName());
			update();
			setMessage(MessageFormat.format("Character encoding {0} detected with {1}% confidence.",
					cm.getName(), cm.getConfidence()), INFORMATION);
		}
		else {
			setMessage("Character encoding detection yielded no result.", WARNING);
		}
	}

	/**
	 * Set the value of the character set combobox
	 * 
	 * @param charset value to set
	 */
	protected final void setCharset(String charset) {
		charsetCombo.setText(charset);
	}

	/**
	 * Add a control decoration to the character set combobox.
	 * 
	 * @param decorationText Tooltip text of the decoration
	 * @param image The decoration image, e.g.<br>
	 *            <code>FieldDecorationRegistry.getDefault()
								.getFieldDecoration(FieldDecorationRegistry.DEC_CONTENT_PROPOSAL)
								.getImage()</code><br>
	 * @param margin The margin in pixels
	 */
	protected final void setCharsetDecoration(String decorationText, Image image, int margin) {
		if (charsetComboDecoration == null) {
			charsetComboDecoration = new ControlDecoration(charsetCombo, SWT.TOP | SWT.LEFT);
		}
		charsetComboDecoration.setDescriptionText(decorationText);
		charsetComboDecoration.setImage(image);
		charsetComboDecoration.setMarginWidth(margin);
	}

	/**
	 * The success message to be displayed when a valid character set name was
	 * entered
	 * 
	 * @param cs The selected character set
	 * @return The message
	 */
	protected String successMessage(Charset cs) {
		return MessageFormat.format("Selected charset: {0}", cs.displayName());
	}

	/**
	 * Update the page state.
	 */
	private void update() {
		String name = charsetCombo.getText();
		if (name != null && !name.isEmpty()) {
			try {
				Charset cs = Charset.forName(name);
				setMessage(successMessage(cs), INFORMATION);
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
