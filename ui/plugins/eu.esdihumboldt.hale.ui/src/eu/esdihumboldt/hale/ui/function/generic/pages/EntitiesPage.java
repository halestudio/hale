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

package eu.esdihumboldt.hale.ui.function.generic.pages;

import java.util.Collections;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.extension.function.FunctionDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.ParameterDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.PropertyParameter;
import eu.esdihumboldt.hale.common.align.extension.function.TypeParameter;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.function.generic.AbstractGenericFunctionWizard;
import eu.esdihumboldt.hale.ui.function.generic.pages.internal.Field;
import eu.esdihumboldt.hale.ui.selection.SchemaSelection;
import eu.esdihumboldt.hale.ui.util.components.DynamicScrolledComposite;

/**
 * Page that allows assigning cell entities
 * 
 * @param <T> the function type
 * @param <F> the field type
 * @param <D> the field definition
 * @author Simon Templer
 */
public abstract class EntitiesPage<T extends FunctionDefinition<D>, D extends ParameterDefinition, F extends Field<D, ?>>
		extends HaleWizardPage<AbstractGenericFunctionWizard<D, T>> implements FunctionWizardPage {

	private final Cell initialCell;
	private final SchemaSelection initialSelection;

	private final Set<EntityDefinition> sourceCandidates = new HashSet<EntityDefinition>();
	private final Set<EntityDefinition> targetCandidates = new HashSet<EntityDefinition>();

	private final Set<F> functionFields = new HashSet<F>();

	private final Observer fieldObserver;

	/**
	 * Create the entities page
	 * 
	 * @param initialSelection the initial schema selection, may be
	 *            <code>null</code>
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
		this.initialSelection = initialSelection;

		// fill candidates
		if (initialSelection != null) {
			for (EntityDefinition candidate : initialSelection.getSourceItems()) {
				if (acceptCandidate(candidate)) {
					sourceCandidates.add(candidate);
				}
			}
			for (EntityDefinition candidate : initialSelection.getTargetItems()) {
				if (acceptCandidate(candidate)) {
					targetCandidates.add(candidate);
				}
			}
		}
		if (initialCell != null) {
			if (initialCell.getSource() != null) {
				for (Entity entity : initialCell.getSource().values()) {
					sourceCandidates.add(entity.getDefinition());
				}
			}
			for (Entity entity : initialCell.getTarget().values()) {
				targetCandidates.add(entity.getDefinition());
			}
		}
	}

	/**
	 * Determines if a candidate from a selection should be accepted. This
	 * implementation returns <code>true</code> and therefore accepts any
	 * {@link EntityDefinition}, override to change behavior.
	 * 
	 * @param candidate the candidate
	 * @return if the candidate should be accepted
	 */
	protected boolean acceptCandidate(EntityDefinition candidate) {
		return true;
	}

	/**
	 * @see HaleWizardPage#createContent(Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		page.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).equalWidth(true).margins(0, 0)
				.create());

		Control header = createHeader(page);
		if (header != null) {
			header.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BEGINNING)
					.grab(true, false).span(2, 1).create());
		}

		Control source = createEntityGroup(SchemaSpaceID.SOURCE, page);
		source.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Control target = createEntityGroup(SchemaSpaceID.TARGET, page);
		target.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		updateState();
	}

	/**
	 * @see HaleWizardPage#onShowPage(boolean)
	 */
	@Override
	protected void onShowPage(boolean firstShow) {
		super.onShowPage(firstShow);

		if (firstShow) {
			// redraw to prevent ghost images drawn by ControlDecoration
			getControl().getParent().redraw();

			/*
			 * Re-layout the wizard dialog as the buttons may be hidden when
			 * using the NewRelationWizard.
			 */
//			for (Control control : getWizard().getShell().getChildren()) {
//				if (control instanceof Composite) {
//					((Composite) control).layout(true, true);
//				}
//			}
			getWizard().getShell().layout(true, true);
		}
	}

	/**
	 * @return the initial cell
	 */
	protected Cell getInitialCell() {
		return initialCell;
	}

	/**
	 * @return the initial selection
	 */
	protected SchemaSelection getInitialSelection() {
		return initialSelection;
	}

	/**
	 * Get the function fields associated with the page
	 * 
	 * @return the function fields
	 */
	protected Set<F> getFunctionFields() {
		return Collections.unmodifiableSet(functionFields);
	}

	/**
	 * Create the header control.
	 * 
	 * @param parent the parent composite
	 * @return the header control or <code>null</code>
	 */
	protected Control createHeader(Composite parent) {
		return null;
	}

	/**
	 * Get the entity candidates for the given schema space
	 * 
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
	 * 
	 * @param ssid the schema space id
	 * @param parent the parent composite
	 * @return the main group control
	 */
	protected Control createEntityGroup(SchemaSpaceID ssid, Composite parent) {
		// return another Composite, since the returned Control's layoutData are
		// overwritten.
		Composite holder = new Composite(parent, SWT.NONE);
		holder.setLayout(GridLayoutFactory.fillDefaults().create());

		// Important: Field does rely on DynamicScrolledComposite to be the
		// parent of its parent,
		// because sadly layout(true, true) on the Shell does not seem to
		// propagate to this place.
		ScrolledComposite sc = new DynamicScrolledComposite(holder, SWT.V_SCROLL);
		sc.setExpandHorizontal(true);
		sc.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).hint(200, 200).create());

		Group main = new Group(sc, SWT.NONE);
		sc.setContent(main);
		main.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).margins(10, 5).create());

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
		final Set<? extends D> fields;
		switch (ssid) {
		case SOURCE:
			fields = function.getSource();
			break;
		case TARGET:
			fields = function.getTarget();
			break;
		default:
			fields = new HashSet<D>();
		}

		// create fields
		for (D field : fields) {
			F functionField = createField(ssid, field, main);
			if (functionField != null) {
				functionFields.add(functionField);
				functionField.addObserver(fieldObserver);
			}
		}

		return holder;
	}

	/**
	 * Create entity assignment fields for the given field definition
	 * 
	 * @param ssid the schema space identifier
	 * @param field the field definition, may be a {@link PropertyParameter} or
	 *            a {@link TypeParameter}
	 * @param parent the parent composite
	 * @return the created field or <code>null</code>
	 */
	private F createField(SchemaSpaceID ssid, D field, Composite parent) {
		if (field.getMaxOccurrence() == 0) {
			return null;
		}

		return createField(field, ssid, parent, getCandidates(ssid), initialCell);
	}

	/**
	 * Create entity assignment fields for the given field definition
	 * 
	 * @param ssid the schema space identifier
	 * @param field the field definition, may be a {@link PropertyParameter} or
	 *            a {@link TypeParameter}
	 * @param parent the parent composite
	 * @param candidates the entity candidates
	 * @param initialCell the initial cell
	 * @return the created field or <code>null</code>
	 */
	protected abstract F createField(D field, SchemaSpaceID ssid, Composite parent,
			Set<EntityDefinition> candidates, Cell initialCell);

	/**
	 * @see FunctionWizardPage#configureCell(MutableCell)
	 */
	@Override
	public void configureCell(MutableCell cell) {
		ListMultimap<String, Entity> source = ArrayListMultimap.create();
		ListMultimap<String, Entity> target = ArrayListMultimap.create();

		// collect entities from fields
		for (F field : functionFields) {
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
		for (Field<?, ?> field : functionFields) {
			if (!field.isValid()) {
				complete = false;
				break;
			}
		}

		setPageComplete(complete);
	}

}
