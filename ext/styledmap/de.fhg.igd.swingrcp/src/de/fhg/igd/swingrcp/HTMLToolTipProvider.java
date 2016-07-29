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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * HTMLTooltipProvider
 *
 * @author Simon Templer
 */
public class HTMLToolTipProvider {

	/**
	 * ToolTipManager
	 *
	 * @author <a href="mailto:simon.templer@igd.fhg.de">Simon Templer</a>
	 */
	public class ToolTipManager implements MouseMoveListener, MouseTrackListener {

		private static final int HOVER_DELAY = 400;

		private final ToolBar toolBar;

		private Timer hoverTimer = null;

		private Shell toolShell;

		private ToolItem currentToolItem = null;

		/**
		 * Creates a tool tip manager for a tool bar
		 * 
		 * @param toolBar the tool bar
		 */
		public ToolTipManager(final ToolBar toolBar) {
			this.toolBar = toolBar;

			toolBar.addMouseMoveListener(this);
			toolBar.addMouseTrackListener(this);
		}

		/**
		 * @see MouseMoveListener#mouseMove(MouseEvent)
		 */
		@Override
		public void mouseMove(final MouseEvent e) {
			final ToolItem item = toolBar.getItem(new Point(e.x, e.y));

			// cancel old task
			if (hoverTimer != null) {
				hoverTimer.cancel();
				hoverTimer.purge();
			}

			if (currentToolItem != item) {
				hideToolTip();
			}

			if (currentToolItem == null && item != null && tooltips.containsKey(item)) {
				// start new one
				hoverTimer = new Timer(true);
				hoverTimer.schedule(new TimerTask() {

					/**
					 * @see TimerTask#run()
					 */
					@Override
					public void run() {
						if (!toolBar.isDisposed()) {
							toolBar.getDisplay().asyncExec(new Runnable() {

								@Override
								public void run() {
									// test if mouse was moved
									Point evt = new Point(e.x, e.y);
									Point pos = toolBar.getDisplay().getCursorLocation();
									Point pt = toolBar.toControl(pos);
									// Rectangle bounds = toolBar.getBounds();
									if (evt.equals(pt)) {
										showToolTip(e, tooltips.get(item), item);
									}
								}

							});
						}
					}

				}, HOVER_DELAY);
			}
		}

		/**
		 * @see MouseTrackListener#mouseEnter(MouseEvent)
		 */
		@Override
		public void mouseEnter(MouseEvent e) {
			mouseMove(e);
		}

		/**
		 * @see MouseTrackListener#mouseExit(MouseEvent)
		 */
		@Override
		public void mouseExit(MouseEvent e) {
			if (hoverTimer != null) {
				hoverTimer.cancel();
				hoverTimer.purge();
				// XXX on Windows this occurs when showing the tooltip -
				// hideToolTip();
			}
		}

		/**
		 * @see MouseTrackListener#mouseHover(MouseEvent)
		 */
		@Override
		public void mouseHover(MouseEvent e) {
			// seems to occur never
			// log.warn("Mouse hover occurred!");
		}

		/**
		 * Show the tool tip
		 * 
		 * @param e the mouse event
		 * @param toolTip the tool tip string
		 * @param item the tool item
		 */
		protected void showToolTip(MouseEvent e, String toolTip, final ToolItem item) {
			// set as current item
			currentToolItem = item;

			toolShell = new Shell(toolBar.getShell(), SWT.ON_TOP | SWT.NO_FOCUS | SWT.TOOL);
			FillLayout layout = new FillLayout();
			toolShell.setLayout(layout);
			try {
				Browser browser = new Browser(toolShell, SWT.NONE);
				browser.setFont(toolBar.getDisplay().getSystemFont());
				browser.setForeground(
						toolBar.getDisplay().getSystemColor(SWT.COLOR_INFO_FOREGROUND));
				browser.setBackground(
						toolBar.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
				browser.setText(toolTip);
				// Point size = toolShell.computeSize( SWT.DEFAULT,
				// SWT.DEFAULT);
				int tbh = toolBar.getBounds().height - 1; // use toolbar height
															// to show tooltip
															// below the bar
				int tix = item.getBounds().x + 1;
				Point pt = toolBar.toDisplay(tix, tbh); // e.x, e.y);
				final int topBuffer = tbh - e.y + 1;

				Rectangle bounds = toolBar.getDisplay().getBounds();

				int x = (pt.x + toolTipWidth > bounds.x + bounds.width)
						? (bounds.x + bounds.width - toolTipWidth) : (pt.x);

				toolShell.setBounds(x, pt.y, toolTipWidth, toolTipHeight);
				// toolShell.setBounds(pt.x, pt.y, size.x, size.y);

				toolShell.addMouseTrackListener(new MouseTrackAdapter() {

					@Override
					public void mouseExit(MouseEvent e) {
						hideToolTip();
					}

				});

				final Timer closeTimer = new Timer(true);
				closeTimer.scheduleAtFixedRate(new TimerTask() {

					@Override
					public void run() {
						if (toolShell != null && !toolShell.isDisposed()) {
							toolShell.getDisplay().asyncExec(new Runnable() {

								@Override
								public void run() {
									if (item == currentToolItem && toolShell != null
											&& !toolShell.isDisposed()) {
										// check if cursor is over tooltip
										Point cursor = toolShell.getDisplay().getCursorLocation();
										Rectangle bounds = toolShell.getBounds();
										// create top buffer
										bounds = new Rectangle(bounds.x, bounds.y - topBuffer,
												bounds.width, bounds.height);
										if (!bounds.contains(cursor)) {
											hideToolTip();
											closeTimer.cancel();
										}
									}
								}

							});
						}
						else {
							// disposed -> cancel timer
							closeTimer.cancel();
						}
					}

				}, 2 * HOVER_DELAY, 1000);

				toolShell.setVisible(true);
			} catch (SWTError err) {
				log.error(err.getMessage(), err);
			}
		}

		/**
		 * Hide the tool tip
		 */
		protected void hideToolTip() {
			currentToolItem = null;

			if (toolShell != null) {
				toolShell.close();
				toolShell.dispose();
				toolShell = null;
			}
		}

	}

