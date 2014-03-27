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

package eu.esdihumboldt.hale.ui.util.tip;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;

/**
 * Browser tip
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class BrowserTip {

	private static final ALogger log = ALoggerFactory.getLogger(BrowserTip.class);

	private static final int HOVER_DELAY = 400;

	private final int toolTipWidth;

	private final int toolTipHeight;

	private final boolean plainText;

	private final ScheduledExecutorService scheduleService;

	/**
	 * The height adjustment when using the computed size
	 */
	private int heightAdjustment = 50;

	/**
	 * Create a browser tip using its own {@link ScheduledExecutorService}
	 * 
	 * @param toolTipWidth the maximum with
	 * @param toolTipHeight the maximum height
	 * @param plainText if the content will be plain text instead of HTML
	 */
	public BrowserTip(int toolTipWidth, int toolTipHeight, boolean plainText) {
		this(toolTipWidth, toolTipHeight, plainText, null);
	}

	/**
	 * Create a browser tip with using given {@link ScheduledExecutorService}
	 * 
	 * @param toolTipWidth the maximum with
	 * @param toolTipHeight the maximum height
	 * @param plainText if the content will be plain text instead of HTML
	 * @param scheduleService the scheduled executor service to use, if
	 *            <code>null</code> a service will be created
	 */
	public BrowserTip(int toolTipWidth, int toolTipHeight, boolean plainText,
			ScheduledExecutorService scheduleService) {
		super();
		this.toolTipWidth = toolTipWidth;
		this.toolTipHeight = toolTipHeight;
		this.plainText = plainText;

		if (scheduleService != null) {
			this.scheduleService = scheduleService;
		}
		else {
			this.scheduleService = Executors.newScheduledThreadPool(1);
		}
	}

	/**
	 * Show the tool tip
	 * 
	 * @param control the tip control
	 * @param posx the x-position
	 * @param posy the y-position
	 * @param toolTip the tool tip string
	 * 
	 * @return the tool shell
	 */
	public Shell showToolTip(Control control, int posx, int posy, String toolTip) {
		return showToolTip(control, posx, posy, toolTip, null, null);
	}

	/**
	 * Show the tool tip
	 * 
	 * @param control the tip control
	 * @param posx the x-position
	 * @param posy the y-position
	 * @param toolTip the tool tip string
	 * @param addBounds additional bounds that will be treated as if in the
	 *            tooltip (the tooltip won't hide if the cursor is inside these
	 *            bounds), may be <code>null</code>
	 * @param addBoundsControl the control the addBounds coordinates are
	 *            relative to, <code>null</code> if addBounds is in display
	 *            coordinates or no addBounds is provided
	 * 
	 * @return the tool shell
	 */
	public Shell showToolTip(Control control, int posx, int posy, String toolTip,
			final Rectangle addBounds, final Control addBoundsControl) {
		final Shell toolShell = new Shell(control.getShell(), SWT.ON_TOP | SWT.NO_FOCUS | SWT.TOOL);
		FillLayout layout = new FillLayout();
		toolShell.setLayout(layout);
		try {
			if (plainText) {
				Text text = new Text(toolShell, SWT.MULTI | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
				text.setFont(control.getDisplay().getSystemFont());
				text.setForeground(control.getDisplay().getSystemColor(SWT.COLOR_INFO_FOREGROUND));
				text.setBackground(control.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
				text.setText(toolTip);
			}
			else {
				Browser browser = new Browser(toolShell, SWT.NONE);
				browser.setFont(control.getDisplay().getSystemFont());
				browser.setForeground(control.getDisplay()
						.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
				browser.setBackground(control.getDisplay()
						.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
				browser.setText(toolTip);
			}

			Point pt = control.toDisplay(posx, posy);

			Rectangle bounds = control.getDisplay().getBounds();

			Point size = toolShell.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			int width = Math.min(toolTipWidth, size.x);
			/*
			 * On Windows XP (not on 7) computeSize seems to result in a size
			 * where the whole text is pressed into one line. We try to fix this
			 * by using the... "widthFactor"! (only for small computed heights)
			 */
			int widthFactor = (size.y < 30) ? (size.x / width) : (1);
			int height = Math.min(toolTipHeight, size.y * widthFactor + heightAdjustment);

			int x = (pt.x + width > bounds.x + bounds.width) ? (bounds.x + bounds.width - width)
					: (pt.x);
			int y = (pt.y + height > bounds.y + bounds.height) ? (bounds.y + bounds.height - height)
					: (pt.y);

			toolShell.setBounds(x, y, width, height);

			final Point initCursor = toolShell.getDisplay().getCursorLocation();

			toolShell.addMouseTrackListener(new MouseTrackAdapter() {

				@Override
				public void mouseExit(MouseEvent e) {
					hideToolTip(toolShell);
				}

			});

			final AtomicReference<ScheduledFuture<?>> closeTimerRef = new AtomicReference<ScheduledFuture<?>>();
			ScheduledFuture<?> closeTimer = scheduleService.scheduleAtFixedRate(new Runnable() {

				@Override
				public void run() {
					if (!toolShell.isDisposed()) {
						toolShell.getDisplay().asyncExec(new Runnable() {

							@Override
							public void run() {
								// check if cursor is over tooltip
								if (!toolShell.isDisposed()) {
									Point cursor = toolShell.getDisplay().getCursorLocation();
									if (!cursor.equals(initCursor)) {
										Rectangle bounds = toolShell.getBounds();
										if (addBounds != null) {
											Rectangle add;
											if (addBoundsControl != null) {
												Point addP = addBoundsControl.toDisplay(
														addBounds.x, addBounds.y);
												add = new Rectangle(addP.x, addP.y,
														addBounds.width, addBounds.height);
											}
											else {
												add = addBounds;
											}
											bounds = bounds.union(add);
										}

										if (!bounds.contains(cursor)) {
											hideToolTip(toolShell);
											ScheduledFuture<?> closeTimer = closeTimerRef.get();
											if (closeTimer != null)
												closeTimer.cancel(true);
										}
									}
								}
								else {
									ScheduledFuture<?> closeTimer = closeTimerRef.get();
									if (closeTimer != null)
										closeTimer.cancel(true);
								}
							}

						});
					}
					else {
						// disposed -> cancel timer
						ScheduledFuture<?> closeTimer = closeTimerRef.get();
						if (closeTimer != null)
							closeTimer.cancel(true);
					}
				}
			}, 2 * HOVER_DELAY, 1000, TimeUnit.MILLISECONDS);
			closeTimerRef.set(closeTimer);

			toolShell.setVisible(true);
			toolShell.setFocus();

			return toolShell;
		} catch (SWTError err) {
			log.error(err.getMessage(), err);

			return null;
		}
	}

	/**
	 * Hide the tool tip
	 * 
	 * @param shell the tip shell
	 */
	public static void hideToolTip(Shell shell) {
		if (shell != null && !shell.isDisposed()) {
			shell.close();
			shell.dispose();
		}
	}

	/**
	 * Determines if the tool tip is visible
	 * 
	 * @param shell the tip shell
	 * 
	 * @return if the tool tip is visible
	 */
	public static boolean toolTipVisible(Shell shell) {
		if (shell != null && !shell.isDisposed()) {
			return true;
		}
		return false;
	}

	/**
	 * @return the heightAdjustment
	 */
	public int getHeightAdjustment() {
		return heightAdjustment;
	}

	/**
	 * @param heightAdjustment the heightAdjustment to set
	 */
	public void setHeightAdjustment(int heightAdjustment) {
		this.heightAdjustment = heightAdjustment;
	}

}
