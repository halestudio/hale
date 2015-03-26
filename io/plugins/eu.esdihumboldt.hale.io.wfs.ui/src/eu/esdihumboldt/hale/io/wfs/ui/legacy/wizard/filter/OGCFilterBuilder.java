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
package eu.esdihumboldt.hale.io.wfs.ui.legacy.wizard.filter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.PropertyDescriptor;

import com.vividsolutions.jts.geom.Geometry;

import eu.esdihumboldt.hale.io.wfs.ui.internal.Messages;

/**
 * SWT Component for specifying a filter
 * 
 * @author unknown
 */
public class OGCFilterBuilder extends Composite {

	FeatureType featureType;
	final CheckboxTableViewer tableViewer;
	final ConditionDataProvider data;

	/**
	 * Constructor
	 * 
	 * @param parent the parent composite
	 * @param featureType the feature type to be filtered
	 */
	public OGCFilterBuilder(Composite parent, FeatureType featureType) {
		super(parent, SWT.NONE);

		this.featureType = featureType;

		setLayout(new GridLayout(1, false));
		setLayoutData(new GridData(GridData.FILL_BOTH));

		final Group conditionsGroup = new Group(this, SWT.NONE);
		conditionsGroup.setLayout(new GridLayout(1, false));
		conditionsGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		conditionsGroup.setText(Messages.OGCFilterBuilder_0); //$NON-NLS-1$

		Table table = new Table(conditionsGroup, SWT.CHECK | SWT.SINGLE | SWT.BORDER
				| SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));

		tableViewer = new CheckboxTableViewer(table);

		String[] titles = { Messages.OGCFilterBuilder_1, Messages.OGCFilterBuilder_2,
				Messages.OGCFilterBuilder_3, Messages.OGCFilterBuilder_4,
				Messages.OGCFilterBuilder_5 }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		int[] widths = { 50, 100, 185, 155, 50 };
		for (int i = 0; i < titles.length; i++) {
			TableViewerColumn columnViewer = new TableViewerColumn(tableViewer, SWT.NONE);
			columnViewer.getColumn().setText(titles[i]);
			columnViewer.getColumn().setWidth(widths[i]);
			columnViewer.getColumn().setResizable(true);
			columnViewer.getColumn().setMoveable(false);
			columnViewer.setEditingSupport(new ConditionEditingSupport(tableViewer, i));
		}

		// tableViewer.setUseHashlookup(true);
		tableViewer.setContentProvider(new ConditionContentProvider());
		tableViewer.setLabelProvider(new ConditionLabelProvider());
		tableViewer.addCheckStateListener(new ConditionCheckStateListener());

		data = new ConditionDataProvider();
		tableViewer.setInput(data);

