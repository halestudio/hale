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

package eu.esdihumboldt.hale.ui.functions.groovy;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.swt.widgets.ToolBar;

import eu.esdihumboldt.cst.functions.groovy.GroovyRetype;
import eu.esdihumboldt.cst.functions.groovy.internal.GroovyUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.align.model.functions.JoinFunction;
import eu.esdihumboldt.hale.common.align.model.functions.join.JoinParameter;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
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
import eu.esdihumboldt.hale.ui.functions.groovy.internal.JoinTypeStructureTray;
import eu.esdihumboldt.hale.ui.functions.groovy.internal.PageFunctions;
import eu.esdihumboldt.hale.ui.functions.groovy.internal.PageHelp;
import eu.esdihumboldt.hale.ui.functions.groovy.internal.TypeStructureTray;
import eu.esdihumboldt.hale.ui.functions.groovy.internal.TypeStructureTray.TypeProvider;
import eu.esdihumboldt.hale.ui.util.groovy.ast.GroovyAST;
import eu.esdihumboldt.hale.ui.util.source.CompilingSourceViewer;
import eu.esdihumboldt.util.groovy.sandbox.GroovyService;
import groovy.lang.Binding;
import groovy.lang.Script;

/**
 * Configuration page for the Groovy Join script.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
public class GroovyJoinPage extends GroovyRetypePage {

	/**
	 * Default constructor.
	 */
	public GroovyJoinPage() {
		super();

		setDescription(
				"Specify a Groovy script to build a target instance from joined source instances");
	}

	@Override
	protected boolean validate(String document) {
		ParameterValue param = CellUtil.getFirstParameter(getWizard().getUnfinishedCell(),
				JoinFunction.PARAMETER_JOIN);
		JoinParameter joinParameter = param.as(JoinParameter.class);

		// check Join parameter
		if (joinParameter == null) {
			// setValidationError("Missing join configuration");
			return false;
		}
		else {
			String error = joinParameter.validate();
			if (!setValidationError(error)) {
				return false;
			}
		}

		// target type
		Type targetType = (Type) CellUtil
				.getFirstEntity(getWizard().getUnfinishedCell().getTarget());
		if (targetType == null) {
			// not yet selected (NewRelationWizard)
			return false;
		}

		/*
		 * FIXME use JoinParameter to fake joined instances!
		 * 
		 * XXX for now just base instance
		 */
		TypeEntityDefinition sourceType = joinParameter.types.get(0);

		InstanceBuilder builder = new InstanceBuilder(false);
		Instance instance = getTestValues().get(sourceType);
		if (instance == null) {
			// use an empty instance as input for the script
			instance = new DefaultInstance(sourceType.getDefinition(), DataSet.SOURCE);
		}

		FamilyInstance source = new FamilyInstanceImpl(instance);

		// prepare binding
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
		PageHelp.createToolItem(toolbar, this);

		// FIXME TypeStructureTray does not support FamilyInstances
		// XXX for now only use Join base type
		JoinTypeStructureTray.createToolItem(toolbar, this, SchemaSpaceID.SOURCE,
				new TypeProvider() {

					@Override
					public Collection<? extends TypeDefinition> getTypes() {
						ParameterValue param = CellUtil.getFirstParameter(
								getWizard().getUnfinishedCell(), JoinFunction.PARAMETER_JOIN);
						JoinParameter joinParameter = param.as(JoinParameter.class);
						if (joinParameter != null && joinParameter.types != null
								&& !joinParameter.types.isEmpty()) {
							return Collections
									.singleton(joinParameter.types.get(0).getDefinition());
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
		return "eu.esdihumboldt.cst.functions.groovy.join";
	}

}
