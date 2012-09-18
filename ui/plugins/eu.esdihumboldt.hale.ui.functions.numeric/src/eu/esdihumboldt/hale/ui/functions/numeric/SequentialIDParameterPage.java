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

package eu.esdihumboldt.hale.ui.functions.numeric;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.cst.functions.numeric.sequentialid.SequentialIDConstants;
import eu.esdihumboldt.hale.common.align.extension.function.PropertyFunctionExtension;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.function.generic.pages.AbstractParameterPage;
import eu.esdihumboldt.hale.ui.util.viewer.EnumContentProvider;

/**
 * Parameter page for the sequential ID function.
 * 
 * @author Simon Templer
 */
public class SequentialIDParameterPage extends AbstractParameterPage implements
		SequentialIDConstants {

	private ComboViewer sequence;

	private Text prefix;

	private Text suffix;

	/**
	 * Default constructor.
	 */
	public SequentialIDParameterPage() {
		super(PropertyFunctionExtension.getInstance().get(ID),
				"Please configure the identifier generation");
	}

	/**
	 * @see AbstractParameterPage#getConfiguration()
	 */
	@Override
	public ListMultimap<String, String> getConfiguration() {
		ListMultimap<String, String> result = ArrayListMultimap.create(3, 1);

		if (sequence != null) {
			ISelection sel = sequence.getSelection();
			if (!sel.isEmpty() && sel instanceof IStructuredSelection) {
				result.put(PARAM_SEQUENCE,
						((Sequence) ((IStructuredSelection) sel).getFirstElement()).name());
			}
		}

		if (prefix != null) {
			result.put(PARAM_PREFIX, prefix.getText());
		}

		if (suffix != null) {
			result.put(PARAM_SUFFIX, suffix.getText());
		}

		return result;
	}

	/**
	 * @see HaleWizardPage#createContent(Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		page.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());

		Label label;
		GridDataFactory labelLayout = GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER);
		GridDataFactory controlLayout = GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER)
				.grab(true, false);

		// select sequence type
		if (getParametersToHandle().containsKey(PARAM_SEQUENCE)) {
			label = new Label(page, SWT.NONE);
			label.setText("Sequence");
			labelLayout.applyTo(label);

			sequence = new ComboViewer(page);
			sequence.setContentProvider(EnumContentProvider.getInstance());
			sequence.setLabelProvider(new LabelProvider() {

				@Override
				public String getText(Object element) {
					if (element instanceof Sequence) {
						switch ((Sequence) element) {
						case overall:
							return "Over all sequential IDs";
						case type:
							return "Per target instance type";
						}
					}

					return super.getText(element);
				}

			});
			sequence.setInput(Sequence.class);
			controlLayout.applyTo(sequence.getControl());

			Sequence initialValue = Sequence.valueOf(getOptionalInitialValue(PARAM_SEQUENCE,
					Sequence.type.name()));
			sequence.setSelection(new StructuredSelection(initialValue));
		}

		// specify prefix
		if (getParametersToHandle().containsKey(PARAM_PREFIX)) {
			label = new Label(page, SWT.NONE);
			label.setText("Prefix");
			labelLayout.applyTo(label);

			prefix = new Text(page, SWT.SINGLE | SWT.BORDER);
			controlLayout.applyTo(prefix);

			prefix.setText(getOptionalInitialValue(PARAM_PREFIX, ""));
		}

		// specify suffix
		if (getParametersToHandle().containsKey(PARAM_SUFFIX)) {
			label = new Label(page, SWT.NONE);
			label.setText("Suffix");
			labelLayout.applyTo(label);

			suffix = new Text(page, SWT.SINGLE | SWT.BORDER);
			controlLayout.applyTo(suffix);

			suffix.setText(getOptionalInitialValue(PARAM_SUFFIX, ""));
		}

		updateStatus();
	}

	/**
	 * Update the page status
	 */
	protected void updateStatus() {
		boolean complete = true;

		if (sequence != null && sequence.getSelection().isEmpty()) {
			complete = false;
		}

		setPageComplete(complete);
	}
}
