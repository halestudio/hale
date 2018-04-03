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

package eu.esdihumboldt.hale.ui.filter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.ui.common.CommonSharedImages;
import eu.esdihumboldt.hale.ui.filter.internal.FilterUIPlugin;
import eu.esdihumboldt.hale.ui.filter.internal.Messages;

/**
 * Field for editing a type filter.
 * 
 * @author Simon Templer
 * @author Sebastian Reinhardt
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class TypeFilterField extends Composite {

	/**
	 * This specified how much time (in milliseconds) needs to pass since the
	 * last change to the field until the filter is updated.
	 */
	private static final int UPDATE_DELAY = 1000;

	/**
	 * Property name of the filter property (as used in
	 * {@link PropertyChangeEvent}s)
	 */
	public static final String PROPERTY_FILTER = "filter";

	/**
	 * Property name of the valid property (as used in
	 * {@link PropertyChangeEvent}s)
	 */
	public static final String PROPERTY_VALID = "valid";

	/**
	 * The supported filter types.
	 */
	public static enum FilterType {
		/** CQL filter */
		CQL,
		/** Extended CQL filter */
		ECQL
	}

	private final Text filterText;
	// private final Button openForm;
	private final Button insertVar;
	private final Button clearFilter;
	private final ControlDecoration decoration;

	private boolean valid = false;
	private Filter filter;

	private final Image insertVarImage;
	private final Image openFormImage;
	private final Image clearFilterImage;

	private Timer currentTimer = null;

	private final Set<PropertyChangeListener> listeners = new HashSet<PropertyChangeListener>();

	/**
	 * Create a new filter field for a given type.
	 * 
	 * @param parent the parent composite
	 * @param style the composite style
	 */
	public TypeFilterField(Composite parent, int style) {
		super(parent, style);

		GridLayout layout = new GridLayout(4, false);
		layout.horizontalSpacing = 2;
		layout.verticalSpacing = 0;

		layout.marginWidth = 0;
		layout.marginHeight = 0;
		setLayout(layout);

		// images
		insertVarImage = FilterUIPlugin.getImageDescriptor("icons/insert.gif").createImage(); //$NON-NLS-1$
		openFormImage = FilterUIPlugin.getImageDescriptor("icons/form.gif").createImage(); //$NON-NLS-1$

		clearFilterImage = CommonSharedImages.getImageRegistry().get(CommonSharedImages.IMG_REMOVE);

		// create components

		Composite filterComp = new Composite(this, SWT.NONE);
		filterComp.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		filterComp.setLayout(GridLayoutFactory.fillDefaults().extendedMargins(0, 6, 0, 0).create());

		// text field
		filterText = new Text(filterComp, SWT.SINGLE | SWT.BORDER);
		filterText.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		filterText.setToolTipText(Messages.FeatureFilterField_3); // $NON-NLS-1$
		filterText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				synchronized (this) {
					if (currentTimer != null) {
						currentTimer.cancel();
					}

					final Display display = PlatformUI.getWorkbench().getDisplay();

					currentTimer = new Timer();
					currentTimer.schedule(new TimerTask() {

						@Override
						public void run() {
							display.syncExec(new Runnable() {

								@Override
								public void run() {
									BusyIndicator.showWhile(display, new Runnable() {

										@Override
										public void run() {
											updateFilter();
										}
									});
								}
							});
						}
					}, UPDATE_DELAY);
				}
			}
		});
		PlatformUI.getWorkbench().getHelpSystem().setHelp(filterText,
				"eu.esdihumboldt.hale.doc.user.filter_field");

		decoration = new ControlDecoration(filterText, SWT.RIGHT | SWT.TOP);

		showDefaultDecoration();

		// clear filter
		clearFilter = new Button(this, SWT.PUSH);
		clearFilter.setEnabled(false);
		clearFilter.setImage(clearFilterImage);
		clearFilter.setToolTipText(Messages.FeatureFilterField_0); // $NON-NLS-1$
		clearFilter.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		clearFilter.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				filterText.setText(""); //$NON-NLS-1$
				clearFilter.setEnabled(false);
				updateFilter();
			}

		});

		// insert variable
		insertVar = new Button(this, SWT.PUSH);
		insertVar.setImage(insertVarImage);
		insertVar.setToolTipText(Messages.FeatureFilterField_6); // $NON-NLS-1$
		insertVar.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		insertVar.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				String var = selectVariable();
				if (var != null) {
					filterText.insert(var);
					filterText.setFocus();
				}
			}

		});

		// open form
