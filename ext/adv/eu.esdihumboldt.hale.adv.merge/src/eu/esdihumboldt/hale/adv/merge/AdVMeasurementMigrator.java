/*
 * Copyright (c) 2018 wetransform GmbH
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

package eu.esdihumboldt.hale.adv.merge;

import java.text.MessageFormat;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.merge.MergeIndex;
import eu.esdihumboldt.hale.common.align.merge.impl.DefaultMergeCellMigrator;
import eu.esdihumboldt.hale.common.align.migrate.AlignmentMigration;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.functions.AssignFunction;
import eu.esdihumboldt.hale.common.core.report.SimpleLog;

/**
 * Migrator for custom function that converts AdV units of measurement to UCUM.
 * 
 * @author Simon Templer
 */
public class AdVMeasurementMigrator extends DefaultMergeCellMigrator {

	private String convertCode(String uom, SimpleLog log) {
		// this method reflects the behavior of the custom function

		// Mapping auf UCUM
		switch (uom) {
		case "urn:adv:uom:m2":
			return "m2";
		case "urn:adv:uom:m":
			return "m";
		case "urn:adv:uom:km":
			return "km";
		}

		// Transformations-Warnung falls kein Mapping auf UCUM vorhanden
		log.warn(MessageFormat.format(
				"Unknown UCUM representation for unit of measurment {0}, code was used as-is",
				uom));
		return uom;
	}

	@Override
	protected void mergeSource(MutableCell cell, String sourceName, EntityDefinition source,
			Cell match, Cell originalCell, SimpleLog log, Void context,
			AlignmentMigration migration, MergeIndex mergeIndex) {

		if (AssignFunction.ID_BOUND.equals(match.getTransformationIdentifier())) {
			// get value used in bound assign
			String value = CellUtil.getFirstParameter(match, AssignFunction.PARAMETER_VALUE)
					.as(String.class);

			// convert value according to custom function
			value = convertCode(value, log);

			// configure cell
			cell.setTransformationIdentifier(AssignFunction.ID_BOUND);
			cell.setSource(ArrayListMultimap.create(match.getSource()));
			ListMultimap<String, ParameterValue> params = ArrayListMultimap.create();
			params.put(AssignFunction.PARAMETER_VALUE, new ParameterValue(value));
			cell.setTransformationParameters(params);

			return;
		}

		super.mergeSource(cell, sourceName, source, match, originalCell, log, context, migration,
				mergeIndex);
	}

}
