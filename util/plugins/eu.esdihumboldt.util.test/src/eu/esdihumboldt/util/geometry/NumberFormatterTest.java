/*
 * Copyright (c) 2016 wetransform GmbH
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

package eu.esdihumboldt.util.geometry;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test for {@link NumberFormatter}
 * 
 * @author Arun
 */
@RunWith(Parameterized.class)
public class NumberFormatterTest {

	private final String format;
	private final double value;
	private final String formattedValue;

	/**
	 * Constructor
	 * 
	 * @param format format in which specified value represented
	 * @param value double value
	 * @param formattedValue formatted value
	 */
	public NumberFormatterTest(String format, double value, String formattedValue) {
		this.format = format;
		this.value = value;
		this.formattedValue = formattedValue;
	}

	/**
	 * supply parameters to parameterized test
	 * 
	 * @return Collection of parameters
	 */
	@SuppressWarnings("rawtypes")
	@Parameters
	public static Collection addParameters() {

		return Arrays.asList(new Object[][] { //
				{ "0000.00", 12.34, "0012.34" }, //
				{ "000000.000", 6750.3, "006750.300" }, //
				{ "0000000.00", 454232.3478, "0454232.35" }, //
				{ "0000.00", 789887.5623, "789887.56" }, //
				{ "0.000", 343452.5623, "343452.562" } //
		});
	}

	/**
	 * Test numbers
	 */
	@Test
	public void testNumbers() {

		String formatted = NumberFormatter.formatTo(this.value,
				NumberFormatter.getFormatter(format));

		assertEquals(formatted, this.formattedValue);

	}

}
