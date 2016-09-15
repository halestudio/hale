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

package de.fhg.igd.mapviewer.view.arecalculation;

import java.awt.Point;

import de.fhg.igd.mapviewer.tip.AbstractMapTip;

/**
 * MapTip for {@link AreaCalc}.
 * 
 * @author <a href="mailto:andreas.burchert@igd.fhg.de">Andreas Burchert</a>
 */
public class AreaMapTip extends AbstractMapTip implements AreaListener {

	private final AreaCalc calc;

	/**
	 * Constructor
	 * 
	 * @param calc the associated area calculator
	 */
	public AreaMapTip(AreaCalc calc) {
		this.calc = calc;

		calc.addListener(this);

		if (calc.isActive()) {
			setTipText(calc.getArea(), null);
		}
	}

	/**
	 * @see AbstractMapTip#position(int, int, int, int, int, int)
	 */
	@Override
	protected Point position(int x, int y, int tipHeight, int tipWidth, int viewWidth,
			int viewHeight) {
		// position below mouse
		// TODO respect bounds
		x += 5;
		y += 5;

		return new Point(x, y);
	}

	/**
	 * @see AbstractMapTip#dispose()
	 */
	@Override
	protected void dispose() {
		calc.removeListener(this);
	}

	/**
	 * @see AbstractMapTip#useMousePos()
	 */
	@Override
	protected boolean useMousePos() {
		return true;
	}

	/**
	 * @see AbstractMapTip#wantsToPaint()
	 */
	@Override
	public boolean wantsToPaint() {
		return super.wantsToPaint() && calc.isActive();
	}

	/**
	 * @see AreaListener#areaChanged(java.lang.String)
	 */
	@Override
	public void areaChanged(String area) {
		setTipText(area, null);
	}

	/**
	 * @see AreaListener#activationStateChanged(boolean)
	 */
	@Override
	public void activationStateChanged(boolean active) {
		if (active) {
			setTipText(calc.getArea(), null);
		}
		else {
			clearTip();
		}
	}

}
