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

package eu.esdihumboldt.hale.ui.util.viewer;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import eu.esdihumboldt.util.Pair;

/**
 * Label provider that delegates to another label provider after extracting the
 * value from a {@link Pair}.
 * 
 * @author Simon Templer
 */
public class PairLabelProvider extends LabelProvider {

	private final boolean first;

	private final LabelProvider labels;

	/**
	 * Create a label provider extracting pair values to determine the label.
	 * 
	 * @param first if the first value in a pair shall be forwarded to the
	 *            delegate label provider, or the second
	 * @param labels the delegate label provider
	 */
	public PairLabelProvider(boolean first, LabelProvider labels) {
		super();
		this.first = first;
		this.labels = labels;
	}

	/**
	 * @see LabelProvider#getImage(Object)
	 */
	@Override
	public Image getImage(Object element) {
		if (element instanceof Pair<?, ?>) {
			Pair<?, ?> pair = (Pair<?, ?>) element;
			return labels.getImage((first) ? (pair.getFirst()) : (pair.getSecond()));
		}

		return labels.getImage(element);
	}

	/**
	 * @see LabelProvider#getText(Object)
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof Pair<?, ?>) {
			Pair<?, ?> pair = (Pair<?, ?>) element;
			return labels.getText((first) ? (pair.getFirst()) : (pair.getSecond()));
		}

		return super.getText(element);
	}

}
