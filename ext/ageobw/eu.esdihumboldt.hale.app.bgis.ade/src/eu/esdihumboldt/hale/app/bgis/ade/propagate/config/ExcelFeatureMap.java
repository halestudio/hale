/*
 * Copyright (c) 2013 Fraunhofer IGD
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
 *     Fraunhofer IGD
 */

package eu.esdihumboldt.hale.app.bgis.ade.propagate.config;

import java.net.URI;
import java.util.Set;

import org.apache.poi.ss.usermodel.Row;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import eu.esdihumboldt.hale.io.xls.AbstractAnalyseTable;

/**
 * Feature map based on excel table.
 * 
 * @author Simon Templer
 */
public class ExcelFeatureMap extends AbstractAnalyseTable implements FeatureMap {

	private int sourceCol, targetCol = -1;

	private final SetMultimap<String, String> targetToSource = HashMultimap.create();

	/**
	 * Create a feature map from the Excel table at the given location.
	 * 
	 * @param location the Excel file location
	 * @throws Exception if loading the file fails
	 */
	public ExcelFeatureMap(URI location) throws Exception {
		analyse(location);
	}

	@Override
	protected void headerCell(int num, String text) {
		if ("source".equalsIgnoreCase(text)) {
			sourceCol = num;
		}
		else if ("target".equalsIgnoreCase(text)) {
			targetCol = num;
		}
	}

	@Override
	protected void analyseRow(int num, Row row) {
		String source = extractText(row.getCell(sourceCol));
		String target = extractText(row.getCell(targetCol));

		targetToSource.put(target, source);
	}

	@Override
	public Set<String> getPossibleSourceTypes(String targetTypeName) {
		return targetToSource.get(targetTypeName);
	}

}
