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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.scripting.groovy;

import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.common.scripting.Script;
import eu.esdihumboldt.hale.ui.common.AttributeEditor;
import eu.esdihumboldt.hale.ui.scripting.ScriptUI;

/**
 * UI class for groovy scripts.
 * 
 * @author Kai Schwierczek
 */
public class GroovyUI implements ScriptUI {

	private Script script;

	/**
	 * @see eu.esdihumboldt.hale.ui.scripting.ScriptUI#setScript(eu.esdihumboldt.hale.common.scripting.Script)
	 */
	@Override
	public void setScript(Script script) {
		this.script = script;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.scripting.ScriptUI#createEditor(org.eclipse.swt.widgets.Composite,
	 *      java.lang.Class)
	 */
	@Override
	public AttributeEditor<String> createEditor(Composite parent, Class<?> binding) {
		return new GroovyEditor(parent, script, binding);
	}
}
