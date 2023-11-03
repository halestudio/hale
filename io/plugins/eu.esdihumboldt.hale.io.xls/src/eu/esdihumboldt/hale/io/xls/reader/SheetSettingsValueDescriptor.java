/*
 * Copyright (c) 2023 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.xls.reader;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.ValueList;
import eu.esdihumboldt.hale.common.core.parameter.AbstractParameterValueDescriptor;

/**
 * Value descriptor for reader parameter with sheet settings.
 * 
 * @author Simon Templer
 */
public class SheetSettingsValueDescriptor extends AbstractParameterValueDescriptor {

	/**
	 * Default constructor
	 */
	public SheetSettingsValueDescriptor() {
		super(Value.NULL, createExample());
	}

	private static Value createExample() {
		ValueList list = new ValueList();

		SheetSettings byIndex = new SheetSettings(null, 0);
		byIndex.setSkipSheet(true);
		list.add(byIndex.toValue());

		String ns = "http://example.com";

		SheetSettings byName1 = new SheetSettings("students", null);
		byName1.setSkipLines(1);
		byName1.setTypeName(new QName(ns, "Student"));
		list.add(byName1.toValue());

		SheetSettings byName2 = new SheetSettings("teachers", null);
		byName2.setSkipLines(3);
		byName2.setTypeName(new QName(ns, "Employee"));
		list.add(byName2.toValue());

		return list.toValue();
	}

	@Override
	public String getSampleDescription() {
		return "Sheet settings are provided as a list. Each item should identify the related sheet by index or name. "
				+ "Sheets can be configured with how many lines to skip when reading the data, "
				+ "which type to associate the sheet to and if they should be skipped entirely.";
	}

}
