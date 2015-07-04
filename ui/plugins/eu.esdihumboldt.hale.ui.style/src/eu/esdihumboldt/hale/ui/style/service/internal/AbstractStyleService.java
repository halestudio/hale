/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.style.service.internal;

import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.swt.graphics.RGB;

import eu.esdihumboldt.hale.ui.common.service.style.StyleService;
import eu.esdihumboldt.hale.ui.common.service.style.StyleServiceListener;

/**
 * Base {@link StyleService} implementation
 * 
 * @author Simon Templer
 */
public abstract class AbstractStyleService implements StyleService {

	private final CopyOnWriteArraySet<StyleServiceListener> listeners = new CopyOnWriteArraySet<StyleServiceListener>();

	/**
	 * @see StyleService#addListener(StyleServiceListener)
	 */
	@Override
	public void addListener(StyleServiceListener listener) {
		listeners.add(listener);
	}

	/**
	 * @see StyleService#removeListener(StyleServiceListener)
	 */
	@Override
	public void removeListener(StyleServiceListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Notify listeners that styles have been added
	 */
	protected void notifyStylesAdded() {
		for (StyleServiceListener listener : listeners) {
			listener.stylesAdded(this);
		}
	}

	/**
	 * Notify listeners that styles have been removed
	 */
	protected void notifyStylesRemoved() {
		for (StyleServiceListener listener : listeners) {
			listener.stylesRemoved(this);
		}
	}

	/**
	 * Notify listeners that the settings have been changed
	 */
	protected void notifySettingsChanged() {
		for (StyleServiceListener listener : listeners) {
			listener.styleSettingsChanged(this);
		}
	}

	/**
	 * Notify listeners that the settings have been changed
	 * 
	 * @param background the new background
	 */
	protected void notifyBackgroundChanged(RGB background) {
		for (StyleServiceListener listener : listeners) {
			listener.backgroundChanged(this, background);
		}
	}

}
