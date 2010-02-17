/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.rcp.views.map.style.editors;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.PropertyDescriptor;
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

import eu.esdihumboldt.hale.rcp.views.map.Messages;

/**
 * Editor for {@link Filter}s
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class FilterEditor implements Editor<Filter> {
	
	/**
	 * Filter inspector
	 */
	private abstract class FilterVisitor extends DefaultFilterVisitor {

		private Object visitFilter(FilterIdentifier id, Expression exp1,
				Expression exp2) {
			if (exp1 instanceof PropertyName && exp2 instanceof Literal) {
				visitFilter(id, ((PropertyName) exp1).getPropertyName(), 
						((Literal) exp2).getValue().toString());
			}
			else if (exp2 instanceof PropertyName && exp1 instanceof Literal) {
				visitFilter(id, ((PropertyName) exp2).getPropertyName(), 
						((Literal) exp1).getValue().toString());
			}
			
			return null;
		}

		/**
		 * Visit a filter that matches the pattern of the
		 *   {@link FilterEditor}
		 * 
		 * @param id the filter identifier
		 * @param propertyName the property name
		 * @param value the value
		 */
		protected abstract void visitFilter(FilterIdentifier id,
				String propertyName, String value);

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
		 * @see DefaultFilterVisitor#visit(PropertyIsGreaterThanOrEqualTo, Object)
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
	
	private static final Log log = LogFactory.getLog(FilterEditor.class);

	private static final FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(null);
	
	private final Composite page;
	
	private final Map<String, PropertyDescriptor> properties = new HashMap<String, PropertyDescriptor>();
	
	private final ComboViewer propertySelect;
	
	private final ComboViewer filterSelect;
	
	private final TextViewer literal;
	
	private final Button filterEnabled;
	
	private boolean changed = false;
	
	/**
	 * Creates a {@link Filter} editor
	 * 
	 * @param parent the parent composite
	 * @param featureType the feature type
	 * @param filter the initial filter
	 */
	public FilterEditor(Composite parent, FeatureType featureType, Filter filter) {
		super();
		
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
		propertySelect = new ComboViewer(page);
		propertySelect.getControl().setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		
		Collection<PropertyDescriptor> featureProperties = featureType.getDescriptors();
		for (PropertyDescriptor property : featureProperties) {
			//TODO filter properties?
			String name = property.getName().getLocalPart();
			properties.put(name, property);
			propertySelect.add(name);
		}
		
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
				FilterIdentifier id = (FilterIdentifier) ((IStructuredSelection) filterSelect.getSelection()).getFirstElement();
				
				String propertyName = ((IStructuredSelection) propertySelect.getSelection()).getFirstElement().toString();
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
				protected void visitFilter(FilterIdentifier id, String propertyName,
						String value) {
					// set property name
					propertySelect.setSelection(new StructuredSelection(propertyName));
					
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
