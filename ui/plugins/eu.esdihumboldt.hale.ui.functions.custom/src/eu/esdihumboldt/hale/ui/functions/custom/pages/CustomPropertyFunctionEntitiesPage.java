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

package eu.esdihumboldt.hale.ui.functions.custom.pages;

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
import eu.esdihumboldt.hale.ui.functions.custom.CustomPropertyFunctionWizard;
import eu.esdihumboldt.hale.ui.functions.custom.pages.internal.BindingOrType;
import eu.esdihumboldt.hale.ui.functions.custom.pages.internal.BindingOrTypeEditor;
import eu.esdihumboldt.hale.ui.functions.custom.pages.internal.CustomPropertyFunctionEntityList;
import eu.esdihumboldt.hale.ui.util.components.DynamicScrolledComposite;

/**
 * Page that allows assigning cell entities
 * 
 * @author Simon Templer
 */
public class CustomPropertyFunctionEntitiesPage extends HaleWizardPage<CustomPropertyFunctionWizard>
		implements CustomFunctionWizardPage {

	private BindingOrTypeEditor target;
	private CustomPropertyFunctionEntityList sources;

//	private final Observer fieldObserver;

	/**
	 * Create the entities page
	 */
	public CustomPropertyFunctionEntitiesPage() {
		super("entities");

		setTitle("Define input and output");
		setDescription("Specify input variables and output type");

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
		sc.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).hint(300, 400).create());

		Group main = new Group(sc, SWT.NONE);
		sc.setContent(main);
		main.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).margins(10, 5).create());

		// load from initial function
		DefaultCustomPropertyFunction cf = getWizard().getResultFunction();

		// set group title
		switch (ssid) {
		case SOURCE:
			main.setText("Input variables");
			sources = new CustomPropertyFunctionEntityList(null, null, main, cf.getSources());
			break;
		case TARGET:
			main.setText("Output");
			target = new BindingOrTypeEditor(main, SchemaSpaceID.TARGET);
			GridDataFactory.fillDefaults().grab(true, false).applyTo(target.getControl());

			if (cf.getTarget() != null) {
				BindingOrType bot = new BindingOrType();

				bot.setType(cf.getTarget().getBindingType());
				bot.setBinding(cf.getTarget().getBindingClass());
				bot.setUseBinding(cf.getTarget().getBindingType() == null);

				target.setValue(bot);
			}

			break;
		}

		return holder;
	}

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
			cf.setTarget(createTargetEntity(target.getValue()));
		}
	}

	private DefaultCustomPropertyFunctionEntity createTargetEntity(BindingOrType value) {
		DefaultCustomPropertyFunctionEntity result = new DefaultCustomPropertyFunctionEntity();

		result.setMinOccurrence(1);
		result.setMaxOccurrence(1);
		result.setEager(false); // not applicable for target

		if (value.isUseBinding()) {
			result.setBindingType(null);
			result.setBindingClass(value.getBinding());
		}
		else {
			result.setBindingClass(null);
			result.setBindingType(value.getType());
		}

		return result;
	}

}
