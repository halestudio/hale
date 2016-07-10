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

package eu.esdihumboldt.hale.ui.views.data.internal.compare;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.core.report.Message;
import eu.esdihumboldt.hale.common.instance.extension.validation.report.InstanceValidationMessage;
import eu.esdihumboldt.hale.common.instance.extension.validation.report.InstanceValidationReport;
import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instancevalidator.InstanceValidator;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.common.definition.DefinitionImages;
import eu.esdihumboldt.hale.ui.common.definition.viewer.TypeDefinitionContentProvider;

/**
 * Label provider for instances in a tree based on a
 * {@link TypeDefinitionContentProvider}. Also handles metadata of those
 * instances.
 * 
 * @author Simon Templer
 */
public class DefinitionInstanceLabelProvider extends StyledCellLabelProvider {

	/**
	 * The pattern of the text for multiple values. This is used by
	 * {@link DefinitionInstanceTreeViewer} to determine if a cell is
	 * "editable". Furthermore it is expected to be at the end of the cell text.
	 */
	public static final String MULTIPLE_VALUE_FORMAT = "({0} of {1})";

	private static final int MAX_STRING_LENGTH = 200;

	private final Instance instance;

	private final DefinitionImages images = new DefinitionImages();

	private final Map<LinkedList<Object>, Integer> chosenPaths = new HashMap<LinkedList<Object>, Integer>();

	private final Map<Object, Integer> chosenMetaPaths = new HashMap<Object, Integer>();

	/**
	 * Create an instance label provider for tree based on a
	 * {@link TypeDefinition}
	 * 
	 * @param instance the instance to use
	 */
	public DefinitionInstanceLabelProvider(Instance instance) {
		super();

		this.instance = instance;
	}

	@SuppressWarnings("javadoc")
	public static class InstanceEntry {

		/**
		 * If a definition is represented by the entry.
		 */
		private final boolean definition;

		/**
		 * The value count of the entry.
		 */
		private final int valueCount;

		/**
		 * The index of the chosen value.
		 */
		private final int choice;

		/**
		 * The actual value of the entry.
		 */
		private final Object value;

		/**
		 * The associated child definition, if any.
		 */
		private final ChildDefinition<?> childDef;

		public InstanceEntry(int valueCount, int choice, Object value, boolean definition,
				ChildDefinition<?> childDef) {
			super();
			this.valueCount = valueCount;
			this.choice = choice;
			this.value = value;
			this.definition = definition;
			this.childDef = childDef;
		}

		public int getValueCount() {
			return valueCount;
		}

		public int getChoice() {
			return choice;
		}

		public Object getValue() {
			return value;
		}

		public boolean isDefinition() {
			return definition;
		}

		public ChildDefinition<?> getChildDef() {
			return childDef;
		}

	}

