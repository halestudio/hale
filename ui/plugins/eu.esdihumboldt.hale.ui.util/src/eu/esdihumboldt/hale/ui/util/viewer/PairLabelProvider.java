/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
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
