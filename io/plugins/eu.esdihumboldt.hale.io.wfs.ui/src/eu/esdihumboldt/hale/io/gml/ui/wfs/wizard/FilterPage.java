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

package eu.esdihumboldt.hale.io.gml.ui.wfs.wizard;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.LineNumberRulerColumn;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.hale.io.gml.ui.wfs.wizard.filter.OGCFilterDialog;
import eu.esdihumboldt.hale.io.wfs.ui.internal.Messages;

/**
 * 
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class FilterPage extends AbstractWfsPage<WfsGetFeatureConfiguration> {

	private final AbstractTypesPage<?> typesPage;

	/**
	 * Type selection combo
	 */
	private ComboViewer typeCombo;

	/**
	 * The filter editor
	 */
	private SourceViewer filterEditor;

	/**
	 * The edit button
	 */
	private Button editButton;

	/**
	 * The last selected feature type
	 */
	private FeatureType lastSelected;

	/**
	 * The filters
	 */
	private final Map<String, String> filters = new HashMap<String, String>();

	/**
	 * The current types
	 */
	private List<FeatureType> types;

	/**
	 * Constructor
	 * 
	 * @param configuration the WFS configuration
	 * @param typesPage the types page
	 */
	public FilterPage(WfsGetFeatureConfiguration configuration, AbstractTypesPage<?> typesPage) {
		super(configuration, Messages.FilterPage_0); //$NON-NLS-1$

		this.typesPage = typesPage;

		setTitle(Messages.FilterPage_1); //$NON-NLS-1$
		setMessage(Messages.FilterPage_2); //$NON-NLS-1$
	}

	/**
	 * @see AbstractWfsPage#onShowPage()
	 */
	@Override
	protected void onShowPage() {
		lastSelected = null;

		List<FeatureType> types = typesPage.getSelection();

		update(types);
	}

	/**
	 * Update the page with the given types
	 * 
	 * @param types the feature types
	 */
	private void update(List<FeatureType> types) {
		this.types = new ArrayList<FeatureType>(types);
		typeCombo.setInput(this.types);

		ISelection selection = (types.isEmpty()) ? (new StructuredSelection())
				: (new StructuredSelection(types.get(0)));
		typeCombo.setSelection(selection);
	}

	/**
	 * @see AbstractWfsPage#createContent(Composite)
	 */
	@Override
	protected void createContent(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);
		page.setLayout(new GridLayout(3, false));

		Label typeLabel = new Label(page, SWT.NONE);
		typeLabel.setText(Messages.FilterPage_3); //$NON-NLS-1$
		typeLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));

		// init combo
		typeCombo = new ComboViewer(page);
		typeCombo.getControl().setLayoutData(
				new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		typeCombo.setContentProvider(ArrayContentProvider.getInstance());
		typeCombo.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof FeatureType) {
					return ((FeatureType) element).getName().getLocalPart();
				}

				return super.getText(element);
			}

		});

		typeCombo.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				if (selection.isEmpty()) {
					updateEditor(null);
				}
				else {
					if (selection instanceof IStructuredSelection) {
						updateEditor((FeatureType) ((IStructuredSelection) selection)
								.getFirstElement());
					}
				}
			}

		});

		Label editorLabel = new Label(page, SWT.NONE);
		editorLabel.setText(Messages.FilterPage_4); //$NON-NLS-1$
		editorLabel.setLayoutData(new GridData(SWT.END, SWT.BEGINNING, false, false));

		// init editor
		final Display display = Display.getCurrent();
		CompositeRuler ruler = new CompositeRuler(3);
		LineNumberRulerColumn lineNumbers = new LineNumberRulerColumn();
		lineNumbers.setBackground(display.getSystemColor(SWT.COLOR_GRAY)); // SWT.COLOR_INFO_BACKGROUND));
		lineNumbers.setForeground(display.getSystemColor(SWT.COLOR_BLACK)); // SWT.COLOR_INFO_FOREGROUND));
		lineNumbers.setFont(JFaceResources.getTextFont());
		ruler.addDecorator(0, lineNumbers);

		filterEditor = new SourceViewer(page, ruler, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);
		filterEditor.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		filterEditor.getTextWidget().setFont(JFaceResources.getTextFont());
		SourceViewerConfiguration conf = new SourceViewerConfiguration();
		filterEditor.configure(conf);

		// create initial document
		IDocument doc = new Document();
		doc.set(""); //$NON-NLS-1$
		filterEditor.setInput(doc);

		// create edit button
		editButton = new Button(page, SWT.PUSH);
		editButton.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, false, false));
		editButton.setText(Messages.FilterPage_6); //$NON-NLS-1$
		editButton.setToolTipText(Messages.FilterPage_7); //$NON-NLS-1$
		editButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (lastSelected != null) {
					FeatureType type = lastSelected;

					OGCFilterDialog dialog = new OGCFilterDialog(display.getActiveShell(),
							MessageFormat.format(Messages.FilterPage_8, type.getName()
									.getLocalPart())); //$NON-NLS-1$
					dialog.setFeatureType(type);
					String filter = dialog.open();
					if (filter != null) {
						filterEditor.getDocument().set(filter);
					}
				}
			}

		});

		setControl(page);
	}

	/**
	 * Update the editor
	 * 
	 * @param selected the selected feature type
	 */
	protected void updateEditor(FeatureType selected) {
		saveCurrent();

		IDocument doc = filterEditor.getDocument();
		lastSelected = selected;

		if (selected != null) {
			// load filter for current selection
			String filter = getFilter(selected);
			doc.set(filter);
		}
	}

	/**
	 * @see AbstractWfsPage#onHidePage()
	 */
	@Override
	protected void onHidePage() {
		saveCurrent();
	}

	/**
	 * Save the filter that is currently edited
	 */
	protected void saveCurrent() {
		IDocument doc = filterEditor.getDocument();

		if (lastSelected != null) {
			// save filter for last selection
			setFilter(lastSelected, doc.get());
		}
	}

	/**
	 * Get the filter for the given feature type
	 * 
	 * @param type the feature type
	 * 
	 * @return the filter
	 */
	protected String getFilter(FeatureType type) {
		String key = getKey(type);

		String filter = filters.get(key);
		if (filter == null) {
			return createDefaultFilter(type);
		}
		else {
			return filter;
		}
	}

	/**
	 * Create a default filter
	 * 
	 * @param type the feature type to create the filter for
	 * 
	 * @return the filter
	 */
	protected String createDefaultFilter(FeatureType type) {
		// no filtering as default
		return ""; //$NON-NLS-1$
	}

	/**
	 * Set the filter for the given feature type
	 * 
	 * @param type the feature type
	 * @param filter the filter to set
	 */
	protected void setFilter(FeatureType type, String filter) {
		String key = getKey(type);

		filters.put(key, filter);

		// TODO validate filter(s)?
	}

	/**
	 * Get the key for the given feature type
	 * 
	 * @param type the feature type
	 * @return the key
	 */
	protected String getKey(FeatureType type) {
		return type.getName().getNamespaceURI() + "/" + type.getName().getLocalPart(); //$NON-NLS-1$
	}

	/**
	 * @see AbstractWfsPage#updateConfiguration(WfsConfiguration)
	 */
	@Override
	public boolean updateConfiguration(WfsGetFeatureConfiguration configuration) {
		saveCurrent();

		if (types != null) {
			List<String> filters = new ArrayList<String>();
			for (FeatureType type : types) {
				String filter = getFilter(type);

				filters.add(filter);
			}

			configuration.setFilters(filters);
		}

		return true;
	}

}
