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

import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.align.extension.function.AbstractFunction;
import eu.esdihumboldt.hale.align.extension.function.AbstractParameter;
import eu.esdihumboldt.hale.align.extension.function.PropertyParameter;
import eu.esdihumboldt.hale.align.extension.function.TypeParameter;
import eu.esdihumboldt.hale.align.model.Cell;
import eu.esdihumboldt.hale.align.model.Entity;
import eu.esdihumboldt.hale.align.model.EntityDefinition;
import eu.esdihumboldt.hale.align.model.MutableCell;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.function.generic.AbstractGenericFunctionWizard;
import eu.esdihumboldt.hale.ui.selection.SchemaSelection;
import eu.esdihumboldt.hale.ui.service.schema.SchemaSpaceID;

/**
 * Page that allows assigning cell entities
 * @param <T> the function type
 * @author Simon Templer
 */
public abstract class EntitiesPage<T extends AbstractFunction> extends HaleWizardPage<AbstractGenericFunctionWizard<T>>
		implements FunctionWizardPage {

	private final Cell initialCell;
	
	private final Set<EntityDefinition> sourceCandidates = new HashSet<EntityDefinition>();
	private final Set<EntityDefinition> targetCandidates = new HashSet<EntityDefinition>();
	
	private final Set<Field> functionFields = new HashSet<Field>();

	private final Observer fieldObserver;
	
	/**
	 * Create the entities page
	 * @param initialSelection the initial schema selection, may be <code>null</code>
	 * @param initialCell the initial cell, may be <code>null</code>
	 */
	public EntitiesPage(SchemaSelection initialSelection, Cell initialCell) {
		super("entities");
		
		setTitle("Entity selection");
		setDescription("Assign entities for the function");
		
		fieldObserver = new Observer() {
			@Override
			public void update(Observable o, Object arg) {
				updateState();
			}
		};
		
		this.initialCell = initialCell;
		
		// fill candidates
		if (initialSelection != null) {
			sourceCandidates.addAll(initialSelection.getSourceItems());
			targetCandidates.addAll(initialSelection.getTargetItems());
		}
		if (initialCell != null) {
			for (Entity entity : initialCell.getSource().values()) {
				sourceCandidates.add(entity.getDefinition());
			}
			for (Entity entity : initialCell.getTarget().values()) {
				targetCandidates.add(entity.getDefinition());
			}
		}
	}

	/**
	 * @see HaleWizardPage#createContent(Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		page.setLayout(new GridLayout(2, true));
		
		Control source = createEntityGroup(SchemaSpaceID.SOURCE, page);
		source.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		
		Control target = createEntityGroup(SchemaSpaceID.TARGET, page);
		target.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		
		updateState();
	}
	
	/**
	 * Get the entity candidates for the given schema space
	 * @param ssid the schema space ID
	 * @return the entity candidates
	 */
	protected Set<EntityDefinition> getCandidates(SchemaSpaceID ssid) {
		switch (ssid) {
		case SOURCE:
			return sourceCandidates;
		case TARGET:
			return targetCandidates;
		default:
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Create an entity group
	 * @param ssid the schema space id
	 * @param parent the parent composite
	 * @return the main group control
	 */
	protected Control createEntityGroup(SchemaSpaceID ssid, Composite parent) {
		Group main = new Group(parent, SWT.NONE);
		main.setLayout(new GridLayout(1, true));
		
		// set group title
		switch (ssid) {
		case SOURCE:
			main.setText("Source");
			break;
		case TARGET:
			main.setText("Target");
			break;
		}
		
		// determine fields
		T function = getWizard().getFunction();
		final Set<? extends AbstractParameter> fields;
		switch (ssid) {
		case SOURCE:
			fields = function.getSource();
			break;
		case TARGET:
			fields = function.getTarget();
			break;
		default:
			fields = new HashSet<AbstractParameter>();
		}
		
		// create fields
		for (AbstractParameter field : fields) {
			Field functionField = createField(ssid, field, main);
			if (functionField != null) {
				functionFields.add(functionField);
				functionField.addObserver(fieldObserver);
			}
		}
		
		return main;
	}

	/**
	 * Create entity assignment fields for the given field definition
	 * @param ssid the schema space identifier
	 * @param field the field definition, may be a {@link PropertyParameter}
	 *   or a {@link TypeParameter}
	 * @param parent the parent composite
	 * @return the created field or <code>null</code>
	 */
	private Field createField(SchemaSpaceID ssid, AbstractParameter field,
			Composite parent) {
		if (field.getMaxOccurrence() == 0) {
			return null;
		}
		
		return createField(field, ssid, parent, getCandidates(ssid), initialCell);
	}

	/**
	 * Create entity assignment fields for the given field definition
	 * @param ssid the schema space identifier
	 * @param field the field definition, may be a {@link PropertyParameter}
	 *   or a {@link TypeParameter}
	 * @param parent the parent composite
	 * @param candidates the entity candidates
	 * @param initialCell the initial cell
	 * @return the created field or <code>null</code>
	 */
	protected abstract Field createField(AbstractParameter field, SchemaSpaceID ssid,
			Composite parent, Set<EntityDefinition> candidates,
			Cell initialCell);

	/**
	 * @see FunctionWizardPage#configureCell(MutableCell)
	 */
	@Override
	public void configureCell(MutableCell cell) {
		ListMultimap<String, Entity> source = ArrayListMultimap.create();
		ListMultimap<String, Entity> target = ArrayListMultimap.create();
		
		// collect entities from fields
		for (Field field : functionFields) {
			switch (field.getSchemaSpace()) {
			case SOURCE:
				field.fillEntities(source);
				break;
			case TARGET:
				field.fillEntities(target);
				break;
			default:
				throw new IllegalStateException("Illegal schema space");
			}
		}
		
		cell.setSource(source);
		cell.setTarget(target);
	}
	
	/**
	 * Update the page complete state
	 */
	private void updateState() {
		boolean complete = true;
		for (Field field : functionFields) {
			if (!field.isValid()) {
				complete = false;
				break;
			}
		}
		
		setPageComplete(complete);
	}

}
