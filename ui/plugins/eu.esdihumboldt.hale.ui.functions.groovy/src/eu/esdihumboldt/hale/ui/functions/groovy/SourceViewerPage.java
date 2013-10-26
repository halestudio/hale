/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.functions.groovy;

import java.util.List;
import java.util.Set;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.LineNumberRulerColumn;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.extension.function.FunctionParameter;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.core.io.Text;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.function.generic.AbstractGenericFunctionWizard;
import eu.esdihumboldt.hale.ui.function.generic.pages.ParameterPage;
import eu.esdihumboldt.hale.ui.util.source.SourceViewerUndoSupport;

/**
 * Generic parameter page based on a source viewer.
 * 
 * @author Simon Templer
 */
public class SourceViewerPage extends HaleWizardPage<AbstractGenericFunctionWizard<?, ?>> implements
		ParameterPage {

	private SourceViewer viewer;
	private final String parameterName;
	private String initialValue = "";

	/**
	 * Constructor.
	 * 
	 * @param pageName the page name
	 * @param parameterName the name of the parameter for the source viewer
	 *            content
	 * @param defaultValue the default value
	 */
	public SourceViewerPage(String pageName, String parameterName, String defaultValue) {
		super(pageName);
		this.parameterName = parameterName;
		this.initialValue = defaultValue;
	}

	@Override
	public void setParameter(Set<FunctionParameter> params,
			ListMultimap<String, ParameterValue> initialValues) {
		if (initialValues != null) {
			List<ParameterValue> val = initialValues.get(parameterName);
			if (val != null && !val.isEmpty()) {
				// try text value
				Text text = val.get(0).as(Text.class);
				if (text != null && text.getText() != null) {
					initialValue = text.getText();
				}
				else {
					// try string value
					initialValue = val.get(0).as(String.class, initialValue);
				}
			}
		}
	}

	@Override
	public ListMultimap<String, ParameterValue> getConfiguration() {
		ListMultimap<String, ParameterValue> result = ArrayListMultimap.create();
		// store as complex Text value
		result.put(parameterName, new ParameterValue(Value.complex(new Text(getDocument().get()))));
		return result;
	}

	@Override
	protected void createContent(Composite page) {
		// init editor
		IVerticalRuler ruler = createRuler();
		viewer = new SourceViewer(page, ruler, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.getTextWidget().setFont(JFaceResources.getTextFont());

		configure(viewer);

		SourceViewerUndoSupport.install(viewer);

		viewer.getDocument().set(initialValue);
	}

	/**
	 * Create the vertical ruler for the source viewer.
	 * 
	 * @return the vertical ruler
	 */
	protected IVerticalRuler createRuler() {
		final Display display = Display.getCurrent();
		CompositeRuler ruler = new CompositeRuler(3);
		LineNumberRulerColumn lineNumbers = new LineNumberRulerColumn();
		lineNumbers.setBackground(display.getSystemColor(SWT.COLOR_GRAY)); // SWT.COLOR_INFO_BACKGROUND));
		lineNumbers.setForeground(display.getSystemColor(SWT.COLOR_BLACK)); // SWT.COLOR_INFO_FOREGROUND));
		lineNumbers.setFont(JFaceResources.getTextFont());
		ruler.addDecorator(0, lineNumbers);
		return ruler;
	}

	/**
	 * Configure the source viewer. Here the configuration and the document are
	 * set.
	 * 
	 * @param viewer the viewer
	 */
	protected void configure(SourceViewer viewer) {
		SourceViewerConfiguration conf = createConfiguration();
		viewer.configure(conf);

		createAndSetDocument(viewer);

		viewer.getDocument().addDocumentListener(new IDocumentListener() {

			@Override
			public void documentChanged(DocumentEvent event) {
				updateState(event.getDocument());
			}

			@Override
			public void documentAboutToBeChanged(DocumentEvent event) {
				// ignore
			}
		});

		updateState(viewer.getDocument());
	}

	/**
	 * Get the source viewer document.
	 * 
	 * @return the document
	 */
	protected IDocument getDocument() {
		return viewer.getDocument();
	}

	/**
	 * Update the page state.
	 * 
	 * @param document the current document
	 */
	protected void updateState(IDocument document) {
		boolean valid = validate(document);

		setPageComplete(valid);
	}

	/**
	 * Validate the given document. The default implementation always returns
	 * <code>true</code>.
	 * 
	 * @param document the document to validate
	 * @return if the document is valid
	 */
	protected boolean validate(IDocument document) {
		return true;
	}

	/**
	 * Create the initial document and set it for the viewer.
	 * 
	 * @param viewer the source viewer
	 */
	protected void createAndSetDocument(SourceViewer viewer) {
		IDocument doc = new Document();
		doc.set(""); //$NON-NLS-1$

		viewer.setDocument(doc);
	}

	/**
	 * Create the source viewer configuration.
	 * 
	 * @return the source viewer configuration
	 */
	protected SourceViewerConfiguration createConfiguration() {
		return new SourceViewerConfiguration();
	}

}
