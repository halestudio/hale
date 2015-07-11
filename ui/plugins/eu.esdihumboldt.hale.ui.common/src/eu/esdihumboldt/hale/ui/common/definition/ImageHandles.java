/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.common.definition;

import org.eclipse.swt.graphics.Image;

import com.google.common.base.FinalizableReferenceQueue;
import com.google.common.base.FinalizableWeakReference;

/**
 * Disposes unused images.
 * 
 * @author Simon Templer
 */
public class ImageHandles {

	private final FinalizableReferenceQueue referenceQueue = new FinalizableReferenceQueue();

	/**
	 * Default constructor.
	 */
	public ImageHandles() {
		super();
	}

	/**
	 * Add an image.
	 * 
	 * @param image an image that should be disposed when there are no longer
	 *            any references to it
	 */
	public synchronized void addReference(Image image) {
		new FinalizableWeakReference<Image>(image, referenceQueue) {

			@Override
			public void finalizeReferent() {
				try {
					Image image = get();
					if (!image.isDisposed()) {
						image.dispose();
					}
				} catch (Exception e) {
					// ignore
				}
			}
		};
	}

}
