/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.ui.style.service.internal;

import org.eclipse.swt.graphics.RGB;

import de.cs3d.util.eclipse.TypeSafeListenerList;
import eu.esdihumboldt.hale.ui.style.service.StyleService;
import eu.esdihumboldt.hale.ui.style.service.StyleServiceListener;

/**
 * Base {@link StyleService} implementation
 * 
 * @author Simon Templer
 */
public abstract class AbstractStyleService implements StyleService {

	private TypeSafeListenerList<StyleServiceListener> listeners = new TypeSafeListenerList<StyleServiceListener>();

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
