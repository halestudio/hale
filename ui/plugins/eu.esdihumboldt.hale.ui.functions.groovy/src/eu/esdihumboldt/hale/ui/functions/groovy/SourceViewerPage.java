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
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.LineNumberRulerColumn;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.extension.function.FunctionParameterDefinition;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.core.io.Text;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.function.generic.pages.ParameterPage;
import eu.esdihumboldt.hale.ui.util.source.CompilingSourceViewer;
import eu.esdihumboldt.hale.ui.util.source.SourceCompiler;
import eu.esdihumboldt.hale.ui.util.source.SourceValidator;
import eu.esdihumboldt.hale.ui.util.source.SourceViewerKeyBindings;
import eu.esdihumboldt.hale.ui.util.source.SourceViewerPanel;
import eu.esdihumboldt.hale.ui.util.source.ValidatingSourceViewer;

/**
 * Generic parameter page based on a source viewer panel.
 * 
 * @param <C> the type of the compilation result, if applicable
 * @param <W> the wizard type
 * @author Simon Templer
 */
public class SourceViewerPage<C, W extends Wizard> extends HaleWizardPage<W> implements
		ParameterPage {

	private CompilingSourceViewer<C> viewer;
	private final String parameterName;
	private String initialValue = "";

	private final SourceCompiler<C> compiler;

	/**
	 * Constructor.
	 * 
	 * @param pageName the page name
	 * @param parameterName the name of the parameter for the source viewer
	 *            content
	 * @param defaultValue the default value
	 * @param compiler the source compiler, <code>null</code> to disable
	 *            compilation
	 */
	public SourceViewerPage(String pageName, String parameterName, String defaultValue,
			SourceCompiler<C> compiler) {
		super(pageName);
		this.parameterName = parameterName;
		this.initialValue = defaultValue;
		this.compiler = compiler;
	}

	@Override
	public void setParameter(Set<FunctionParameterDefinition> params,
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
		if (viewer != null) {
			// store as complex Text value
			result.put(parameterName,
					new ParameterValue(Value.complex(new Text(getDocument().get()))));
		}
		return result;
	}

	@Override
	protected void createContent(Composite page) {
		// init editor
		IVerticalRuler ruler = createVerticalRuler();
		IOverviewRuler overviewRuler = createOverviewRuler();
		SourceViewerPanel<C> panel = new SourceViewerPanel<C>(page, ruler, overviewRuler,
				new SourceValidator() {

					@Override
					public boolean validate(String content) {
						return SourceViewerPage.this.validate(content);
					}
				}, compiler);
		viewer = panel.getViewer();
		viewer.getTextWidget().setFont(JFaceResources.getTextFont());
		viewer.addPropertyChangeListener(new IPropertyChangeListener() {

			@Override
			public void propertyChange(final PropertyChangeEvent event) {
				if (ValidatingSourceViewer.PROPERTY_VALID.equals(event.getProperty())) {
					getShell().getDisplay().syncExec(new Runnable() {

						@Override
						public void run() {
							setPageComplete((Boolean) event.getNewValue());
						}
					});
				}
				else if (ValidatingSourceViewer.PROPERTY_VALIDATION_ENABLED.equals(event
						.getProperty())) {
					if (!((Boolean) event.getNewValue())) {
						// if validation is disabled, automatically set the page
						// to be complete
						setPageComplete(true);
					}
				}
			}
		});

		configure(viewer);

		SourceViewerKeyBindings.installDefault(viewer);

		viewer.getDocument().set(initialValue);

		addActions(panel.getToolbar(), viewer);
	}

	/**
	 * Add actions to the tool bar.
	 * 
	 * @param toolbar the tool bar
	 * @param viewer the source viewer
	 */
	protected void addActions(ToolBar toolbar, CompilingSourceViewer<C> viewer) {
		// override me
	}

	/**
	 * Create the overview ruler for the source viewer.
	 * 
	 * @return the overview ruler or <code>null</code>
	 */
	protected IOverviewRuler createOverviewRuler() {
		return null;
	}

	/**
	 * Create the vertical ruler for the source viewer.
	 * 
	 * @return the vertical ruler
	 */
	protected IVerticalRuler createVerticalRuler() {
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
	}

	/**
	 * Force validation of the source viewers document.
	 */
	public void forceValidation() {
		if (viewer != null) {
			viewer.forceUpdate();
		}
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
	 * Validate the given document. The default implementation always returns
	 * <code>true</code>.
	 * 
	 * @param document the document to validate
	 * @return if the document is valid
	 */
	protected boolean validate(String document) {
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
