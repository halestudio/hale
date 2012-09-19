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

package eu.esdihumboldt.hale.ui.views.styledmap.tool;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.jdesktop.swingx.mapviewer.GeoPosition;

import de.fhg.igd.mapviewer.tools.AbstractMapTool;
import eu.esdihumboldt.hale.ui.selection.InstanceSelection;
import eu.esdihumboldt.hale.ui.selection.impl.DefaultInstanceSelection;
import eu.esdihumboldt.hale.ui.views.styledmap.painter.AbstractInstancePainter;

/**
 * Instance selection tool.
 * 
 * @author Simon Templer
 */
public abstract class AbstractInstanceTool extends AbstractMapTool implements ISelectionProvider,
		ISelectionListener {

	private final Set<ISelectionChangedListener> listeners = new HashSet<ISelectionChangedListener>();

	private ISelection lastSelection = null;

	/**
	 * Default constructor
	 */
	public AbstractInstanceTool() {
		super();

		initSelection();
	}

	private void initSelection() {
		// initialize the selection with the existing selection
		if (Display.getCurrent() == null) {
			final Display display = PlatformUI.getWorkbench().getDisplay();
			display.syncExec(new Runnable() {

				@Override
				public void run() {
					lastSelection = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getSelectionService().getSelection();
				}
			});
		}
		else {
			lastSelection = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getSelectionService().getSelection();
		}
	}

	/**
	 * @see AbstractMapTool#activate()
	 */
	@Override
	public void activate() {
		super.activate();

		initSelection();
	}

	/**
	 * @see AbstractMapTool#click(MouseEvent, GeoPosition)
	 */
	@Override
	public void click(MouseEvent me, GeoPosition pos) {
		// override me
	}

	/**
	 * @see AbstractMapTool#popup(MouseEvent, GeoPosition)
	 */
	@Override
	public void popup(MouseEvent me, GeoPosition pos) {
		// override me
	}

	/**
	 * @see AbstractMapTool#released(MouseEvent, GeoPosition)
	 */
	@Override
	public void released(MouseEvent me, GeoPosition pos) {
		// override me
	}

	/**
	 * @see AbstractMapTool#pressed(MouseEvent, GeoPosition)
	 */
	@Override
	public void pressed(MouseEvent me, GeoPosition pos) {
		// override me
	}

	/**
	 * Fire a selection change
	 * 
	 * @param selection the new selection
	 */
	protected void fireSelectionChange(ISelection selection) {
		for (ISelectionChangedListener listener : listeners) {
			listener.selectionChanged(new SelectionChangedEvent(this, selection));
		}
	}

	/**
	 * @see ISelectionProvider#addSelectionChangedListener(ISelectionChangedListener)
	 */
	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		listeners.add(listener);
	}

	/**
	 * @see ISelectionProvider#getSelection()
	 */
	@Override
	public ISelection getSelection() {
		return lastSelection;
	}

	/**
	 * @see ISelectionProvider#removeSelectionChangedListener(ISelectionChangedListener)
	 */
	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		listeners.remove(listener);
	}

	/**
	 * @see ISelectionProvider#setSelection(ISelection)
	 */
	@Override
	public void setSelection(ISelection selection) {
		lastSelection = selection;
		fireSelectionChange(selection);
	}

	/**
	 * @see ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (selection != lastSelection) {
			if (selection instanceof InstanceSelection) {
				// only allow override by instance selections
				lastSelection = selection;
			}
		}
	}

	/**
	 * Update the selection for the given selection area.
	 * 
	 * @param selectionArea the selection area (as supported by
	 *            {@link #getSelection(AbstractInstancePainter, Object)})
	 * @param combineWithLast if the selection shall be combined with the last
	 *            selection
	 * @param allowPainterCombine if selections from different painters may be
	 *            combined
	 */
	protected void updateSelection(Object selectionArea, boolean combineWithLast,
			boolean allowPainterCombine) {
		ISelection newSelection = null;

		// determine new selection
		for (AbstractInstancePainter painter : mapKit
				.getTilePainters(AbstractInstancePainter.class)) {
			ISelection selection = getSelection(painter, selectionArea);
			if (allowPainterCombine) {
				newSelection = AbstractInstancePainter.combineSelection(newSelection, selection);
			}
			else {
				newSelection = AbstractInstancePainter.preferSelection(newSelection, selection);
			}
		}

		// combine with old
		if (combineWithLast) {
			newSelection = AbstractInstancePainter.combineSelection(lastSelection, newSelection);
		}

		if (newSelection == null) {
			newSelection = new DefaultInstanceSelection();
		}

		// selection update
		lastSelection = newSelection;
		fireSelectionChange(newSelection);
	}

	/**
	 * Get the selection for the given painter in the given selection area.
	 * 
	 * @param painter the instance painter
	 * @param selectionArea the selection area (supported are {@link Rectangle},
	 *            {@link Point}, {@link Polygon} or a {@link Polygon} array)
	 * 
	 * @return the selection or <code>null</code>
	 */
	protected ISelection getSelection(AbstractInstancePainter painter, Object selectionArea) {
		if (selectionArea instanceof Polygon[]) {
			Polygon[] polys = (Polygon[]) selectionArea;

			ISelection selection = null;
			for (Polygon poly : polys) {
				ISelection polySelection = painter.getSelection(poly);
				selection = AbstractInstancePainter.combineSelection(selection, polySelection);
			}

			return selection;
		}
		else if (selectionArea instanceof Polygon) {
			return painter.getSelection((Polygon) selectionArea);
		}
		else if (selectionArea instanceof Rectangle) {
			return painter.getSelection((Rectangle) selectionArea);
		}
		else if (selectionArea instanceof Point) {
			return painter.getSelection((Point) selectionArea);
		}
		else {
			return null;
		}
	}

}