	/**
	 * Find the instance entry at the given tree path.
	 * 
	 * @param treePath the tree path
	 * @return the instance entry information
	 */
	public InstanceEntry findInstanceEntry(TreePath treePath) {
		// descend in instance
		int valueCount = 0;
		int choice = 0;
		Object value = instance;
		ChildDefinition<?> childDef = null;
		LinkedList<Object> segmentList = new LinkedList<Object>();
		boolean definition = false; // if a definition is represented

		// First segment is TypeDefinition.
		if (treePath.getFirstSegment() instanceof EntityDefinition) {
			definition = true;

			segmentList.add(((EntityDefinition) treePath.getFirstSegment()).getType());
			for (int i = 1; value != null && i < treePath.getSegmentCount(); i++) {
				Object segment = treePath.getSegment(i);
				if (segment instanceof EntityDefinition) {
					segment = ((EntityDefinition) segment).getDefinition();
				}
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
					}
					else
						value = null;
				}
				else {
					// TODO log message?
					value = null;
				}
			}
		}
		else {
			// if segments contain a set of metadata keys
			if (treePath.getFirstSegment() instanceof Set<?>) {
				if (treePath.getSegmentCount() > 1 && value != null) {

					Object key = treePath.getLastSegment();

					if (key instanceof String) {
						List<Object> values = ((Instance) value).getMetaData(key.toString());
						choice = 0;
						valueCount = 0;
						if (values != null && values.size() > 0) {
							Integer chosenPath = chosenMetaPaths.get(key);
							choice = chosenPath == null ? 0 : chosenPath;
							value = values.get(choice);
							valueCount = values.size();
						}
						else
							value = null;
					}

				}
			}
		}

		return new InstanceEntry(valueCount, choice, value, definition, childDef);
	}

	/**
	 * @see CellLabelProvider#update(ViewerCell)
	 */
	@Override
	public void update(ViewerCell cell) {
		TreePath treePath = cell.getViewerRow().getTreePath();

		InstanceEntry entry = findInstanceEntry(treePath);
		Object value = entry.value;

		InstanceValidationReport report = null;
		// If childDef is null we are at the top element.
		if (entry.definition && entry.childDef == null) {
			report = InstanceValidator.validate(instance);
		}

		boolean hasValue = false;
		if (entry.definition && value instanceof Instance) {
			hasValue = ((Instance) value).getValue() != null;
		}
		else if (!entry.definition && treePath.getSegmentCount() == 1) {
			// metadata root
			if (instance.getMetaDataNames().isEmpty()) {
				hasValue = true;
				value = null;
			}
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
			// TODO some kind of conversion?
			String stringValue = value.toString();
			/*
			 * Values that are very large, e.g. string representations of very
			 * complex geometries lead to
			 * StyledCellLabelProvider.updateTextLayout taking a very long time,
			 * rendering the application unresponsive when the data views are
			 * displayed. As such, we reduce the string to a maximum size.
			 */
			if (stringValue.length() > MAX_STRING_LENGTH) {
				stringValue = stringValue.substring(0, MAX_STRING_LENGTH) + "...";
			}

			styledString = new StyledString(stringValue, null);
		}

		// mark cell if there are other values
		if (entry.valueCount > 1) {
			String decoration = " "
					+ MessageFormat.format(MULTIPLE_VALUE_FORMAT, entry.choice + 1,
							entry.valueCount);
			styledString.append(decoration, StyledString.COUNTER_STYLER);
		}

		cell.setText(styledString.toString());
		cell.setStyleRanges(styledString.getStyleRanges());

		if (report != null && !report.getWarnings().isEmpty())
			cell.setImage(PlatformUI.getWorkbench().getSharedImages()
					.getImage(ISharedImages.IMG_OBJS_WARN_TSK));

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
		for (int i = 0; i < path.getSegmentCount(); i++) {
			Object element = path.getSegment(i);
			if (element instanceof EntityDefinition) {
				element = ((EntityDefinition) element).getDefinition();
			}
			segmentList.add(element);
		}
		if (path.getFirstSegment() instanceof EntityDefinition) {
			if (choice == 1)
				chosenPaths.remove(segmentList);
			else
				chosenPaths.put(segmentList, choice - 1);
		}

		if (path.getFirstSegment() instanceof Set<?>) {
			if (choice == 1)
				chosenMetaPaths.remove(path.getLastSegment());
			chosenMetaPaths.put(path.getLastSegment(), choice - 1);
		}

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
		if (element instanceof EntityDefinition) {
			InstanceValidationReport report = InstanceValidator.validate(instance);

			Collection<InstanceValidationMessage> warnings = report.getWarnings();

			if (warnings.isEmpty())
				return null;

			StringBuilder toolTip = new StringBuilder();
			for (Message m : warnings)
				toolTip.append(m.getFormattedMessage()).append('\n');

			return toolTip.substring(0, toolTip.length() - 1);
		}
		else
			return null;
	}

	/**
	 * get the specific choosen metadata value number for a certain metadata key
	 * 
	 * @param key the metadata key
	 * @return the choice represented by an int
	 */
	public int getMetaDataChoice(String key) {
		if (chosenMetaPaths.containsKey(key)) {
			return chosenMetaPaths.get(key).intValue();
		}
		else
			return 0;
	}
}
