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

package eu.esdihumboldt.hale.ui.views.data.internal.compare;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.core.report.Message;
import eu.esdihumboldt.hale.common.core.report.Report;
import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instancevalidator.InstanceValidator;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.common.definition.DefinitionImages;
import eu.esdihumboldt.hale.ui.common.definition.viewer.TypeDefinitionContentProvider;

/**
 * Label provider for instances in a tree based on a 
 * {@link TypeDefinitionContentProvider}.
 *
 * @author Simon Templer
 */
public class DefinitionInstanceLabelProvider extends StyledCellLabelProvider {
	/**
	 * The pattern of the text for multiple values.
	 * This is used by {@link DefinitionInstanceTreeViewer} to determine
	 * if a cell is "editable".
	 * Furthermore it is expected to be at the end of the cell text.
	 */
	public static final String MULTIPLE_VALUE_FORMAT = "({0} of {1})";

	private static final int MAX_STRING_LENGTH = 200;

	private final Instance instance;
	
	private final DefinitionImages images = new DefinitionImages();

	private Map<LinkedList<Object>, Integer> chosenPaths = new HashMap<LinkedList<Object>, Integer>();
	
	/**
	 * Create an instance label provider for tree based on a 
	 * {@link TypeDefinition} 
	 * @param instance the instance to use 
	 */
	public DefinitionInstanceLabelProvider(Instance instance) {
		super();

		this.instance = instance;
	}

	/**
	 * @see CellLabelProvider#update(ViewerCell)
	 */
	@Override
	public void update(ViewerCell cell) {
		TreePath treePath = cell.getViewerRow().getTreePath();
		
		// descend in instance
		int valueCount = 0;
		int choice = 0;
		Object value = instance;
		ChildDefinition<?> childDef = null;
		LinkedList<Object> segmentList = new LinkedList<Object>();
		segmentList.add(treePath.getFirstSegment());
		// First segment is TypeDefinition.
		for (int i = 1; value != null && i < treePath.getSegmentCount(); i++) {
			Object segment = treePath.getSegment(i);
			segmentList.add(segment);
			if (segment instanceof ChildDefinition<?>) {
				childDef = (ChildDefinition<?>) segment;
				Object[] values = ((Group) value).getProperty(childDef.getName());
				choice = 0;
				valueCount = 0;
				if (values != null && values.length > 0) {
					Integer chosenPath = chosenPaths.get(segmentList);
					choice = chosenPath == null ? 0 : chosenPath;
					value = values[choice];
					valueCount = values.length;
				} else
					value = null;
			} else {
				//TODO log message?
				value = null;
			}
		}

		Report<Message> report = null;
		// If childDef is null we are at the top element.
		if (childDef == null)
			report = InstanceValidator.validate(instance);

		boolean hasValue = false;
		if (value instanceof Instance) {
			hasValue = ((Instance) value).getValue() != null;
		}

		StyledString styledString;
		if (value == null) {
			styledString = new StyledString("no value", StyledString.DECORATIONS_STYLER);
		}
		else if (value instanceof Group && !hasValue) {
			styledString = new StyledString("+", StyledString.QUALIFIER_STYLER);
		}
		else {
			if (value instanceof Instance) {
				value = ((Instance) value).getValue();
			}
			//TODO some kind of conversion?
			String stringValue = value.toString();
			/*
			 * Values that are very large, e.g. string representations of very
			 * complex geometries lead to StyledCellLabelProvider.updateTextLayout
			 * taking a very long time, rendering the application unresponsive
			 * when the data views are displayed.
			 * As such, we reduce the string to a maximum size.
			 */
			if (stringValue.length() > MAX_STRING_LENGTH) {
				stringValue = stringValue.substring(0, MAX_STRING_LENGTH) + "...";
			}

			styledString = new StyledString(stringValue, null);
		}
		
		// mark cell if there are other values
		if (valueCount > 1) {
			String decoration = " " + MessageFormat.format(MULTIPLE_VALUE_FORMAT, choice + 1, valueCount);
			styledString.append(decoration, StyledString.COUNTER_STYLER);
		}

		cell.setText(styledString.toString());
		cell.setStyleRanges(styledString.getStyleRanges());

		if (report != null && !report.getWarnings().isEmpty())
			cell.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_WARN_TSK));

		super.update(cell);
	}

	/**
	 * Select a specific path.
	 *
	 * @param path the path at which a choice is necessary
	 * @param choice the made choice
	 */
	public void selectPath(TreePath path, int choice) {
		LinkedList<Object> segmentList = new LinkedList<Object>();
		for (int i = 0; i < path.getSegmentCount(); i++)
			segmentList.add(path.getSegment(i));
		if (choice == 1)
			chosenPaths.remove(segmentList);
		else
			chosenPaths.put(segmentList, choice - 1);
	}

	/**
	 * @see BaseLabelProvider#dispose()
	 */
	@Override
	public void dispose() {
		images.dispose();

		super.dispose();
	}

	/**
	 * @see org.eclipse.jface.viewers.CellLabelProvider#getToolTipText(java.lang.Object)
	 */
	@Override
	public String getToolTipText(Object element) {
		if (element instanceof TypeDefinition) {
			Report<Message> report = InstanceValidator.validate(instance);

			Collection<Message> warnings = report.getWarnings();

			if (warnings.isEmpty())
				return null;

			StringBuilder toolTip = new StringBuilder();
			for (Message m : warnings)
				toolTip.append(m.getFormattedMessage()).append('\n');

			return toolTip.substring(0, toolTip.length() - 1);
		} else
			return null;
	}
}
