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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.views.styledmap;

import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;

/**
 * Helper class providing a work-around for Eclipse Bug 233450 (
 * {@link "https://bugs.eclipse.org/bugs/show_bug.cgi?id=233450"}) where with
 * GTK as windowing system it is not possible to show a SWT menu on top of a
 * AWT/Swing component.<br>
 * <br>
 * Implementation based on
 * {@link "http://www.eclipsezone.com/eclipse/forums/t95687.html"}
 * 
 * @author Simon Templer
 */
public class GTKAWTBridgePopupFix {

	private static final int MAX_ATTEMPTS = 200;
	private static final int MAX_RETRIES = 5;

	/**
	 * Show the given SWT menu. Must be called from the display thread.
	 * 
	 * @param menu the menu to show
	 */
	public static void showMenu(final Menu menu) {
		showMenu(menu, MAX_RETRIES);
	}

	private static void showMenu(final Menu menu, final int retriesLeft) {
		if (retriesLeft > 0) {
			final Display display = Display.getCurrent();

			// remember the active shell
			final Shell active = display.getActiveShell();

			// create a dummy shell the popup will be displayed over
			final Shell useForPopups = new Shell(display, SWT.NO_TRIM | SWT.NO_FOCUS | SWT.ON_TOP);
			Point l = display.getCursorLocation();
			l.x -= 2;
			l.y -= 2;
			useForPopups.setLocation(l);
			useForPopups.setSize(4, 4);
			useForPopups.open();
			final AtomicInteger count = new AtomicInteger(0);

			Runnable r = new Runnable() {

				private Listener hideListener;
				private Listener showListener;

				private void removeListeners() {
					if (hideListener != null) {
						menu.removeListener(SWT.Hide, hideListener);
					}
					if (showListener != null) {
						menu.removeListener(SWT.Show, showListener);
					}
				}

				@Override
				public void run() {
					useForPopups.setActive();

					menu.addListener(SWT.Hide, hideListener = new Listener() {

						@Override
						public void handleEvent(Event e) {
							removeListeners();
							useForPopups.dispose();
							if (!active.isDisposed()) {
								active.setActive();
							}
						}
					});
					menu.addListener(SWT.Show, showListener = new Listener() {

						@Override
						public void handleEvent(Event e) {
							count.incrementAndGet();
							if (!menu.isVisible() && count.get() > MAX_ATTEMPTS) {
								Runnable r = new Runnable() {

									@Override
									public void run() {
										menu.setVisible(false);
										removeListeners();
										useForPopups.dispose();
										showMenu(menu, retriesLeft - 1);
									}
								};
								display.asyncExec(r);
								return;
							}

							Runnable r = new Runnable() {

								@Override
								public void run() {
									if (!menu.isVisible()) {
										menu.setVisible(true);
									}
								}
							};
							display.asyncExec(r);
						}
					});

					menu.setVisible(true);
				}
			};

			display.asyncExec(r);
		}
	}
}