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

import org.eclipse.draw2d.ConnectionRouter;
import org.eclipse.draw2d.IFigure;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.IDisposable;
import org.eclipse.zest.core.viewers.IEntityConnectionStyleProvider;
import org.eclipse.zest.core.viewers.IEntityStyleProvider;
import org.eclipse.zest.core.widgets.ZestStyles;

import eu.esdihumboldt.hale.common.align.extension.function.AbstractFunction;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.common.function.viewer.FunctionLabelProvider;

/**
 * Label provider for mapping graphs.
 * @author Simon Templer
 */
public class GraphLabelProvider extends LabelProvider implements IEntityStyleProvider,
		IEntityConnectionStyleProvider {
	
	private final int entityBorderWidth = 2;
	private final Color entityBorderColor;
	private final Color entityBorderHighlightColor;
	private final Color typeBackgroundColor;
	private final Color propertyBackgroundColor;
	private final Color entityHighlightColor;
	private final Color entityForegorundColor;
	
	private final int cellBorderWidth = 2;
	private final Color cellBorderColor;
	private final Color cellBorderHighlightColor;
	private final Color cellBackgroundColor;
	private final Color cellHighlightColor;
	private final Color cellForegroundColor;
	
	private final DefinitionLabelProvider definitionLabels = new DefinitionLabelProvider(true);
	
	private final FunctionLabelProvider functionLabels = new FunctionLabelProvider();

	/**
	 * Default constructor
	 */
	public GraphLabelProvider() {
		super();
		
		final Display display = PlatformUI.getWorkbench().getDisplay();
		
		//XXX keep entity colors in a central place?
		//XXX colors dependent of mapping context?
		
		// entity colors
		entityBorderColor = null;
		entityForegorundColor = null; //display.getSystemColor(SWT.COLOR_BLACK);
		typeBackgroundColor = new Color(display, 190, 230, 160);
		propertyBackgroundColor = new Color(display, 220, 250, 200);
		entityHighlightColor = new Color(display, 250, 250, 130);
		entityBorderHighlightColor = display.getSystemColor(SWT.COLOR_GRAY);
		
		// cell colors
		cellBorderColor = null;
		cellBorderHighlightColor = display.getSystemColor(SWT.COLOR_GRAY);
		cellBackgroundColor = null;
		cellForegroundColor = null;
		cellHighlightColor = entityHighlightColor;
	}

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
		
		if (element instanceof Definition<?>) {
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
		
		// dispose created colors
		typeBackgroundColor.dispose();
		propertyBackgroundColor.dispose();
		entityHighlightColor.dispose();
		entityHighlightColor.dispose();
		
//		cellBorderHighlightColor.dispose();
		
		super.dispose();
	}
	
	/**
	 * @see IEntityStyleProvider#getNodeHighlightColor(Object)
	 */
	@Override
	public Color getNodeHighlightColor(Object entity) {
		if (entity instanceof Entity || entity instanceof Definition<?>) {
			return entityHighlightColor;
		}
		
		if (entity instanceof Cell) {
			return cellHighlightColor;
		}
		
		return null;
	}

	/**
	 * @see IEntityStyleProvider#getBorderColor(Object)
	 */
	@Override
	public Color getBorderColor(Object entity) {
		if (entity instanceof Entity || entity instanceof Definition<?>) {
			return entityBorderColor;
		}
		
		if (entity instanceof Cell) {
			return cellBorderColor;
		}
		
		return null;
	}

	/**
	 * @see IEntityStyleProvider#getBorderHighlightColor(Object)
	 */
	@Override
	public Color getBorderHighlightColor(Object entity) {
		if (entity instanceof Entity || entity instanceof Definition<?>) {
			return entityBorderHighlightColor;
		}
		
		if (entity instanceof Cell) {
			return cellBorderHighlightColor;
		}
		
		return null;
	}

	/**
	 * @see IEntityStyleProvider#getBorderWidth(Object)
	 */
	@Override
	public int getBorderWidth(Object entity) {
		if (entity instanceof Entity || entity instanceof Definition<?>) {
			return entityBorderWidth;
		}
		
		if (entity instanceof Cell) {
			return cellBorderWidth;
		}
		
		return -1;
	}

	/**
	 * @see IEntityStyleProvider#getBackgroundColour(Object)
	 */
	@Override
	public Color getBackgroundColour(Object entity) {
		if (entity instanceof Entity || entity instanceof Definition<?>) {
			if (entity instanceof Type || entity instanceof TypeDefinition) {
				return typeBackgroundColor;
			}
			return propertyBackgroundColor;
		}
		
		if (entity instanceof Cell) {
			return cellBackgroundColor;
		}
		
		return null;
	}

	/**
	 * @see IEntityStyleProvider#getForegroundColour(Object)
	 */
	@Override
	public Color getForegroundColour(Object entity) {
		if (entity instanceof Entity || entity instanceof Definition<?>) {
			return entityForegorundColor;
		}
		
		if (entity instanceof Cell) {
			return cellForegroundColor;
		}
		
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

	/**
	 * @see IEntityConnectionStyleProvider#getConnectionStyle(Object, Object)
	 */
	@Override
	public int getConnectionStyle(Object src, Object dest) {
		return ZestStyles.CONNECTIONS_SOLID; // | ZestStyles.CONNECTIONS_DIRECTED;
	}

	/**
	 * @see IEntityConnectionStyleProvider#getColor(Object, Object)
	 */
	@Override
	public Color getColor(Object src, Object dest) {
		// default
		return null;
	}

	/**
	 * @see IEntityConnectionStyleProvider#getHighlightColor(Object, Object)
	 */
	@Override
	public Color getHighlightColor(Object src, Object dest) {
		// default
		return null;
	}

	/**
	 * @see IEntityConnectionStyleProvider#getLineWidth(Object, Object)
	 */
	@Override
	public int getLineWidth(Object src, Object dest) {
		// default
		return -1;
	}

	/**
	 * @see IEntityConnectionStyleProvider#getTooltip(Object, Object)
	 */
	@Override
	public IFigure getTooltip(Object src, Object dest) {
		// default
		return null;
	}

	/**
	 * @see IEntityConnectionStyleProvider#getRouter(Object, Object)
	 */
	@Override
	public ConnectionRouter getRouter(Object src, Object dest) {
		// default
		return null;
	}

}
