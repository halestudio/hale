package eu.esdihumboldt.hale.io.gml.ui.wfs.wizard;

import java.net.URL;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import eu.esdihumboldt.hale.io.gml.ui.wfs.wizard.capabilities.GetCapabilititiesRetriever;

/*+-------------+----------------------------------------------------------*
 *|  |  |_|_|_|_|   Fraunhofer-Institut fuer Graphische Datenverarbeitung  *
 *|__|__|_|_|_|_|     (Fraunhofer Institute for Computer Graphics)         *
 *|  |  |_|_|_|_|                                                          *
 *|__|__|_|_|_|_|                                                          *
 *|  __ |    ___|                                                          *
 *| /_  /_  / _ |     Fraunhoferstrasse 5                                  *
 *|/   / / /__/ |     D-64283 Darmstadt, Germany                           *
 *+-------------+----------------------------------------------------------*/

/**
 * WFS location field editor
 * 
 * @author Simon Templer
 */
public class WfsLocationFieldEditor extends FieldEditor {

	/**
	 * Delay for validating on modification in milliseconds
	 */
	private static final int MODIFY_VALIDATE_DELAY = 1200;

	private Combo combo;

	private boolean valid;

	private Timer timer;

	/**
	 * @see FieldEditor#FieldEditor(String, String, Composite)
	 */
	public WfsLocationFieldEditor(String name, String labelText, Composite parent) {
		super(name, labelText, parent);
	}

	/**
	 * @see FieldEditor#adjustForNumColumns(int)
	 */
	@Override
	protected void adjustForNumColumns(int numColumns) {
		GridData gd = (GridData) combo.getLayoutData();
		gd.horizontalSpan = numColumns - 1;
		// We only grab excess space if we have to
		// If another field editor has more columns then
		// we assume it is setting the width.
		gd.grabExcessHorizontalSpace = gd.horizontalSpan == 1;
	}

	/**
	 * @see FieldEditor#doFillIntoGrid(Composite, int)
	 */
	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		getLabelControl(parent);

		combo = new Combo(parent, SWT.BORDER | SWT.DROP_DOWN);
		GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, numColumns - 1, 1);
		combo.setLayoutData(layoutData);

		loadRecent();

		refreshValidState();

		combo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				scheduleValidation(); // refreshValidState();
			}
		});
	}

	/**
	 * Schedule the validation
	 */
	protected void scheduleValidation() {
		final Display display = Display.getCurrent();

		synchronized (this) {
			if (timer != null) {
				timer.cancel();
			}

			// invalidate
			invalidate();

			// schedule validation
			timer = new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					display.syncExec(new Runnable() {

						@Override
						public void run() {
							BusyIndicator.showWhile(display, new Runnable() {

								@Override
								public void run() {
									refreshValidState();
								}
							});
						}
					});
				}
			}, MODIFY_VALIDATE_DELAY);
		}
	}

	/**
	 * Load the recently used WFSes
	 */
	protected void loadRecent() {
		List<String> recent = WfsPreferences.getRecent();
		combo.setItems(recent.toArray(new String[recent.size()]));
		if (!recent.isEmpty() && (combo.getText() == null || combo.getText().isEmpty())) {
			combo.setText(recent.get(0));
		}
	}

	/**
	 * Update the recently used WFSes
	 */
	public void updateRecent() {
		String value = combo.getText();
		if (value != null && !value.isEmpty()) {
			List<String> recent = WfsPreferences.getRecent();
			recent.remove(value);
			recent.add(0, value);
			WfsPreferences.setRecent(recent);
		}
	}

	/**
	 * @see FieldEditor#doLoad()
	 */
	@Override
	protected void doLoad() {
		String value = getPreferenceStore().getString(getPreferenceName());
		combo.setText((value == null) ? ("") : (value)); //$NON-NLS-1$
	}

	/**
	 * @see FieldEditor#doLoadDefault()
	 */
	@Override
	protected void doLoadDefault() {
		String value = getPreferenceStore().getDefaultString(getPreferenceName());
		combo.setText((value == null) ? ("") : (value)); //$NON-NLS-1$
	}

	/**
	 * @see FieldEditor#doStore()
	 */
	@Override
	protected void doStore() {
		getPreferenceStore().setValue(getPreferenceName(), combo.getText());
	}

	/**
	 * @see FieldEditor#getNumberOfControls()
	 */
	@Override
	public int getNumberOfControls() {
		return 2;
	}

	/**
	 * Get the value
	 * 
	 * @return the value
	 */
	public String getValue() {
		return combo.getText();
	}

	/**
	 * Set the value
	 * 
	 * @param value the value to set
	 */
	public void setValue(String value) {
		combo.setText((value == null) ? ("") : (value)); //$NON-NLS-1$

		refreshValidState();
	}

	/**
	 * @see FieldEditor#isValid()
	 */
	@Override
	public boolean isValid() {
		return valid;
	}

	/**
	 * @see FieldEditor#refreshValidState()
	 */
	@Override
	protected void refreshValidState() {
		boolean newValid = getValidState();

		if (valid != newValid) {
			valid = newValid;

			fireStateChanged(IS_VALID, !valid, valid);
		}
	}

	/**
	 * Invalidate the editor
	 */
	protected void invalidate() {
		if (valid) {
			valid = false;
			fireStateChanged(IS_VALID, !valid, valid);
		}
	}

	private boolean getValidState() {
		String value = getValue();

		DialogPage page = getPage();

		// try to create a URL
		try {
			new URL(value);
		} catch (Throwable e) {
			if (page != null) {
				page.setErrorMessage(e.getLocalizedMessage());
			}
			return false;
		}

		// try to get capabilities?
		try {
			GetCapabilititiesRetriever.getDataStore(value);
		} catch (Throwable e) {
			if (page != null) {
				page.setErrorMessage(e.getLocalizedMessage());
			}
			return false;
		}

		// passed all tests
		if (page != null) {
			page.setErrorMessage(null);
		}
		return true;
	}

}
