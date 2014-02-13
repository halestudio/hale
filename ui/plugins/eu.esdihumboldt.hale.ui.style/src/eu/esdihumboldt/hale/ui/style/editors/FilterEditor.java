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
package eu.esdihumboldt.hale.ui.style.editors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.visitor.DefaultFilterVisitor;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.PropertyIsGreaterThan;
import org.opengis.filter.PropertyIsGreaterThanOrEqualTo;
import org.opengis.filter.PropertyIsLessThan;
import org.opengis.filter.PropertyIsLessThanOrEqualTo;
import org.opengis.filter.PropertyIsLike;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.instance.helper.PropertyResolver;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.common.definition.selector.PropertyDefinitionSelector;

/**
 * Editor for {@link Filter}s
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class FilterEditor implements Editor<Filter> {

	/**
	 * Filter inspector
	 */
	private abstract class FilterVisitor extends DefaultFilterVisitor {

		private Object visitFilter(FilterIdentifier id, Expression exp1, Expression exp2) {
			if (exp1 instanceof PropertyName && exp2 instanceof Literal) {
				visitFilter(id, ((PropertyName) exp1).getPropertyName(), ((Literal) exp2)
						.getValue().toString());
			}
			else if (exp2 instanceof PropertyName && exp1 instanceof Literal) {
				visitFilter(id, ((PropertyName) exp2).getPropertyName(), ((Literal) exp1)
						.getValue().toString());
			}

			return null;
		}

		/**
		 * Visit a filter that matches the pattern of the {@link FilterEditor}
		 * 
		 * @param id the filter identifier
		 * @param propertyName the property name
		 * @param value the value
		 */
		protected abstract void visitFilter(FilterIdentifier id, String propertyName, String value);

		/**
		 * @see DefaultFilterVisitor#visit(PropertyIsEqualTo, Object)
		 */
		@Override
		public Object visit(PropertyIsEqualTo filter, Object data) {
			return visitFilter(FilterIdentifier.EQUAL, filter.getExpression1(),
					filter.getExpression2());
		}

		/**
		 * @see DefaultFilterVisitor#visit(PropertyIsLike, Object)
		 */
		@Override
		public Object visit(PropertyIsLike filter, Object data) {
			return visitFilter(FilterIdentifier.LIKE, filter.getExpression(),
					filterFactory.literal(filter.getLiteral()));
		}

		/**
		 * @see DefaultFilterVisitor#visit(PropertyIsGreaterThan, Object)
		 */
		@Override
		public Object visit(PropertyIsGreaterThan filter, Object data) {
			return visitFilter(FilterIdentifier.GREATER_THAN, filter.getExpression1(),
					filter.getExpression2());
		}

		/**
		 * @see DefaultFilterVisitor#visit(PropertyIsGreaterThanOrEqualTo,
		 *      Object)
		 */
		@Override
		public Object visit(PropertyIsGreaterThanOrEqualTo filter, Object data) {
			return visitFilter(FilterIdentifier.GREATER_OR_EQUAL, filter.getExpression1(),
					filter.getExpression2());
		}

		/**
		 * @see DefaultFilterVisitor#visit(PropertyIsLessThan, Object)
		 */
		@Override
		public Object visit(PropertyIsLessThan filter, Object data) {
			return visitFilter(FilterIdentifier.LESS_THAN, filter.getExpression1(),
					filter.getExpression2());
		}

		/**
		 * @see DefaultFilterVisitor#visit(PropertyIsLessThanOrEqualTo, Object)
		 */
		@Override
		public Object visit(PropertyIsLessThanOrEqualTo filter, Object data) {
			return visitFilter(FilterIdentifier.LESS_OR_EQUAL, filter.getExpression1(),
					filter.getExpression2());
		}

	}

	/**
	 * Supported filters enumeration
	 */
	public enum FilterIdentifier {
		/**
		 * @see FilterFactory#equals(Expression, Expression)
		 */
		EQUAL,
		/**
		 * @see FilterFactory#less(Expression, Expression)
		 */
		LESS_THAN,
		/**
		 * @see FilterFactory#lessOrEqual(Expression, Expression)
		 */
		LESS_OR_EQUAL,
		/**
		 * @see FilterFactory#greater(Expression, Expression)
		 */
		GREATER_THAN,
		/**
		 * @see FilterFactory#greaterOrEqual(Expression, Expression)
		 */
		GREATER_OR_EQUAL,
		/**
		 * @see FilterFactory#like(Expression, String)
		 */
		LIKE;

		/**
		 * @see Enum#toString()
		 */
		@Override
		public String toString() {
			switch (this) {
			case EQUAL:
				return "="; //$NON-NLS-1$
			case LESS_THAN:
				return "<"; //$NON-NLS-1$
			case LESS_OR_EQUAL:
				return "<="; //$NON-NLS-1$
			case GREATER_THAN:
				return ">"; //$NON-NLS-1$
			case GREATER_OR_EQUAL:
				return ">="; //$NON-NLS-1$
			case LIKE:
				return "like"; //$NON-NLS-1$
			default:
				return super.toString();
			}
		}

	}

	private static final ALogger log = ALoggerFactory.getLogger(FilterEditor.class);

	private static final FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(null);

	private final Composite page;

	private final PropertyDefinitionSelector propertySelect;

	private final ComboViewer filterSelect;

	private final TextViewer literal;

	private final Button filterEnabled;

	private final TypeDefinition typeDefinition;

	private boolean changed = false;

	/**
	 * Creates a {@link Filter} editor
	 * 
	 * @param parent the parent composite
	 * @param typeDefinition the type definition
	 * @param filter the initial filter
	 */
	public FilterEditor(Composite parent, TypeDefinition typeDefinition, Filter filter) {
		super();

		this.typeDefinition = typeDefinition;
		page = new Composite(parent, SWT.NONE);
		page.setLayout(new GridLayout(4, false));

		// filter enabled
		filterEnabled = new Button(page, SWT.CHECK);
		filterEnabled.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));

		// enable/disable controls
		filterEnabled.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean enabled = ((Button) e.widget).getSelection();
				setControlsEnabled(enabled);
				changed = true;
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// ignore
			}
		});

		// property select
		propertySelect = new PropertyDefinitionSelector(page, typeDefinition, null);
		propertySelect.getControl().setLayoutData(
				GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).minSize(100, SWT.DEFAULT)
						.grab(true, false).create());

		// filter here

		// filter select
		filterSelect = new ComboViewer(page);
		filterSelect.getControl().setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		filterSelect.add(FilterIdentifier.values());

		// literal editor
		literal = new TextViewer(page, SWT.SINGLE | SWT.BORDER);
		literal.getControl().setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, true, false));
		literal.setDocument(new Document());

		// set initial values
		setValue(filter);

		// change listeners
		propertySelect.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				changed = true;
			}

		});

		filterSelect.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				changed = true;
			}

		});

		literal.getDocument().addDocumentListener(new IDocumentListener() {

			@Override
			public void documentChanged(DocumentEvent event) {
				changed = true;
			}

			@Override
			public void documentAboutToBeChanged(DocumentEvent event) {
				// ignore
			}
		});
	}

	private void setControlsEnabled(boolean enabled) {
		propertySelect.getControl().setEnabled(enabled);
		filterSelect.getControl().setEnabled(enabled);
		literal.getControl().setEnabled(enabled);
	}

	/**
	 * @see Editor#getControl()
	 */
	@Override
	public Control getControl() {
		return page;
	}

	/**
	 * @see Editor#getValue()
	 */
	@Override
	public Filter getValue() {
		if (filterEnabled.getSelection()) {
			try {
				FilterIdentifier id = (FilterIdentifier) ((IStructuredSelection) filterSelect
						.getSelection()).getFirstElement();

				String propertyName = "";

				// get the selected entity from the property selector (filter
				// fields)
				// and pass it to the filter
				EntityDefinition entity = propertySelect.getSelectedObject();

				if (entity != null) {
					Iterator<ChildContext> childIt = entity.getPropertyPath().iterator();

					if (childIt.hasNext()) {
						propertyName = propertyName.concat(childIt.next().getChild().getName()
								.toString());
					}

					while (childIt.hasNext()) {
						propertyName = propertyName.concat("."
								+ childIt.next().getChild().getName().toString());
					}

				}

				else {
					propertyName = "<select>";
				}

				String valueText = literal.getDocument().get();

				Expression property = filterFactory.property(propertyName);
				Expression value = filterFactory.literal(valueText);

				switch (id) {
				case EQUAL:
					return filterFactory.equals(property, value);
				case LESS_THAN:
					return filterFactory.less(property, value);
				case LESS_OR_EQUAL:
					return filterFactory.lessOrEqual(property, value);
				case GREATER_THAN:
					return filterFactory.greater(property, value);
				case GREATER_OR_EQUAL:
					return filterFactory.greaterOrEqual(property, value);
				case LIKE:
					return filterFactory.like(property, valueText);
				default:
					return null;
				}
			} catch (Exception e) {
				log.warn("Error getting filter", e); //$NON-NLS-1$
				return null;
			}
		}
		else {
			// no filter
			return null;
		}
	}

	/**
	 * @see Editor#isChanged()
	 */
	@Override
	public boolean isChanged() {
		return changed;
	}

	/**
	 * @see Editor#setValue(Object)
	 */
	@Override
	public void setValue(final Filter filter) {
		filterEnabled.setSelection(false);
		setControlsEnabled(false);

		if (filter != null) {
			filter.accept(new FilterVisitor() {

				@Override
				protected void visitFilter(FilterIdentifier id, String propertyName, String value) {

					Boolean invalidProperty = false;
					List<ChildContext> path = new ArrayList<ChildContext>();

					// set the correct selected name for the property selector
					List<QName> qNames = PropertyResolver.getQNamesFromPath(propertyName);

					ChildDefinition<?> child = typeDefinition.getChild(qNames.get(0));
					if (child != null) {
						path.add(new ChildContext(child));

						for (int i = 1; i < qNames.size(); i++) {
							child = DefinitionUtil.getChild(child, qNames.get(i));
							if (child != null) {
								path.add(new ChildContext(child));
							}
							else {
								invalidProperty = true;
								break;
							}
						}
					}
					else {
						invalidProperty = true;
					}

					if (!invalidProperty && !path.isEmpty()) {
						PropertyEntityDefinition entity = new PropertyEntityDefinition(
								typeDefinition, path, null, null);
						propertySelect.setSelection(new StructuredSelection(entity));
					}
					else {
						propertySelect.setSelection(new StructuredSelection());
					}
					// set filter
					filterSelect.setSelection(new StructuredSelection(id));

					// set value
					literal.getDocument().set(value);

					filterEnabled.setSelection(true);
					setControlsEnabled(true);
				}
			}, null);
		}
	}
}
