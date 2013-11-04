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

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.control.ErrorCollector;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.codehaus.groovy.syntax.SyntaxException;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.AnnotationRulerColumn;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.LineNumberRulerColumn;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import com.google.common.collect.Iterators;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.cst.functions.groovy.GroovyConstants;
import eu.esdihumboldt.hale.ui.util.ColorManager;
import eu.esdihumboldt.hale.ui.util.groovy.GroovyColorManager;
import eu.esdihumboldt.hale.ui.util.groovy.GroovySourceViewerUtil;
import eu.esdihumboldt.hale.ui.util.groovy.SimpleGroovySourceViewerConfiguration;
import eu.esdihumboldt.hale.ui.util.source.SimpleAnnotationUtil;
import eu.esdihumboldt.hale.ui.util.source.SimpleAnnotations;
import groovy.lang.Script;

/**
 * Base page for editing a Groovy script for type relations.
 * 
 * @author Simon Templer
 */
public class GroovyScriptPage extends SourceViewerPage implements GroovyConstants {

	private static final ALogger log = ALoggerFactory.getLogger(GroovyScriptPage.class);

	/**
	 * The Groovy color manager.
	 */
	protected final ColorManager colorManager = new GroovyColorManager();

	private final IAnnotationModel annotationModel = new AnnotationModel();

	/**
	 * Default constructor.
	 */
	public GroovyScriptPage() {
		super("groovyScript", PARAMETER_SCRIPT, BINDING_TARGET + " = {\n\n}");
	}

	@Override
	protected SourceViewerConfiguration createConfiguration() {
		return new SimpleGroovySourceViewerConfiguration(colorManager);
	}

	@Override
	protected void createAndSetDocument(SourceViewer viewer) {
		IDocument doc = new Document();
		GroovySourceViewerUtil.setupDocument(doc);
		annotationModel.connect(doc);
		doc.set(""); //$NON-NLS-1$

		viewer.setDocument(doc, annotationModel);
	}

	@Override
	public void dispose() {
		colorManager.dispose();

		super.dispose();
	}

	/**
	 * Handle a completed validation and display the results.
	 * 
	 * @param script the executed script if it could be compiled, may be
	 *            <code>null</code>
	 * @param exception the error that occurred, <code>null</code> if the
	 *            validation was successful
	 * @return if the script was validated successfully
	 */
	protected boolean handleValidationResult(Script script, final Exception exception) {
		// set page message
		getShell().getDisplay().syncExec(new Runnable() {

			@Override
			public void run() {
				if (exception == null) {
					setMessage(null);
				}
				else {
					setMessage(exception.getMessage(), ERROR);
				}
			}
		});

		// add annotation based on exception
		if (exception != null) {
			addErrorAnnotation(script, exception);
		}

		// return valid if NPE, as this might be caused by null test values
		return exception == null || exception instanceof NullPointerException;
	}

	/**
	 * Add an error annotation based on the given exception.
	 * 
	 * @param script the Groovy script, may be <code>null</code> if it could not
	 *            be compiled
	 * @param exception the occurred exception
	 */
	private void addErrorAnnotation(Script script, Exception exception) {
		addGroovyErrorAnnotation(annotationModel, getDocument(), script, exception);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean validate(String document) {
		// clear annotations
		List<Annotation> annotations = new ArrayList<>();
		Iterators.addAll(annotations, annotationModel.getAnnotationIterator());
		for (Annotation annotation : annotations) {
			annotationModel.removeAnnotation(annotation);
		}

		return super.validate(document);
	}

	@Override
	protected IOverviewRuler createOverviewRuler() {
		IOverviewRuler ruler = SimpleAnnotationUtil.createDefaultOverviewRuler(14, colorManager,
				annotationModel);
		return ruler;
	}

	@Override
	protected IVerticalRuler createVerticalRuler() {
		final Display display = Display.getCurrent();
		CompositeRuler ruler = new CompositeRuler(3);

		AnnotationRulerColumn annotations = SimpleAnnotationUtil
				.createDefaultAnnotationRuler(annotationModel);

		ruler.addDecorator(0, annotations);

		LineNumberRulerColumn lineNumbers = new LineNumberRulerColumn();
		lineNumbers.setBackground(display.getSystemColor(SWT.COLOR_GRAY)); // SWT.COLOR_INFO_BACKGROUND));
		lineNumbers.setForeground(display.getSystemColor(SWT.COLOR_BLACK)); // SWT.COLOR_INFO_FOREGROUND));
		lineNumbers.setFont(JFaceResources.getTextFont());

		ruler.addDecorator(1, lineNumbers);
		return ruler;
	}

	/**
	 * Add an error annotation to the given annotation model based on an
	 * exception that occurred while compiling or executing the Groovy Script.
	 * 
	 * @param annotationModel the annotation model
	 * @param document the current document
	 * @param script the executed script, or <code>null</code>
	 * @param exception the occurred exception
	 */
	public static void addGroovyErrorAnnotation(IAnnotationModel annotationModel,
			IDocument document, Script script, Exception exception) {
		// handle multiple groovy compilation errors
		if (exception instanceof MultipleCompilationErrorsException) {
			ErrorCollector errors = ((MultipleCompilationErrorsException) exception)
					.getErrorCollector();
			for (int i = 0; i < errors.getErrorCount(); i++) {
				SyntaxException ex = errors.getSyntaxError(i);
				if (ex != null) {
					addGroovyErrorAnnotation(annotationModel, document, script, ex);
				}
			}
			return;
		}

		Annotation annotation = new Annotation(SimpleAnnotations.TYPE_ERROR, false,
				exception.getLocalizedMessage());
		Position position = null;

		// single syntax exception
		if (exception instanceof SyntaxException) {
			int line = ((SyntaxException) exception).getStartLine() - 1;
			if (line >= 0) {
				try {
					position = new Position(document.getLineOffset(line));
				} catch (BadLocationException e1) {
					log.warn("Wrong error position in document", e1);
				}
			}
		}

		// try to determine position from stack trace of script execution
		if (position == null && script != null) {
			for (StackTraceElement ste : exception.getStackTrace()) {
				if (ste.getClassName().startsWith(script.getClass().getName())) {
					int line = ste.getLineNumber() - 1;
					if (line >= 0) {
						try {
							position = new Position(document.getLineOffset(line));
							break;
						} catch (BadLocationException e1) {
							log.warn("Wrong error position in document", e1);
						}
					}
				}
			}
		}

		// fallback
		if (position == null) {
			position = new Position(0);
		}

		annotationModel.addAnnotation(annotation, position);
	}

}