	/**
	 * Custom action item
	 */
	public class CustomActionItem extends ContributionItem {

		private final IAction action;

		private CustomActionItem(final IAction action) {
			this.action = action;
		}

		/**
		 * @see ContributionItem#fill(ToolBar, int)
		 */
		@Override
		public void fill(ToolBar parent, int index) {
			// register toolbar
			addToolBar(parent);

			// determine ToolItem style
			int style;
			switch (action.getStyle()) {
			case IAction.AS_RADIO_BUTTON:
				style = SWT.RADIO;
				break;
			case IAction.AS_CHECK_BOX:
				style = SWT.CHECK;
				break;
			case IAction.AS_DROP_DOWN_MENU:
				style = SWT.DROP_DOWN;
				break;
			case IAction.AS_PUSH_BUTTON:
			default:
				style = SWT.PUSH;
			}

			// create ToolItem
			final ToolItem item = new ToolItem(parent, style, index);

			// determine ToolItem properties
			Image image = null;
			if (action.getImageDescriptor() != null)
				image = action.getImageDescriptor().createImage();
			if (image != null)
				item.setImage(image);
			else
				item.setText(action.getText());

			item.setSelection(action.isChecked());
			item.setEnabled(action.isEnabled());

			item.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					action.run();
				}

				@Override
				public void widgetSelected(SelectionEvent e) {
					action.run();
				}

			});

			// add property change listeners
			action.addPropertyChangeListener(new IPropertyChangeListener() {

				@Override
				public void propertyChange(PropertyChangeEvent pce) {
					// text
					if (pce.getProperty().equals(IAction.TEXT))
						item.setText((String) pce.getNewValue());
					// enabled
					else if (pce.getProperty().equals(IAction.ENABLED))
						item.setEnabled((Boolean) pce.getNewValue());
					// image
					else if (pce.getProperty().equals(IAction.IMAGE)) {
						if (pce.getNewValue() != null) {
							Image image = ((ImageDescriptor) pce.getNewValue()).createImage();
							item.setImage(image);
						}
						else
							item.setImage(null);
					}
				}

			});

			// add to factories map
			tooltips.put(item, action.getToolTipText());
		}

	}

	private static final Log log = LogFactory.getLog(HTMLToolTipProvider.class);

	private final Map<ToolItem, String> tooltips = new HashMap<ToolItem, String>();

	private final Set<ToolBar> toolBars = new HashSet<ToolBar>();

	private int toolTipWidth = 240;

	private int toolTipHeight = 100;

	/**
	 * Create a custom action item for the given action
	 * 
	 * @param action the action
	 * 
	 * @return the custom action item
	 */
	public CustomActionItem createItem(final IAction action) {
		return new CustomActionItem(action);
	}

	private void addToolBar(ToolBar bar) {
		if (!toolBars.contains(bar)) {
			toolBars.add(bar);

			new ToolTipManager(bar);
		}
	}

	/**
	 * @return the toolTipWidth
	 */
	public int getToolTipWidth() {
		return toolTipWidth;
	}

	/**
	 * @param toolTipWidth the toolTipWidth to set
	 */
	public void setToolTipWidth(int toolTipWidth) {
		this.toolTipWidth = toolTipWidth;
	}

	/**
	 * @return the toolTipHeight
	 */
	public int getToolTipHeight() {
		return toolTipHeight;
	}

	/**
	 * @param toolTipHeight the toolTipHeight to set
	 */
	public void setToolTipHeight(int toolTipHeight) {
		this.toolTipHeight = toolTipHeight;
	}

}
