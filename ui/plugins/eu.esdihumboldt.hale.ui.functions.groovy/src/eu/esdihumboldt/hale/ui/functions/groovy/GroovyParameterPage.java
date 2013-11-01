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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.cst.functions.groovy.GroovyConstants;
import eu.esdihumboldt.cst.functions.groovy.GroovyTransformation;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.Property;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyValue;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.PropertyValueImpl;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.instance.groovy.InstanceBuilder;
import eu.esdihumboldt.hale.ui.functions.core.SourceListParameterPage;
import eu.esdihumboldt.hale.ui.functions.core.SourceViewerParameterPage;
import eu.esdihumboldt.hale.ui.scripting.groovy.InstanceTestValues;
import eu.esdihumboldt.hale.ui.scripting.groovy.TestValues;
import eu.esdihumboldt.hale.ui.util.IColorManager;
import eu.esdihumboldt.hale.ui.util.groovy.GroovyColorManager;
import eu.esdihumboldt.hale.ui.util.groovy.GroovySourceViewerUtil;
import eu.esdihumboldt.hale.ui.util.groovy.SimpleGroovySourceViewerConfiguration;
import eu.esdihumboldt.hale.ui.util.source.SimpleAnnotationUtil;
import eu.esdihumboldt.hale.ui.util.source.SimpleAnnotations;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

/**
 * Parameter page for Groovy property function.
 * 
 * @author Kai Schwierczek
 * @author Simon Templer
 */
public class GroovyParameterPage extends SourceViewerParameterPage implements GroovyConstants {

	private static final ALogger log = ALoggerFactory.getLogger(GroovyParameterPage.class);

	private Iterable<EntityDefinition> variables;
	private final TestValues testValues;
	private final IColorManager colorManager = new GroovyColorManager();
	private final IAnnotationModel annotationModel = new AnnotationModel();

	/**
	 * Default constructor.
	 */
	public GroovyParameterPage() {
		super("script");

		setTitle("Function parameters");
		setDescription("Specify a Groovy script to determine the target property value");

		testValues = new InstanceTestValues();
	}

	/**
	 * @see SourceViewerParameterPage#onShowPage(boolean)
	 */
	@Override
	protected void onShowPage(boolean firstShow) {
		super.onShowPage(firstShow);

		// variables may have changed
		updateState(getDocument());
	}

	/**
	 * @see SourceViewerParameterPage#getParameterName()
	 */
	@Override
	protected String getParameterName() {
		return PARAMETER_SCRIPT;
	}

	/**
	 * @see SourceViewerParameterPage#getSourcePropertyName()
	 */
	@Override
	protected String getSourcePropertyName() {
		return ENTITY_VARIABLE;
	}

	/**
	 * @see SourceViewerParameterPage#validate(IDocument)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected boolean validate(IDocument document) {
		List<PropertyValue> values = new ArrayList<PropertyValue>();
		if (variables != null) {
			for (EntityDefinition var : variables) {
				if (var instanceof PropertyEntityDefinition) {
					PropertyEntityDefinition property = (PropertyEntityDefinition) var;
					values.add(new PropertyValueImpl(testValues.get(property), property));
				}
			}
		}

		// clear annotations
		List<Annotation> annotations = new ArrayList<>();
		Iterators.addAll(annotations, annotationModel.getAnnotationIterator());
		for (Annotation annotation : annotations) {
			annotationModel.removeAnnotation(annotation);
		}

		Property targetProperty = (Property) CellUtil.getFirstEntity(getWizard()
				.getUnfinishedCell().getTarget());
		InstanceBuilder builder = GroovyTransformation
				.createBuilder(targetProperty.getDefinition());

		// TODO specify classloader?
		boolean useInstanceValues = CellUtil.getOptionalParameter(getWizard().getUnfinishedCell(),
				GroovyTransformation.PARAM_INSTANCE_VARIABLES, Value.of(false)).as(Boolean.class);
		GroovyShell shell = new GroovyShell(GroovyTransformation.createGroovyBinding(values, null,
				builder, useInstanceValues));
		Script script = null;
		try {
			script = shell.parse(document.get());

			GroovyTransformation.evaluate(script, builder, targetProperty.getDefinition()
					.getDefinition().getPropertyType());
		} catch (Exception e) {
			setMessage(e.getMessage(), ERROR);
			addErrorAnnotation(script, e);
			// return valid if NPE, as this might be caused by null test values
			return e instanceof NullPointerException;
//			return false;
		}

		setMessage(null);
		return true;
	}

	private void addErrorAnnotation(Script script, Exception e) {
		// handle multiple groovy compilation errors
		if (e instanceof MultipleCompilationErrorsException) {
			ErrorCollector errors = ((MultipleCompilationErrorsException) e).getErrorCollector();
			for (int i = 0; i < errors.getErrorCount(); i++) {
				SyntaxException ex = errors.getSyntaxError(i);
				if (ex != null) {
					addErrorAnnotation(script, ex);
				}
			}
			return;
		}

		Annotation annotation = new Annotation(SimpleAnnotations.TYPE_ERROR, false,
				e.getLocalizedMessage());
		Position position = null;

		// single syntax exception
		if (e instanceof SyntaxException) {
			int line = ((SyntaxException) e).getStartLine() - 1;
			if (line >= 0) {
				try {
					position = new Position(getTextField().getDocument().getLineOffset(line));
				} catch (BadLocationException e1) {
					log.warn("Wrong error position in document", e1);
				}
			}
		}

		// try to determine position from stack trace of script execution
		if (position == null && script != null) {
			for (StackTraceElement ste : e.getStackTrace()) {
				if (ste.getClassName().equals(script.getClass().getName())) {
					int line = ste.getLineNumber() - 1;
					if (line >= 0) {
						try {
							position = new Position(getTextField().getDocument()
									.getLineOffset(line));
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

	/**
	 * @see SourceListParameterPage#sourcePropertiesChanged(Iterable)
	 */
	@Override
	protected void sourcePropertiesChanged(Iterable<EntityDefinition> variables) {
		this.variables = variables;
	}

	/**
	 * @see SourceViewerParameterPage#getVariableName(EntityDefinition)
	 */
	@Override
	protected String getVariableName(EntityDefinition variable) {
		// dots are not allowed in variable names, an underscore is used instead
		return super.getVariableName(variable).replace('.', '_');
	}

	@Override
	protected SourceViewerConfiguration createConfiguration() {
		return new SimpleGroovySourceViewerConfiguration(colorManager, ImmutableList.of(
				BINDING_BUILDER, BINDING_TARGET));
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.functions.core.SourceViewerParameterPage#createOverviewRuler()
	 */
	@Override
	protected IOverviewRuler createOverviewRuler() {
		IOverviewRuler ruler = SimpleAnnotationUtil.createDefaultOverviewRuler(14, colorManager,
				annotationModel);
		return ruler;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.functions.core.SourceViewerParameterPage#createVerticalRuler()
	 */
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
}
