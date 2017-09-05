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

import java.util.Collection;
import java.util.Collections;

import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.widgets.ToolBar;

import com.google.common.collect.ImmutableList;

import eu.esdihumboldt.cst.functions.groovy.GroovyRetype;
import eu.esdihumboldt.cst.functions.groovy.helper.HelperFunctionsService;
import eu.esdihumboldt.cst.functions.groovy.internal.GroovyUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.align.transformation.function.ExecutionContext;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.FamilyInstanceImpl;
import eu.esdihumboldt.hale.common.align.transformation.report.impl.CellLog;
import eu.esdihumboldt.hale.common.align.transformation.report.impl.DefaultTransformationReporter;
import eu.esdihumboldt.hale.common.instance.groovy.InstanceBuilder;
import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.instance.model.FamilyInstance;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstance;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.function.generic.AbstractGenericFunctionWizard;
import eu.esdihumboldt.hale.ui.functions.groovy.internal.HelperFunctionsCompletions;
import eu.esdihumboldt.hale.ui.functions.groovy.internal.InstanceBuilderCompletions;
import eu.esdihumboldt.hale.ui.functions.groovy.internal.PageFunctions;
import eu.esdihumboldt.hale.ui.functions.groovy.internal.PageHelp;
import eu.esdihumboldt.hale.ui.functions.groovy.internal.TypeStructureTray;
import eu.esdihumboldt.hale.ui.functions.groovy.internal.TypeStructureTray.TypeProvider;
import eu.esdihumboldt.hale.ui.scripting.groovy.InstanceTestValues;
import eu.esdihumboldt.hale.ui.scripting.groovy.TestValues;
import eu.esdihumboldt.hale.ui.util.groovy.SimpleGroovySourceViewerConfiguration;
import eu.esdihumboldt.hale.ui.util.groovy.ast.GroovyAST;
import eu.esdihumboldt.hale.ui.util.source.CompilingSourceViewer;
import eu.esdihumboldt.util.groovy.sandbox.GroovyService;
import groovy.lang.Binding;
import groovy.lang.Script;

/**
 * Configuration page for the Groovy Retype script.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
public class GroovyRetypePage extends GroovyScriptPage<AbstractGenericFunctionWizard<?, ?>> {

	private final TestValues testValues;

	/**
	 * Default constructor.
	 */
	public GroovyRetypePage() {
		super();
		setTitle("Convert instance script");
		setDescription("Specify a Groovy script to build a target instance from a source instance");

		testValues = new InstanceTestValues();
	}

	/**
	 * @return the testValues
	 */
	public TestValues getTestValues() {
		return testValues;
	}

	@Override
	protected SourceViewerConfiguration createConfiguration() {
		InstanceBuilderCompletions targetCompletions = new InstanceBuilderCompletions(
				definitionImages) {

			@Override
			protected TypeDefinition getTargetType() {
				Type typeEntity = (Type) CellUtil
						.getFirstEntity(getWizard().getUnfinishedCell().getTarget());
				if (typeEntity != null) {
					return typeEntity.getDefinition().getDefinition();
				}
				return null;
			}
		};

		HelperFunctionsCompletions functionCompletions = new HelperFunctionsCompletions(
				HaleUI.getServiceProvider().getService(HelperFunctionsService.class));

		return new SimpleGroovySourceViewerConfiguration(colorManager,
				ImmutableList.of(BINDING_BUILDER, BINDING_SOURCE, BINDING_TARGET,
						BINDING_SOURCE_TYPES, BINDING_TARGET_TYPE, BINDING_CELL, BINDING_LOG,
						BINDING_CELL_CONTEXT, BINDING_FUNCTION_CONTEXT,
						BINDING_TRANSFORMATION_CONTEXT, BINDING_HELPER_FUNCTIONS),
				ImmutableList.of(targetCompletions, functionCompletions));
	}

	@Override
	protected boolean validate(String document) {
		super.validate(document);

		Type targetType = (Type) CellUtil
				.getFirstEntity(getWizard().getUnfinishedCell().getTarget());
		Type sourceType = (Type) CellUtil
				.getFirstEntity(getWizard().getUnfinishedCell().getSource());
		if (sourceType == null || targetType == null) {
			// not yet selected (NewRelationWizard)
			return false;
		}

		InstanceBuilder builder = new InstanceBuilder(false);

		Instance instance = testValues.get(sourceType.getDefinition());
		if (instance == null) {
			// use an empty instance as input for the script
			instance = new DefaultInstance(sourceType.getDefinition().getDefinition(),
					DataSet.SOURCE);
		}
		FamilyInstance source = new FamilyInstanceImpl(instance);
		Cell cell = getWizard().getUnfinishedCell();
		CellLog log = new CellLog(new DefaultTransformationReporter("dummy", false), cell);
		ExecutionContext context = new DummyExecutionContext(HaleUI.getServiceProvider());
		Binding binding = GroovyRetype.createBinding(source, cell, builder, log, context,
				targetType.getDefinition().getDefinition());

		GroovyService service = HaleUI.getServiceProvider().getService(GroovyService.class);
		Script script = null;
		try {
			script = service.parseScript(document, binding);

			GroovyUtil.evaluateAll(script, builder, targetType.getDefinition().getDefinition(),
					service, log);
		} catch (final Exception e) {
			return handleValidationResult(script, e);
		}

		return handleValidationResult(script, null);
	}

	@Override
	protected void addActions(ToolBar toolbar, CompilingSourceViewer<GroovyAST> viewer) {
		super.addActions(toolbar, viewer);

		PageHelp.createToolItem(toolbar, this);

		TypeStructureTray.createToolItem(toolbar, this, SchemaSpaceID.SOURCE, new TypeProvider() {

			@Override
			public Collection<? extends TypeDefinition> getTypes() {
				Type sourceType = (Type) CellUtil
						.getFirstEntity(getWizard().getUnfinishedCell().getSource());
				if (sourceType != null) {
					return Collections.singleton(sourceType.getDefinition().getDefinition());
				}
				return Collections.emptyList();
			}
		});

		TypeStructureTray.createToolItem(toolbar, this, SchemaSpaceID.TARGET, new TypeProvider() {

			@Override
			public Collection<? extends TypeDefinition> getTypes() {
				Type targetType = (Type) CellUtil
						.getFirstEntity(getWizard().getUnfinishedCell().getTarget());
				if (targetType != null) {
					return Collections.singleton(targetType.getDefinition().getDefinition());
				}
				return Collections.emptyList();
			}
		});

		PageFunctions.createToolItem(toolbar, this);
	}

	@Override
	public String getHelpContext() {
		return "eu.esdihumboldt.cst.functions.groovy.retype";
	}

}
