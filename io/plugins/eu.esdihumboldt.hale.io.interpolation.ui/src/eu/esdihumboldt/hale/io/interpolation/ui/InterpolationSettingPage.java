package eu.esdihumboldt.hale.io.interpolation.ui;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.instance.geometry.InterpolationHelper;
import eu.esdihumboldt.hale.ui.io.IOWizard;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;
import eu.esdihumboldt.util.geometry.interpolation.extension.InterpolationAlgorithmFactory;
import eu.esdihumboldt.util.geometry.interpolation.extension.InterpolationExtension;
import eu.esdihumboldt.util.geometry.interpolation.grid.GridInterpolation;

/**
 * Configuration page for specifying maximum position error for interpolation
 * 
 * @author Arun
 *
 */
public class InterpolationSettingPage
		extends AbstractConfigurationPage<IOProvider, IOWizard<IOProvider>> {

	private Text error;
	private Button moveToGrid;
	private ComboViewer algorithms;

	/**
	 * Default constructor
	 */
	public InterpolationSettingPage() {
		super("interpolation.basicSettings", "Interpolation", null);

		setDescription("Basic settings for interpolation of curve geometries");
	}

	@Override
	public void enable() {
		// do nothing

	}

	@Override
	public void disable() {
		// do nothing

	}

	@Override
	public boolean updateConfiguration(IOProvider provider) {
		if (!validate()) {
			setErrorMessage("value is not valid!");
			return false;
		}
		setErrorMessage("");

		// set interpolation algorithm
		Value alg = null;
		ISelection sel = algorithms.getSelection();
		if (!sel.isEmpty() && sel instanceof IStructuredSelection) {
			Object selected = ((IStructuredSelection) sel).getFirstElement();
			if (selected instanceof InterpolationAlgorithmFactory) {
				String id = ((InterpolationAlgorithmFactory) selected).getIdentifier();
				alg = Value.of(id);
			}
		}
		provider.setParameter(InterpolationHelper.PARAMETER_INTERPOLATION_ALGORITHM, alg);

		// set maximum positional error
		provider.setParameter(InterpolationHelper.PARAMETER_MAX_POSITION_ERROR,
				Value.of(error.getText()));

		// set move to grid flag
		provider.setParameter(GridInterpolation.PARAMETER_MOVE_ALL_TO_GRID,
				Value.of(moveToGrid.getSelection()));
		return true;
	}

	@Override
	protected void createContent(Composite page) {
		// page.setLayout(GridLayoutFactory.swtDefaults().numColumns(3).create());
		page.setLayout(new GridLayout(1, false));

		Group groupError = new Group(page, SWT.NONE);
		GridLayoutFactory.swtDefaults().numColumns(3).applyTo(groupError);
//		GridDataFactory.fillDefaults().grab(true, false).applyTo(groupError);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).applyTo(groupError);
		groupError.setText("Interpolated geometries");

		// Interpolation algorithm
		Label labelAlg = new Label(groupError, SWT.NONE);
		labelAlg.setText("Interpolation algorithm: ");
		labelAlg.setLayoutData(GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).create());

		algorithms = new ComboViewer(groupError, SWT.READ_ONLY);
		algorithms.setContentProvider(ArrayContentProvider.getInstance());
		algorithms.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof InterpolationAlgorithmFactory) {
					return ((InterpolationAlgorithmFactory) element).getDisplayName();
				}
				return super.getText(element);
			}

		});
		algorithms.getControl().setLayoutData(GridDataFactory.swtDefaults()
				.align(SWT.FILL, SWT.CENTER).grab(true, false).span(2, 1).create());
		algorithms.setInput(InterpolationExtension.getInstance().getFactories());
		algorithms.setSelection(new StructuredSelection(InterpolationExtension.getInstance()
				.getFactory(InterpolationHelper.DEFAULT_ALGORITHM)));

		// label error
		Label labelError = new Label(groupError, SWT.NONE);
		labelError.setText("Maximum Position Error: ");
		labelError.setLayoutData(GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).create());

		error = new Text(groupError, SWT.BORDER | SWT.SINGLE);
		error.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER)
				.grab(true, false).create());

		// label unit
		Label labelUnit = new Label(groupError, SWT.NONE);
		labelUnit.setText("(unit)");
		labelUnit.setLayoutData(GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).create());

		Label positionErrorDesc = new Label(groupError, SWT.NONE);
		positionErrorDesc.setText(
				"Supplied maximum positional error will be used to interpolate Arc and Circle geometries");
		GridDataFactory.fillDefaults().span(3, 1).applyTo(positionErrorDesc);

		Group group = new Group(page, SWT.NONE);
		group.setLayout(new GridLayout(1, false));
		group.setText("Other geometries");
		GridDataFactory.fillDefaults().span(3, 1).applyTo(group);

		moveToGrid = new Button(group, SWT.CHECK);
		moveToGrid.setText("Move all geometries to interpolation grid (if applicable)");
		GridDataFactory.fillDefaults().grab(true, false).applyTo(moveToGrid);
		// default
		moveToGrid.setSelection(false);

		Label desc = new Label(group, SWT.NONE);
		desc.setText(
				"Moves all geometries coordinates to the interpolation grid to allow for topological consistency with interpolated geometries.");
		GridDataFactory.fillDefaults().grab(true, false).applyTo(desc);

		// filler
		new Label(page, SWT.NONE);

		// label with warning message
		Composite warnComp = new Composite(page, SWT.NONE);
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(warnComp);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(warnComp);

		setPageComplete(false);
	}

	@Override
	protected void onShowPage(boolean firstShow) {
		if (firstShow) {
			loadPreValue(getWizard().getProvider());
			setPageComplete(true);
		}
	}

	private boolean validate() {
		String txt = error.getText();
		if (txt == null || txt.equals("")) {
			return false;
		}
		else {
			try {
				@SuppressWarnings("unused")
				double val = Double.parseDouble(txt);
				return true;
			} catch (NumberFormatException ex) {
				return false;
			}
		}
	}

	/**
	 * Load max position error value
	 * 
	 * @param provider the I/O provider to get
	 */
	private void loadPreValue(IOProvider provider) {
		// load interpolation algorithm
		String id = provider.getParameter(InterpolationHelper.PARAMETER_INTERPOLATION_ALGORITHM)
				.as(String.class, InterpolationHelper.DEFAULT_ALGORITHM);
		InterpolationAlgorithmFactory fact = InterpolationExtension.getInstance().getFactory(id);
		if (fact == null) {
			fact = InterpolationExtension.getInstance()
					.getFactory(InterpolationHelper.DEFAULT_ALGORITHM);
		}
		ISelection selection;
		if (fact == null) {
			selection = new StructuredSelection();
		}
		else {
			selection = new StructuredSelection(fact);
		}
		algorithms.setSelection(selection);

		// load maximum positional error
		Double value = provider.getParameter(InterpolationHelper.PARAMETER_MAX_POSITION_ERROR)
				.as(Double.class);
		if (value != null) {
			error.setText(Double.toString(value.doubleValue()));
		}
		else {
			error.setText(Double.toString(InterpolationHelper.DEFAULT_MAX_POSITION_ERROR));
		}

		// load move to grid flag
		moveToGrid.setSelection(provider.getParameter(GridInterpolation.PARAMETER_MOVE_ALL_TO_GRID)
				.as(Boolean.class, false));
	}
}
