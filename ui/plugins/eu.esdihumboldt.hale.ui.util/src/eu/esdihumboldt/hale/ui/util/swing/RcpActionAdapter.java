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
package eu.esdihumboldt.hale.ui.util.swing;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;

/**
 * This class provides a SWT Adapter for Swing Actions.
 * 
 * @author Simon Templer, Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class RcpActionAdapter extends Action implements PropertyChangeListener {

	private static final ALogger _log = ALoggerFactory.getLogger(RcpActionAdapter.class);

	private final javax.swing.Action action;

	/**
	 * The display
	 */
	protected final Display display;

	/**
	 * Creates an ActionAdapter
	 * 
	 * @param action the Swing action to wrap
	 */
	public RcpActionAdapter(final javax.swing.Action action) {
		this(action, Action.AS_PUSH_BUTTON);
	}

	/**
	 * Creates an ActionAdapter
	 * 
	 * @param action the Swing action to wrap
	 * @param style the JFace action style
	 * 
	 * @see Action#Action(String, int)
	 */
	public RcpActionAdapter(final javax.swing.Action action, int style) {
		super(null, style);

		if (action == null)
			throw new IllegalArgumentException();

		this.action = action;

		this.display = Display.getCurrent();
		if (this.display == null)
			throw new IllegalArgumentException("ActionAdapter has to be created in display thread"); //$NON-NLS-1$

		action.addPropertyChangeListener(this);

		loadImage();
	}

	/**
	 * Set the actions icon as {@link ImageDescriptor} if possible
	 */
	@SuppressWarnings("deprecation")
	private void loadImage() {
		Object icon = action.getValue(javax.swing.Action.SMALL_ICON);

		if (icon instanceof ImageIcon) {
			try {
				setImageDescriptor(ImageDescriptor
						.createFromImageData(SwingRcpUtilities.convertToSWT((ImageIcon) icon)));
			} catch (Exception e) {
				_log.warn("Error converting action icon", e); //$NON-NLS-1$
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#getDescription()
	 */
	@Override
	public String getDescription() {
		return (String) action.getValue(javax.swing.Action.LONG_DESCRIPTION);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#getText()
	 */
	@Override
	public String getText() {
		Object text = action.getValue(javax.swing.Action.NAME);
		if (text == null)
			return null;
		else
			return text.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#getToolTipText()
	 */
	@Override
	public String getToolTipText() {
		return (String) action.getValue(javax.swing.Action.SHORT_DESCRIPTION);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return action.isEnabled();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(final boolean enabled) {
		final boolean old = isEnabled();

		action.setEnabled(enabled);

		display.asyncExec(new Runnable() {

			@Override
			public void run() {
				firePropertyChange("enabled", old, enabled); //$NON-NLS-1$
			}

		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#setText(java.lang.String)
	 */
	@Override
	public void setText(final String text) {
		if (action != null) {
			final String old = getText();

			action.putValue(javax.swing.Action.NAME, text);

			display.asyncExec(new Runnable() {

				@Override
				public void run() {
					firePropertyChange("text", old, text); //$NON-NLS-1$
				}

			});
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#setToolTipText(java.lang.String)
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		// execute action
		SwingUtilities.invokeLater(new Runnable() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run() {
				action.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
			}

		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.
	 * PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(final PropertyChangeEvent evt) {
		// propagate property change event
		// -> enabled
		if (evt.getPropertyName().equals("enabled")) //$NON-NLS-1$
			display.asyncExec(new Runnable() {

				@Override
				public void run() {
					firePropertyChange(Action.ENABLED, evt.getOldValue(), evt.getNewValue());
				}

			});
	}

}
