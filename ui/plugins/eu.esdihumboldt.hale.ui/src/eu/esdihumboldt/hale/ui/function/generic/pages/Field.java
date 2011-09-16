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

package eu.esdihumboldt.hale.ui.function.generic.pages;

import java.util.Collections;
import java.util.HashSet;
import java.util.Observable;
import java.util.Set;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.align.extension.function.AbstractParameter;
import eu.esdihumboldt.hale.align.model.Cell;
import eu.esdihumboldt.hale.align.model.Entity;
import eu.esdihumboldt.hale.align.model.EntityDefinition;
import eu.esdihumboldt.hale.ui.service.schema.SchemaSpaceID;

/**
 * Represents named entities in a function
 * @param <S> the entity selector type
 * @author Simon Templer
 */
public abstract class Field<S extends EntitySelector> extends Observable {

	private final AbstractParameter definition;
	private final SchemaSpaceID ssid;
	
	private final Set<S> selectors = new HashSet<S>();
	private final Composite selectorContainer;
	private final ISelectionChangedListener selectionChangedListener;
	
	private boolean valid = false;

	/**
	 * Create a field
	 * @param definition the field definition
	 * @param ssid the schema space
	 * @param parent the parent composite
	 * @param candidates the entity candidates
	 * @param initialCell the initial cell
	 */
	public Field(AbstractParameter definition, SchemaSpaceID ssid, 
			Composite parent, Set<EntityDefinition> candidates,
			Cell initialCell) {
		super();
		
		this.definition = definition;
		this.ssid = ssid;
		
		// field name
		if (definition.getName() != null && !definition.getName().isEmpty()) {
			Label name = new Label(parent, SWT.NONE);
			name.setText(definition.getName());
			name.setLayoutData(GridDataFactory.swtDefaults().create());
		}
		
		selectorContainer = new Composite(parent, SWT.NONE);
		selectorContainer.setLayoutData(GridDataFactory.fillDefaults().
				grab(true, false).create());
		selectorContainer.setLayout(GridLayoutFactory.fillDefaults().create());
		
		selectionChangedListener = new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updateState();
			}
		};
		
		// mandatory fields
		S selector = createEntitySelector(ssid, 
				candidates, definition, selectorContainer);
		selector.getControl().setLayoutData(GridDataFactory.swtDefaults().
				align(SWT.FILL, SWT.CENTER).grab(true, false).create());
		addSelector(selector);
		
		//TODO conditions/filters etc. ?!
		
		//TODO optional fields (+)
		
		if (initialCell != null) {
			//TODO populate with entities from cell
		}
		else {
			//TODO automatic population for simple cases?
		}
		
		updateState();
	}
	
	/**
	 * Create an entity selector
	 * @param ssid the schema space
	 * @param candidates the entity candidates
	 * @param field the field definition
	 * @param parent the parent composite
	 * @return the entity selector
	 */
	protected abstract S createEntitySelector(SchemaSpaceID ssid,
			Set<EntityDefinition> candidates, AbstractParameter field, 
			Composite parent);
	
	/**
	 * Get the selectors associated with the field
	 * @return the selectors
	 */
	protected Set<S> getSelectors() {
		return Collections.unmodifiableSet(selectors);
	}

	/**
	 * Add a selector
	 * @param selector the entity selector to add
	 */
	protected void addSelector(S selector) {
		selectors.add(selector);
		selector.getViewer().addSelectionChangedListener(selectionChangedListener);
	}
	
	/**
	 * Remove a selector
	 * @param selector the entity selector to remove
	 */
	protected void removeSelector(S selector) {
		//TODO remove listener
		//TODO remove from set
		//TODO remove from composite (dispose)
		//TODO relayout
	}

	/**
	 * Get the schema space
	 * @return the schema space
	 */
	public SchemaSpaceID getSchemaSpace() {
		return ssid;
	}
	
	/**
	 * Updates the valid state
	 */
	private void updateState() {
		boolean newValid = true;
		
		//valid if no selection is empty
		//TODO improve
		for (EntitySelector selector : selectors) {
			if (selector.getViewer().getSelection().isEmpty()) {
				newValid = false;
				break;
			}
		}
		
		boolean change = newValid != valid;
		valid = newValid;
		
		if (change) {
			setChanged();
			notifyObservers();
		}
	}
	
	/**
	 * Determines if the field is valid in its current configuration
	 * @return if the field is valid
	 */
	public boolean isValid() {
		return valid;
	}

	/**
	 * Fill the given map with the field's entities
	 * @param target the map to add the entities to
	 */
	public void fillEntities(ListMultimap<String, Entity> target) {
		for (S selector : selectors) {
			Entity entity = selector.getEntity();
			if (entity != null) {
				target.put(definition.getName(), entity);
			}
		}
	}

}
