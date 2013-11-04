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

import eu.esdihumboldt.cst.functions.groovy.GroovyCreate;
import eu.esdihumboldt.cst.functions.groovy.internal.GroovyUtil;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.instance.groovy.InstanceBuilder;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.functions.groovy.TypeStructureTray.TypeProvider;
import eu.esdihumboldt.hale.ui.util.groovy.SimpleGroovySourceViewerConfiguration;
import eu.esdihumboldt.hale.ui.util.source.ValidatingSourceViewer;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

/**
 * Configuration page for the Groovy Create script.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
public class GroovyCreatePage extends GroovyScriptPage {

	/**
	 * Default constructor.
	 */
	public GroovyCreatePage() {
		super();
		setTitle("Create instances script");
		setDescription("Specify a Groovy script to build the target instance");
	}

	@Override
	protected SourceViewerConfiguration createConfiguration() {
		return new SimpleGroovySourceViewerConfiguration(colorManager, ImmutableList.of(
				BINDING_BUILDER, BINDING_INDEX, BINDING_TARGET));
	}

	@Override
	protected boolean validate(String document) {
		super.validate(document);

		Type typeEntity = (Type) CellUtil.getFirstEntity(getWizard().getUnfinishedCell()
				.getTarget());

		InstanceBuilder builder = new InstanceBuilder();
		Binding binding = GroovyCreate.createBinding(0, builder);

		GroovyShell shell = new GroovyShell(binding);
		Script script = null;
		try {
			script = shell.parse(document);

			GroovyUtil.evaluate(script, builder, typeEntity.getDefinition().getDefinition());
		} catch (final Exception e) {
			return handleValidationResult(script, e);
		}

		return handleValidationResult(script, null);
	}

	@Override
	protected void addActions(ToolBar toolbar, ValidatingSourceViewer viewer) {
		super.addActions(toolbar, viewer);

		PageHelp.createToolItem(toolbar, this);

		TypeStructureTray.createToolItem(toolbar, this, SchemaSpaceID.TARGET, new TypeProvider() {

			@Override
			public Collection<? extends TypeDefinition> getTypes() {
				Type typeEntity = (Type) CellUtil.getFirstEntity(getWizard().getUnfinishedCell()
						.getTarget());
				if (typeEntity != null) {
					return Collections.singleton(typeEntity.getDefinition().getDefinition());
				}
				return Collections.emptyList();
			}
		});
	}

	@Override
	public String getHelpContext() {
		return "eu.esdihumboldt.cst.functions.groovy.create";
	}

}
