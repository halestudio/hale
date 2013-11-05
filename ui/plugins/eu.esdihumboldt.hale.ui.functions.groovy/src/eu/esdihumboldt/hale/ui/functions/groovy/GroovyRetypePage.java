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
import eu.esdihumboldt.cst.functions.groovy.internal.GroovyUtil;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.FamilyInstanceImpl;
import eu.esdihumboldt.hale.common.instance.groovy.InstanceBuilder;
import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.instance.model.FamilyInstance;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstance;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.functions.groovy.TypeStructureTray.TypeProvider;
import eu.esdihumboldt.hale.ui.scripting.groovy.InstanceTestValues;
import eu.esdihumboldt.hale.ui.scripting.groovy.TestValues;
import eu.esdihumboldt.hale.ui.util.groovy.SimpleGroovySourceViewerConfiguration;
import eu.esdihumboldt.hale.ui.util.source.ValidatingSourceViewer;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

/**
 * Configuration page for the Groovy Retype script.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
public class GroovyRetypePage extends GroovyScriptPage {

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

	@Override
	protected SourceViewerConfiguration createConfiguration() {
		return new SimpleGroovySourceViewerConfiguration(colorManager, ImmutableList.of(
				BINDING_BUILDER, BINDING_SOURCE, BINDING_TARGET));
	}

	@Override
	protected boolean validate(String document) {
		super.validate(document);

		Type targetType = (Type) CellUtil.getFirstEntity(getWizard().getUnfinishedCell()
				.getTarget());
		Type sourceType = (Type) CellUtil.getFirstEntity(getWizard().getUnfinishedCell()
				.getSource());

		InstanceBuilder builder = new InstanceBuilder();

		Instance instance = testValues.get(sourceType.getDefinition());
		if (instance == null) {
			// use an empty instance as input for the script
			instance = new DefaultInstance(sourceType.getDefinition().getDefinition(),
					DataSet.SOURCE);
		}
		FamilyInstance source = new FamilyInstanceImpl(instance);
		Binding binding = GroovyRetype.createBinding(source, builder);

		GroovyShell shell = new GroovyShell(binding);
		Script script = null;
		try {
			script = shell.parse(document);

			GroovyUtil.evaluate(script, builder, targetType.getDefinition().getDefinition());
		} catch (final Exception e) {
			return handleValidationResult(script, e);
		}

		return handleValidationResult(script, null);
	}

	@Override
	protected void addActions(ToolBar toolbar, ValidatingSourceViewer viewer) {
		super.addActions(toolbar, viewer);

		PageHelp.createToolItem(toolbar, this);

		TypeStructureTray.createToolItem(toolbar, this, SchemaSpaceID.SOURCE, new TypeProvider() {

			@Override
			public Collection<? extends TypeDefinition> getTypes() {
				Type sourceType = (Type) CellUtil.getFirstEntity(getWizard().getUnfinishedCell()
						.getSource());
				if (sourceType != null) {
					return Collections.singleton(sourceType.getDefinition().getDefinition());
				}
				return Collections.emptyList();
			}
		});

		TypeStructureTray.createToolItem(toolbar, this, SchemaSpaceID.TARGET, new TypeProvider() {

			@Override
			public Collection<? extends TypeDefinition> getTypes() {
				Type targetType = (Type) CellUtil.getFirstEntity(getWizard().getUnfinishedCell()
						.getTarget());
				if (targetType != null) {
					return Collections.singleton(targetType.getDefinition().getDefinition());
				}
				return Collections.emptyList();
			}
		});
	}

	@Override
	public String getHelpContext() {
		return "eu.esdihumboldt.cst.functions.groovy.retype";
	}

}
