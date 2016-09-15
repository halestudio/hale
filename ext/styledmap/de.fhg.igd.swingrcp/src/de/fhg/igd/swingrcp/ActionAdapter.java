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
package de.fhg.igd.swingrcp;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;

/**
 * SWT ActionAdapter for Swing Actions
 *
 * @author Simon Templer
 */
public class ActionAdapter extends Action implements PropertyChangeListener {

	private static final Log log = LogFactory.getLog(ActionAdapter.class);

	private final javax.swing.Action action;

	/**
	 * The display
	 */
	protected final Display display;

	/**
	 * Creates an ActionAdapter
	 * 
	 * @param action the internal swing action
	 */
	public ActionAdapter(final javax.swing.Action action) {
		this(action, Action.AS_PUSH_BUTTON);
	}

	/**
	 * Creates an ActionAdapter
	 * 
	 * @param action the internal swing action
	 * @param style the action style
	 */
	public ActionAdapter(final javax.swing.Action action, int style) {
		super(null, style);

		if (action == null)
			throw new IllegalArgumentException();

		this.action = action;

		this.display = Display.getCurrent();
		if (this.display == null)
			throw new IllegalArgumentException("ActionAdapter has to be created in display thread");

		action.addPropertyChangeListener(this);

		loadImage();
	}

	/**
	 * Set the actions icon as {@link ImageDescriptor} if possible
	 */
	private void loadImage() {
		Object icon = action.getValue(javax.swing.Action.SMALL_ICON);

		if (icon instanceof ImageIcon) {
			try {
				setImageDescriptor(ImageDescriptor
						.createFromImageData(SwingRCPUtilities.convertToSWT((ImageIcon) icon)));
			} catch (Exception e) {
				log.warn("Error converting action icon", e);
			}
		}
	}

	/**
	 * @see Action#getDescription()
	 */
	@Override
	public String getDescription() {
		return (String) action.getValue(javax.swing.Action.LONG_DESCRIPTION);
	}

	/**
	 * @see Action#getText()
	 */
	@Override
	public String getText() {
		Object text = action.getValue(javax.swing.Action.NAME);
		if (text == null)
			return null;
		else
			return text.toString();
	}

	/**
	 * @see Action#getToolTipText()
	 */
	@Override
	public String getToolTipText() {
		return (String) action.getValue(javax.swing.Action.SHORT_DESCRIPTION);
	}

	/**
	 * @see Action#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return action.isEnabled();
	}

	/**
	 * @see Action#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(final boolean enabled) {
		final boolean old = isEnabled();

		action.setEnabled(enabled);

		display.asyncExec(new Runnable() {

			@Override
			public void run() {
				firePropertyChange("enabled", old, enabled);
			}

		});
	}

	/**
	 * @see Action#setText(java.lang.String)
	 */
	@Override
	public void setText(final String text) {
		if (action != null) {
			final String old = getText();

			action.putValue(javax.swing.Action.NAME, text);

			display.asyncExec(new Runnable() {

				@Override
				public void run() {
					firePropertyChange("text", old, text);
				}

			});
		}
	}

	/**
	 * @see Action#setToolTipText(java.lang.String)
	 */
	@Override
	public void setToolTipText(final String toolTipText) {
		final String old = getToolTipText();

		action.putValue(javax.swing.Action.SHORT_DESCRIPTION, toolTipText);

		display.asyncExec(new Runnable() {

			@Override
			public void run() {
				firePropertyChange(Action.TOOL_TIP_TEXT, old, toolTipText);
			}

		});
	}

	/**
	 * @see Action#run()
	 */
	@Override
	public void run() {
		// execute action
		SwingUtilities.invokeLater(new Runnable() {

			/**
			 * @see Runnable#run()
			 */
			@Override
			public void run() {
				action.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
			}

		});
	}

	/**
	 * @see PropertyChangeListener#propertyChange(PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(final PropertyChangeEvent evt) {
		// propagate property change event
		// -> enabled
		if (evt.getPropertyName().equals("enabled"))
			display.asyncExec(new Runnable() {

				@Override
				public void run() {
					firePropertyChange(Action.ENABLED, evt.getOldValue(), evt.getNewValue());
				}

			});
	}

}
