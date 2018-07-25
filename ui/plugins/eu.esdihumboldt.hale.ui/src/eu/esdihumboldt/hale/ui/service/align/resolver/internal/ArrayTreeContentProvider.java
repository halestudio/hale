/*
 * Copyright (c) 2018 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.ui.service.align.resolver.internal;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Base class for tree content provider based on {@link ArrayContentProvider}.
 * 
 * This class was introduced because of a Groovy compiler problem in context of
 * Java 8 that would only appear at runtime. It seems that the methods
 * {@link #dispose()} and {@link #inputChanged(Viewer, Object, Object)} are
 * provided both via the base class and the interface was a problem for the
 * Groovy compiler.
 * 
 * @author Simon Templer
 */
public abstract class ArrayTreeContentProvider extends ArrayContentProvider
		implements ITreeContentProvider {

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		super.inputChanged(viewer, oldInput, newInput);
	}

}
