/*
 * Copyright (c) 2016 Fraunhofer IGD
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
 *     Fraunhofer IGD <http://www.igd.fraunhofer.de/>
 */
package de.fhg.igd.mapviewer.server.wms.wizard.pages;

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Display;

import de.fhg.igd.mapviewer.concurrency.SwtCallback;
import de.fhg.igd.mapviewer.server.wms.capabilities.WMSUtil;
import de.fhg.igd.mapviewer.server.wms.wizard.pages.ConcurrentValidator.Validation;

/**
 * WMS location field editor
 * 
 * @author Simon Templer
 */
public class WMSLocationFieldEditor extends StringFieldEditor {

	private final ConcurrentValidator validator;

	private boolean doValidation = true;

	/**
	 * Default constructor
	 * 
	 * @param display the display
	 */
	public WMSLocationFieldEditor(Display display) {
		super();

		setValidateStrategy(StringFieldEditor.VALIDATE_ON_KEY_STROKE);

		validator = new ConcurrentValidator(new SwtCallback<Boolean>(display) {

			@Override
			protected void error(Throwable e) {
				setErrorMessage(e.getLocalizedMessage());
				updateState();
			}

			@Override
			protected void finished(Boolean result) {
				updateState();
			}
		}, false);
	}

	/**
	 * @see StringFieldEditor#doCheckState()
	 */
	@Override
	protected boolean doCheckState() {
		final String value = getStringValue();

		if (doValidation) {
			validator.runValidation(new Validation() {

				@Override
				public boolean validate() throws Exception {
					WMSUtil.getCapabilities(value);
					return true;
				}
			});
		}

		return validator.isValid();
	}

	private void updateState() {
		boolean oldState = isValid();

		doValidation = false;
		try {
			refreshValidState();
		} catch (Exception e) {
			// ignore
		}
		doValidation = true;

		boolean newState = isValid();
		if (oldState != newState) {
			fireStateChanged(IS_VALID, oldState, newState);
		}
	}

}