//		openForm = new Button(this, SWT.PUSH);
//		
//		openForm.setImage(openFormImage);
//		openForm.setToolTipText(Messages.FeatureFilterField_9); //$NON-NLS-1$
//		openForm.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
//		openForm.addSelectionListener(new SelectionAdapter() {
//
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				FeatureFilterFormDialog dialog = new FeatureFilterFormDialog(
//						Display.getCurrent().getActiveShell(), FeatureFilterField.this.type.getFeatureType());
//				if (dialog.open() == FeatureFilterFormDialog.OK) {
//					String filter = dialog.getFilterExpression();
//					setFilterExpression(filter);
//				}
//			}
//			
//		});
//		
//		setType(type);

		updateFilter();
	}

	/**
	 * Returns a string to insert to the filter that the user selected.
	 * 
	 * @return a string to insert to the filter or <code>null</code>
	 */
	protected abstract String selectVariable();

	/**
	 * Creates a {@link Filter} from the given filter string.<br>
	 * If the string is no valid input, any exception may be thrown.
	 * 
	 * @param filterString the filter string
	 * @return a filter representation of the filter string
	 * @throws Exception if creation of the filter fails
	 */
	protected abstract Filter createFilter(String filterString) throws Exception;

	/**
	 * Update the filter and valid properties.
	 */
	protected void updateFilter() {
		Filter lastFilter = filter;
		boolean lastValid = valid;
		String filterString = filterText.getText();

		boolean filterPresent = filterString != null && !filterString.isEmpty();
		if (filterPresent) {
			clearFilter.setEnabled(true);
			try {
				filter = createFilter(filterString);
				valid = true;
				showDefaultDecoration();
			} catch (Throwable e) {
				// show error decoration
				decoration.setImage(FieldDecorationRegistry.getDefault()
						.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR).getImage());
				decoration.setDescriptionText(e.getMessage());
				decoration.show();
				// mark as invalid
				filter = null;
				valid = false;
			}
		}
		else {
			clearFilter.setEnabled(false);
			filter = null;
			valid = false;
			showDefaultDecoration();
		}

		// fire events
		if (lastValid != valid) {
			notifyListeners(new PropertyChangeEvent(this, PROPERTY_VALID, lastValid, valid));
		}
		notifyListeners(new PropertyChangeEvent(this, PROPERTY_FILTER, lastFilter, filter));
	}

	/**
	 * Enables/Disables the button for variable selection.
	 * 
	 * @param enabled the enable status
	 */
	protected final void setVariableSelectEnabled(boolean enabled) {
		insertVar.setEnabled(enabled);
	}

	/**
	 * Get the filter expression.
	 * 
	 * @return the filter expression
	 */
	public String getFilterExpression() {
		return filterText.getText();
	}

	/**
	 * Get the filter.
	 * 
	 * @return the filter or <code>null</code>
	 */
	public Filter getFilter() {
		return filter;
	}

	/**
	 * States if the current filter expression is valid and
	 * 
	 * @return the valid
	 */
	public boolean isValid() {
		return valid;
	}

	/**
	 * Set the filter expression.
	 * 
	 * @param filterExpression the filter expression
	 */
	public void setFilterExpression(String filterExpression) {
		filterText.setText(filterExpression);

		updateFilter();
	}

	/**
	 * @see Widget#dispose()
	 */
	@Override
	public void dispose() {
		openFormImage.dispose();
		insertVarImage.dispose();

		super.dispose();
	}

	/**
	 * Add a filter listener
	 * 
	 * @param listener the filter listener
	 */
	public void addListener(PropertyChangeListener listener) {
		listeners.add(listener);
	}

	/**
	 * Remove a filter listener
	 * 
	 * @param listener the filter listener
	 */
	public void removeListener(PropertyChangeListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Notify the listeners of a property change.
	 * 
	 * @param evt the property change event
	 */
	protected void notifyListeners(PropertyChangeEvent evt) {
		for (PropertyChangeListener listener : listeners) {
			listener.propertyChange(evt);
		}
	}

	private void showDefaultDecoration() {
		decoration.setImage(FieldDecorationRegistry.getDefault()
				.getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION).getImage());
		decoration.setDescriptionText("for Example: \"id\" = '1'");
		decoration.show();
	}

}
