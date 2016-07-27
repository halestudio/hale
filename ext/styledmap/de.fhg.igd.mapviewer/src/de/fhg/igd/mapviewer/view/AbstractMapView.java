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
package de.fhg.igd.mapviewer.view;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.part.WorkbenchPart;

import de.fhg.igd.mapviewer.view.PositionStatus.EpsgProvider;

/**
 * Abstract map view
 * 
 * @author Andreas Stein
 */
public abstract class AbstractMapView extends ViewPart {

	private final SelectionProviderFacade selectionProvider = new SelectionProviderFacade();

	/**
	 * The EPSG code provider
	 */
	protected EpsgProvider epsgProvider = new EpsgProvider() {

		@Override
		public int getEpsgCode() {
			// default: use map CRS
			return 0;
		}
	};

	/**
	 * Set the selection provider
	 * 
	 * @param prov the selection provider to set
	 */
	public void setSelectionProvider(ISelectionProvider prov) {
		selectionProvider.setSelectionProvider(prov);
	}

	/**
	 * @see WorkbenchPart#createPartControl(Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		getSite().setSelectionProvider(selectionProvider);
	}

	/**
	 * @return the epsgProvider
	 */
	public EpsgProvider getEpsgProvider() {
		return epsgProvider;
	}

	/**
	 * @param epsgProvider the epsgProvider to set
	 */
	public void setEpsgProvider(EpsgProvider epsgProvider) {
		this.epsgProvider = epsgProvider;
	}

}
