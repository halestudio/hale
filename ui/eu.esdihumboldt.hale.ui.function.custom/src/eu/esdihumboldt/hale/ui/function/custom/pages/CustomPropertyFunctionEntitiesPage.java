/*
 * Copyright (c) 2015 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.function.custom.pages;

import java.util.Collections;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

import eu.esdihumboldt.hale.common.align.custom.DefaultCustomPropertyFunction;
import eu.esdihumboldt.hale.common.align.custom.DefaultCustomPropertyFunctionEntity;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.function.custom.CustomPropertyFunctionWizard;
import eu.esdihumboldt.hale.ui.function.custom.pages.internal.CustomPropertyFunctionEntityEditor;
import eu.esdihumboldt.hale.ui.function.custom.pages.internal.CustomPropertyFunctionEntityList;
import eu.esdihumboldt.hale.ui.util.components.DynamicScrolledComposite;

/**
 * Page that allows assigning cell entities
 * 
 * @author Simon Templer
 */
public class CustomPropertyFunctionEntitiesPage extends
		HaleWizardPage<CustomPropertyFunctionWizard> implements CustomFunctionWizardPage {

	private CustomPropertyFunctionEntityEditor target;
	private CustomPropertyFunctionEntityList sources;

//	private final Observer fieldObserver;

	/**
	 * Create the entities page
	 */
	public CustomPropertyFunctionEntitiesPage() {
		super("entities");

		setTitle("Define input and output");
		setDescription("Specify input and output variables");

//		fieldObserver = new Observer() {
//
//			@Override
//			public void update(Observable o, Object arg) {
//				updateState();
//			}
//		};
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
	 * Create the header control.
	 * 
	 * @param parent the parent composite
	 * @return the header control or <code>null</code>
	 */
	protected Control createHeader(Composite parent) {
		return null;
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
			sources = new CustomPropertyFunctionEntityList(null, null, main,
					Collections.<DefaultCustomPropertyFunctionEntity> emptyList());
			break;
		case TARGET:
			main.setText("Target");
			target = new CustomPropertyFunctionEntityEditor(main);
			break;
		}

		// create fields
//		for (D field : fields) {
//			F functionField = createField(ssid, field, main);
//			if (functionField != null) {
//				functionFields.add(functionField);
//				functionField.addObserver(fieldObserver);
//			}
//		}

		return holder;
	}

//	/**
//	 * @see FunctionWizardPage#configureCell(MutableCell)
//	 */
//	@Override
//	public void configureCell(MutableCell cell) {
//		ListMultimap<String, Entity> source = ArrayListMultimap.create();
//		ListMultimap<String, Entity> target = ArrayListMultimap.create();
//
//		// collect entities from fields
//		for (F field : functionFields) {
//			switch (field.getSchemaSpace()) {
//			case SOURCE:
//				field.fillEntities(source);
//				break;
//			case TARGET:
//				field.fillEntities(target);
//				break;
//			default:
//				throw new IllegalStateException("Illegal schema space");
//			}
//		}
//
//		cell.setSource(source);
//		cell.setTarget(target);
//	}

	/**
	 * Update the page complete state
	 */
	private void updateState() {
//		boolean complete = true;
//		for (Field<?, ?> field : functionFields) {
//			if (!field.isValid()) {
//				complete = false;
//				break;
//			}
//		}
//
//		setPageComplete(complete);

		apply();

		setPageComplete(true);
	}

	@Override
	public void apply() {
		DefaultCustomPropertyFunction cf = getWizard().getResultFunction();
		if (cf != null && sources != null && target != null) {
			cf.setSources(sources.getValues());
			cf.setTarget(target.getValue());
		}
	}

}
