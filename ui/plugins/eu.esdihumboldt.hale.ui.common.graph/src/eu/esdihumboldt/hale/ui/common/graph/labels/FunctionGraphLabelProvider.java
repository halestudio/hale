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

package eu.esdihumboldt.hale.ui.common.graph.labels;

import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.zest.core.viewers.EntityConnectionData;
import org.eclipse.zest.core.widgets.custom.shapes.FingerPost;

import eu.esdihumboldt.hale.common.align.extension.function.AbstractParameter;
import eu.esdihumboldt.hale.common.align.extension.function.Function;
import eu.esdihumboldt.util.Pair;

/**
 * Label provider for graphs based on {@link Function}(s).
 * 
 * @author Patrick Lieb
 */
public class FunctionGraphLabelProvider extends GraphLabelProvider {
	
	private final Color targetbackgroundcolor;
	
	private final Color sourcebackgroundcolor;

	/**
	 * @see eu.esdihumboldt.hale.ui.common.graph.labels.GraphLabelProvider#getImage(java.lang.Object)
	 */
	
	public FunctionGraphLabelProvider(){
		
		final Display display = PlatformUI.getWorkbench().getDisplay();
		
		targetbackgroundcolor = new Color(display, 255, 160, 122);
		sourcebackgroundcolor = new Color(display, 255, 236, 139);	
	}
	
	@Override
	public Image getImage(Object element) {
		
		if (element instanceof Pair<?, ?>) {
			return super.getImage(((Pair<?, ?>) element).getFirst());
		}

		return super.getImage(element);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.graph.labels.GraphLabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		
		if (element instanceof Pair<?, ?>) {
			element = ((Pair<?, ?>) element).getFirst();
		}

		if (element instanceof EntityConnectionData) {
			return "";
		}

		if (element instanceof AbstractParameter) {
			String result = ((AbstractParameter) element).getDisplayName();
			if (!result.equals(""))
				return result;
			// XXX only for developing use
			return "(not set)";
		}

		return super.getText(element);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.graph.labels.GraphLabelProvider#getFigure(java.lang.Object)
	 */
	@Override
	public IFigure getFigure(Object element) {

		if (element instanceof AbstractParameter) {
			return new ParameterFigure(
					new FingerPost(10, SWT.LEFT), getOccurenceString((AbstractParameter) element));
		}
		
		if (element instanceof Pair<?, ?>) {
			element = ((Pair<?, ?>) element).getFirst();
		}
		
		if (element instanceof AbstractParameter) {
			return new ParameterFigure(
					new FingerPost(10, SWT.RIGHT), getOccurenceString((AbstractParameter) element));
		}
		
		return super.getFigure(element);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.graph.labels.GraphLabelProvider#getBackgroundColour(java.lang.Object)
	 */
	@Override
	public Color getBackgroundColour(Object entity) {
		
		if (entity instanceof AbstractParameter) {
			return targetbackgroundcolor;
		}
		
		if (entity instanceof Pair<?, ?>) {
			entity = ((Pair<?, ?>) entity).getFirst();
		}

		if (entity instanceof AbstractParameter) {
			return sourcebackgroundcolor;
		}
		
		return super.getBackgroundColour(entity);
	}
	
	private String getOccurenceString(AbstractParameter parameter){
		
		String max;
		String min;
		
		if(parameter.getMinOccurrence() == -1){
			min = "n";
		} else {
			min = String.valueOf(parameter.getMinOccurrence());
		}
		
		if(parameter.getMaxOccurrence() == -1){
			max = "n";
		} else {
			max = String.valueOf(parameter.getMaxOccurrence());
		}
		
		String text = min + "..." + max;
		
		return text;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.graph.labels.GraphLabelProvider#dispose()
	 */
	@Override
	public void dispose() {
		targetbackgroundcolor.dispose();
		sourcebackgroundcolor.dispose();
		super.dispose();
	}

}