		final Composite conditionButtons = new Composite(conditionsGroup, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		conditionButtons.setLayout(layout);

		final Button addConditionButton = new Button(conditionButtons, SWT.NONE);
		addConditionButton.setText(Messages.OGCFilterBuilder_6); //$NON-NLS-1$
		addConditionButton.setToolTipText(Messages.OGCFilterBuilder_7); //$NON-NLS-1$
		addConditionButton.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				data.getConditions().add(new Condition());
				tableViewer.refresh();
			}
		});

		final Button removeConditionButton = new Button(conditionButtons, SWT.NONE);
		removeConditionButton.setText(Messages.OGCFilterBuilder_8); //$NON-NLS-1$
		removeConditionButton.setToolTipText(Messages.OGCFilterBuilder_9); //$NON-NLS-1$
		removeConditionButton.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				ISelection selection = tableViewer.getSelection();
				if (selection != null && selection instanceof IStructuredSelection) {
					IStructuredSelection sel = (IStructuredSelection) selection;
					Object obj = sel.getFirstElement();
					if (obj != null && obj instanceof Condition) {
						data.getConditions().remove(obj);
						tableViewer.refresh();
					}
				}
			}
		});
	}

	/*
	 * void tingeRow(int index) { Table table = tableViewer.getTable(); Color
	 * color = getDisplay().getSystemColor(SWT.COLOR_RED); TableItem item =
	 * table.getItem(index); if (item != null) { item.setBackground(color);
	 * tableViewer.refresh(); } }
	 */

	String[] removeEmpty(String[] ar) {
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < ar.length; i++) {
			if (!ar[i].isEmpty()) {
				list.add(ar[i]);
			}
		}
		ar = new String[list.size()];
		return list.toArray(ar);
	}

	/**
	 * Build the filter string
	 * 
	 * @return the filter string
	 * @throws IllegalStateException if the defined conditions are not valid
	 */
	public String buildFilter() throws IllegalStateException {
		List<Condition> conditions = data.getConditions();

		Table table = tableViewer.getTable();
		// check all conditions
		for (int i = 0; i < conditions.size(); i++) {
			Condition condition = conditions.get(i);

			if (condition.getProperty() == null) {
				table.setSelection(i);
				throw new IllegalStateException("Property must be set at selected condition."); //$NON-NLS-1$
			}

			if (condition.getComparison() == null) {
				table.setSelection(i);
				throw new IllegalStateException("Comparison must be set at selected condition."); //$NON-NLS-1$
			}

			if (condition.getComparison() == null || condition.getValue().toString().isEmpty()) {
				if (!condition.getComparison().equals("PropertyIsNull")) { //$NON-NLS-1$
					table.setSelection(i);
					throw new IllegalStateException("Value must be set at selected condition."); //$NON-NLS-1$
				}
			}

			if (condition.getComparison().equals("PropertyIsBetween")) { //$NON-NLS-1$
				String[] s = removeEmpty(condition.getValue().toString().split(" |,|;")); //$NON-NLS-1$
				if (s.length != 2) {
					table.setSelection(i);
					throw new IllegalStateException(
							"Lower and upper value must be set, separated by space, comma or semicolon."); //$NON-NLS-1$
				}
				else {
					condition.setValue(s);
				}
			}

			if (i < conditions.size() - 1 && condition.getUnion() == null) {
				table.setSelection(i);
				throw new IllegalStateException("Logical union must be set at selected condition."); //$NON-NLS-1$
			}
		}

		Iterator<Condition> iter = conditions.iterator();
		Op operation = null;
		if (conditions.size() == 1) {
			Condition c = iter.next();
			operation = getOperation(c);
		}
		else if (conditions.size() >= 2) {
			Condition c = iter.next();
			LogicalOp lop = new LogicalOp(c.getUnion());
			lop.addOp(getOperation(c));
			c = iter.next();
			lop.addOp(getOperation(c));

			while (iter.hasNext()) {
				Condition c2 = iter.next();
				if (c.getUnion().equals(lop.name)) {
					lop.addOp(getOperation(c2));
				}
				else {
					LogicalOp lop2 = new LogicalOp(c.getUnion());
					lop2.addOp(lop);
					lop2.addOp(getOperation(c2));
					lop = lop2;
				}
				c = c2;
			}
			operation = lop;
		}

		if (operation != null) {
			StringWriter stringWriter = new StringWriter();
			PrintWriter writer = new PrintWriter(stringWriter);
			writer.println("<Filter>"); //$NON-NLS-1$
			operation.serialize(writer, 1);
			writer.print("</Filter>"); //$NON-NLS-1$

			return stringWriter.toString();
		}
		else {
			return ""; // no filter //$NON-NLS-1$
		}
	}

	private static void printIndent(PrintWriter w, int indent) {
		for (int i = 0; i < indent; i++) {
			w.print('\t');
		}
	}

	Op getOperation(Condition c) {
		Op operation = new ComparisonOp(c.getComparison(),
				c.getProperty().getName().getLocalPart(), c.getValue());
		if (c.isNegate()) {
			LogicalOp negOp = new LogicalOp("Not"); //$NON-NLS-1$
			negOp.addOp(operation);
			operation = negOp;
		}
		return operation;
	}

	private static abstract class Op {

		String name;

		public Op(String name) {
			this.name = name;
		}

		public abstract void serialize(PrintWriter out, int indent);
	}

	private static class LogicalOp extends Op {

		List<Op> ops;

		public LogicalOp(String name) {
			super(name);
			ops = new ArrayList<Op>();
		}

		public void addOp(Op op) {
			ops.add(op);
		}

		@Override
		public void serialize(PrintWriter out, int indent) {
			printIndent(out, indent);
			out.println("<" + name + ">"); //$NON-NLS-1$ //$NON-NLS-2$
			for (Op op : ops) {
				op.serialize(out, indent + 1);
			}
			printIndent(out, indent);
			out.println("</" + name + ">"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private class ComparisonOp extends Op {

		String property;
		Object value;

		public ComparisonOp(String name, String property, Object value) {
			super(name);
			this.property = property;
			this.value = value;
		}

		@Override
		public void serialize(PrintWriter out, int indent) {
			if (name.equals("PropertyIsLike")) { //$NON-NLS-1$
				printIndent(out, indent);
				out.println("<PropertyIsLike wildCard=\"*\" singleChar=\"?\" escapeChar=\"!\">"); //$NON-NLS-1$
			}
			else {
				printIndent(out, indent);
				out.println("<" + name + ">"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			printIndent(out, indent + 1);
			out.println("<PropertyName>" + featureType.getName().getLocalPart() + "/" + property + "</PropertyName>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			if (name.equals("PropertyIsBetween")) { //$NON-NLS-1$
				String lv = "?"; //$NON-NLS-1$
				String hv = "?"; //$NON-NLS-1$
				String[] s = (String[]) value;
				if (s.length == 2) {
					lv = s[0];
					hv = s[1];
				}
				printIndent(out, indent + 1);
				out.println("<LowerBoundary><Literal>" + lv + "</Literal></LowerBoundary>"); //$NON-NLS-1$ //$NON-NLS-2$
				printIndent(out, indent + 1);
				out.println("<UpperBoundary><Literal>" + hv + "</Literal></UpperBoundary>"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else if (!name.equals("PropertyIsNull")) { //$NON-NLS-1$
				printIndent(out, indent + 1);
				out.println("<Literal>" + value + "</Literal>"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			printIndent(out, indent);
			out.println("</" + name + ">"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private static class Condition {

		boolean negate;
		PropertyDescriptor property;
		String comparison;
		Object value;
		String union;

		public PropertyDescriptor getProperty() {
			return property;
		}

		public void setProperty(PropertyDescriptor property) {
			/*
			 * if (this.property != null) { if
			 * (!this.property.getType().getBinding
			 * ().isAssignableFrom(property.getType().getBinding())) {
			 * setComparison(null); value = null; } }
			 */
			this.property = property;

		}

		public boolean isNegate() {
			return negate;
		}

		public void setNegate(boolean negate) {
			this.negate = negate;
		}

		public String getComparison() {
			return comparison;
		}

		public void setComparison(String comparison) {
			this.comparison = comparison;
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}

		public String getUnion() {
			return union;
		}

		public void setUnion(String union) {
			this.union = union;
		}
	}

	private static class ConditionDataProvider {

		List<Condition> conditions = new ArrayList<Condition>();

		public List<Condition> getConditions() {
			return conditions;
		}
	}

	private static class ConditionContentProvider implements IStructuredContentProvider {

		@Override
		public Object[] getElements(Object inputElement) {
			ConditionDataProvider data = (ConditionDataProvider) inputElement;
			return data.getConditions().toArray();
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// ignore
		}

		@Override
		public void dispose() {
			// do nothing
		}
	}

	private static class ConditionLabelProvider extends LabelProvider implements
			ITableLabelProvider {

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			Condition condition = (Condition) element;

			if (columnIndex == 1) {
				if (condition.getProperty() != null) {
					return condition.getProperty().getName().getLocalPart();
				}
			}
			else if (columnIndex == 2) {
				return condition.getComparison();
			}
			else if (columnIndex == 3) {
				if (condition.getValue() != null) {
					return condition.getValue().toString();
				}
			}
			else if (columnIndex == 4) {
				if (condition.getUnion() != null) {
					return condition.getUnion();
				}
			}
			return null;
		}
	}

	private class ConditionEditingSupport extends EditingSupport {

		int column;

		ComboBoxCellEditor propertyEditor;
		List<PropertyDescriptor> propertyList = new ArrayList<PropertyDescriptor>();
		List<String> spatialConditionList = new ArrayList<String>();
		String[] spatialConditions = {
				"Equals", "Disjoint", "Touches", "Within", "Overlaps", "Crosses", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
				"Intersects", "Contains", "DWithin", "Beyond", "BBOX" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		ComboBoxCellEditor comparisonConditionEditor;
		List<String> comparisonConditionList = new ArrayList<String>();
		String[] comparisonConditions = {
				"PropertyIsEqualTo", "PropertyIsNotEqualTo", "PropertyIsLessThan", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				"PropertyIsGreaterThan", "PropertyIsLessThanOrEqualTo", "PropertyIsGreaterThanOrEqualTo", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				"PropertyIsLike", "PropertyIsNull", "PropertyIsBetween" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		ComboBoxCellEditor logicalUnionEditor;
		List<String> logicalUnionList = new ArrayList<String>();
		String[] logicalUnions = { "And", "Or" }; //$NON-NLS-1$ //$NON-NLS-2$
		TextCellEditor textEditor;

		public ConditionEditingSupport(ColumnViewer viewer, int column) {
			super(viewer);
			this.column = column;

			textEditor = new TextCellEditor(((TableViewer) viewer).getTable());

			if (featureType != null) {
				List<String> properties = new ArrayList<String>();
				Iterator<PropertyDescriptor> iterator = featureType.getDescriptors().iterator();
				while (iterator.hasNext()) {
					PropertyDescriptor property = iterator.next();
					// removes geometry property
					if (!Geometry.class.isAssignableFrom(property.getType().getBinding())) {
						propertyList.add(property);
						properties.add(property.getName().getLocalPart());
					}
				}
				String[] propertyNames = new String[properties.size()];
				propertyEditor = new ComboBoxCellEditor(((TableViewer) viewer).getTable(),
						properties.toArray(propertyNames), SWT.READ_ONLY);
				Control control = propertyEditor.getControl();
				if (control instanceof CCombo) {
					((CCombo) control).setVisibleItemCount(9);
				}
			}

			// fill spatialConditionList and prepare ComboBoxCellEditor
			for (int i = 0; i < spatialConditions.length; i++) {
				spatialConditionList.add(spatialConditions[i]);
			}
//			ComboBoxCellEditor spatialConditionEditor = new ComboBoxCellEditor(((TableViewer) viewer).getTable(), 
//					spatialConditions, SWT.READ_ONLY);

			/*
			 * propertyEditor = new ComboBoxCellEditor(((TableViewer)
			 * viewer).getTable(), spatialConditions, SWT.READ_ONLY);
			 */

			// fill comparisonConditionList and prepare ComboBoxCellEditor
			for (int i = 0; i < comparisonConditions.length; i++) {
				comparisonConditionList.add(comparisonConditions[i]);
			}
			comparisonConditionEditor = new ComboBoxCellEditor(((TableViewer) viewer).getTable(),
					comparisonConditions, SWT.READ_ONLY);
			Control control = comparisonConditionEditor.getControl();
			if (control instanceof CCombo) {
				((CCombo) control).setVisibleItemCount(9);
			}

			// fill logicalUnionList and prepare ComboBoxCellEditor
			for (int i = 0; i < logicalUnions.length; i++) {
				logicalUnionList.add(logicalUnions[i]);
			}
			logicalUnionEditor = new ComboBoxCellEditor(((TableViewer) viewer).getTable(),
					logicalUnions, SWT.READ_ONLY);
		}

		@Override
		protected Object getValue(Object element) {
			Condition condition = (Condition) element;

			if (column == 1) {
				return propertyList.indexOf(condition.getProperty());
			}
			else if (column == 2) {
				/*
				 * if
				 * (Geometry.class.isAssignableFrom(condition.getProperty().getType
				 * ().getBinding())) { return
				 * spatialConditionList.indexOf(condition.getComparison()); }
				 * else
				 */{
					return comparisonConditionList.indexOf(condition.getComparison());
				}
			}
			else if (column == 3) {
				Object value = condition.getValue();
				if (value == null) {
					value = ""; //$NON-NLS-1$
				}
				return value.toString();
			}
			else if (column == 4) {
				return logicalUnionList.indexOf(condition.getUnion());
			}
			return null;
		}

		@Override
		protected void setValue(Object element, Object value) {
			Condition condition = (Condition) element;

			if (column == 1) {
				if ((Integer) value != -1) {
					condition.setProperty(propertyList.get((Integer) value));
				}
			}
			else if (column == 2) {
				if ((Integer) value != -1) {
					/*
					 * if
					 * (Geometry.class.isAssignableFrom(condition.getProperty(
					 * ).getType().getBinding())) {
					 * condition.setComparison(spatialConditionList
					 * .get((Integer) value)); } else
					 */{
						condition.setComparison(comparisonConditionList.get((Integer) value));
					}
				}
			}
			else if (column == 3) {
				if (value != null) {
					condition.setValue(value.toString());
				}
			}
			else if (column == 4) {
				if ((Integer) value != -1) {
					condition.setUnion(logicalUnionList.get((Integer) value));
				}
			}

			getViewer().update(element, null);
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			// Condition condition = (Condition) element;

			if (column == 1) {
				return propertyEditor;
			}
			else if (column == 2) {
				/*
				 * if
				 * (Geometry.class.isAssignableFrom(condition.getProperty().getType
				 * ().getBinding())) { return spatialConditionEditor; } else
				 */{
					return comparisonConditionEditor;
				}
			}
			else if (column == 3) {
				/*
				 * if
				 * (Geometry.class.isAssignableFrom(condition.getProperty().getType
				 * ().getBinding())) { return spatialConditionEditor; } else
				 */{
					return textEditor;
				}
			}
			else if (column == 4) {
				return logicalUnionEditor;
			}

			return null;
		}
	}

	private static class ConditionCheckStateListener implements ICheckStateListener {

		@Override
		public void checkStateChanged(CheckStateChangedEvent event) {
			Condition condition = (Condition) event.getElement();
			condition.setNegate(event.getChecked());
		}
	}

}
