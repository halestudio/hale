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
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import eu.esdihumboldt.util.format.DecimalFormatUtil;

/**
 * Test for {@link DecimalFormatUtil}
 * 
 * @author Arun Verma
 */
@RunWith(Parameterized.class)
public class DecimalFormatUtilTest {

	private final String format;
	private final Number value;
	private final String formattedValue;

	/**
	 * Constructor
	 * 
	 * @param format the formatter pattern
	 * @param value value to format
	 * @param formattedValue expected result of the formatting operation
	 */
	public DecimalFormatUtilTest(String format, Number value, String formattedValue) {
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
				{ "0.000", 343452.5623, "343452.562" }, //
				{ "0.00000", 343452.5623, "343452.56230" }, //
				{ "0.0000#", 343452.5623, "343452.5623" }, //
				{ "0.0#", -343452.5623, "-343452.56" }, //
				{ "0.#", -343452.0, "-343452" }, //
				{ "0.0#", -343452.0, "-343452.0" }, //
				{ "0.##############", 5.6 + 5.8, "11.4" }, //
				{ "0.####", 15.6f + 5.8f, "21.4" }, //
				{ "0.#####################", BigDecimal.valueOf(15.6).add(BigDecimal.valueOf(5.8)),
						"21.4" } //
		});
	}

	/**
	 * Test numbers
	 */
	@Test
	public void testFormatter() {
		DecimalFormat decimalFormat = DecimalFormatUtil.getFormatter(format);
		assertNotNull(decimalFormat);

		String formatResult = decimalFormat.format(this.value);
		assertEquals(this.formattedValue, formatResult);
	}

}
