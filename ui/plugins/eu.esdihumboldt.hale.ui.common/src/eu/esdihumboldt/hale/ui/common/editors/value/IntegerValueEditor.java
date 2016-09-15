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

package eu.esdihumboldt.hale.ui.common.editors.value;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.ui.common.AttributeEditor;

/**
 * Value editor wrapping an integer editor.
 * 
 * @author Simon Templer
 */
public class IntegerValueEditor extends ValueEditor<Integer> {

	/**
	 * Wraps an integer editor as {@link Value} editor.
	 * 
	 * @param editor the editor to wrap
	 */
	public IntegerValueEditor(AttributeEditor<Integer> editor) {
		super(editor);
	}

	@Override
	protected Integer fromValue(Value value) {
		return value.as(Integer.class);
	}

	@Override
	protected Value toValue(Integer value) {
		return Value.of(value);
	}

}
