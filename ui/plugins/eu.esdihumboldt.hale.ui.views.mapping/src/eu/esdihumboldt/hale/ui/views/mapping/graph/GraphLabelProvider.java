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

package eu.esdihumboldt.hale.ui.views.mapping.graph;

import org.eclipse.draw2d.IFigure;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.services.IDisposable;
import org.eclipse.zest.core.viewers.IEntityStyleProvider;

import eu.esdihumboldt.hale.common.align.extension.function.AbstractFunction;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.common.function.viewer.FunctionLabelProvider;

/**
 * Label provider for mapping graphs.
 * @author Simon Templer
 */
public class GraphLabelProvider extends LabelProvider implements IEntityStyleProvider {
	
	private final DefinitionLabelProvider definitionLabels = new DefinitionLabelProvider();
	
	private final FunctionLabelProvider functionLabels = new FunctionLabelProvider();

	/**
	 * @see LabelProvider#getImage(Object)
	 */
	@Override
	public Image getImage(Object element) {
		if (element instanceof Entity) {
			element = ((Entity) element).getDefinition();
		}
		
		if (element instanceof EntityDefinition) {
			element = ((EntityDefinition) element).getDefinition();
		}
		
		if (element instanceof Definition<?>) {
			// use definition image
			return definitionLabels.getImage(element);
		}
		
		if (element instanceof Cell) {
			// use function image if possible
			Cell cell = (Cell) element;
			String functionId = cell.getTransformationIdentifier();
			AbstractFunction<?> function = FunctionUtil.getFunction(functionId);
			if (function != null) {
				return functionLabels.getImage(function);
			}
			return null;
		}
		
		return super.getImage(element);
	}

	/**
	 * @see LabelProvider#getText(Object)
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof Entity) {
			element = ((Entity) element).getDefinition();
		}
		
		if (element instanceof EntityDefinition) {
			// use definition text
			return definitionLabels.getText(element);
		}
		
		if (element instanceof Cell) {
			// use function name if possible
			Cell cell = (Cell) element;
			String functionId = cell.getTransformationIdentifier();
			AbstractFunction<?> function = FunctionUtil.getFunction(functionId);
			if (function != null) {
				return functionLabels.getText(function);
			}
			return functionId;
		}
		
		return super.getText(element);
	}

	/**
	 * @see IDisposable#dispose()
	 */
	@Override
	public void dispose() {
		definitionLabels.dispose();
		functionLabels.dispose();
	}

	/**
	 * @see IEntityStyleProvider#getNodeHighlightColor(Object)
	 */
	@Override
	public Color getNodeHighlightColor(Object entity) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see IEntityStyleProvider#getBorderColor(Object)
	 */
	@Override
	public Color getBorderColor(Object entity) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see IEntityStyleProvider#getBorderHighlightColor(Object)
	 */
	@Override
	public Color getBorderHighlightColor(Object entity) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see IEntityStyleProvider#getBorderWidth(Object)
	 */
	@Override
	public int getBorderWidth(Object entity) {
		// TODO Auto-generated method stub
		return -1;
	}

	/**
	 * @see IEntityStyleProvider#getBackgroundColour(Object)
	 */
	@Override
	public Color getBackgroundColour(Object entity) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see IEntityStyleProvider#getForegroundColour(Object)
	 */
	@Override
	public Color getForegroundColour(Object entity) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see IEntityStyleProvider#getTooltip(Object)
	 */
	@Override
	public IFigure getTooltip(Object entity) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see IEntityStyleProvider#fisheyeNode(Object)
	 */
	@Override
	public boolean fisheyeNode(Object entity) {
		// TODO Auto-generated method stub
		return false;
	}

}
