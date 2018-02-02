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

package eu.esdihumboldt.hale.common.align.merge.functions;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.cst.functions.groovy.GroovyConstants;
import eu.esdihumboldt.cst.functions.groovy.GroovyCreate;
import eu.esdihumboldt.cst.functions.groovy.GroovyJoin;
import eu.esdihumboldt.cst.functions.groovy.GroovyMerge;
import eu.esdihumboldt.cst.functions.groovy.GroovyRetype;
import eu.esdihumboldt.hale.common.align.merge.MergeIndex;
import eu.esdihumboldt.hale.common.align.merge.impl.DefaultMergeCellMigrator;
import eu.esdihumboldt.hale.common.align.migrate.AlignmentMigration;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.functions.CreateFunction;
import eu.esdihumboldt.hale.common.align.model.functions.JoinFunction;
import eu.esdihumboldt.hale.common.align.model.functions.MergeFunction;
import eu.esdihumboldt.hale.common.align.model.functions.RetypeFunction;
import eu.esdihumboldt.hale.common.core.io.Text;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.report.SimpleLog;

/**
 * Merge migrator for the Groovy Retype function.
 * 
 * @author Simon Templer
 */
public class GroovyRetypeMergeMigrator extends DefaultMergeCellMigrator {

	@Override
	protected void mergeSource(MutableCell cell, String sourceName, EntityDefinition source,
			Cell match, Cell originalCell, SimpleLog log, Void context,
			AlignmentMigration migration, MergeIndex mergeIndex) {

		// combination always possible
		// but Groovy script cannot be handled automatically

		String matchFunction = match.getTransformationIdentifier();
		// function that the migrated cell should have
		String translateTo = matchFunction;
		// if the script must be dropped
		boolean dropScript = true;
		// if there is an existing script in the match
		boolean addToScript = false;

		switch (matchFunction) {
		// cases where "normal" match is replaced by Groovy equivalent
		case JoinFunction.ID:
			translateTo = GroovyJoin.ID;
			dropScript = false;
			break;
		case RetypeFunction.ID:
			translateTo = GroovyRetype.ID;
			dropScript = false;
			break;
		case MergeFunction.ID:
			translateTo = GroovyMerge.ID;
			dropScript = false;
			break;
		case CreateFunction.ID:
			translateTo = GroovyCreate.ID;
			dropScript = false;
			break;
		// cases where a groovy match is annotated with the script
		case GroovyJoin.ID:
		case GroovyCreate.ID:
		case GroovyRetype.ID:
		case GroovyMerge.ID:
			dropScript = false;
			addToScript = true;
			break;
		default:
			// all other functions
		}

		// cell function
		cell.setTransformationIdentifier(translateTo);

		// source from match
		cell.setSource(ArrayListMultimap.create(match.getSource()));

		// parameters from match
		ListMultimap<String, ParameterValue> params = ArrayListMultimap
				.create(match.getTransformationParameters());

		String script = null;
		ParameterValue scriptValue = CellUtil.getFirstParameter(originalCell,
				GroovyConstants.PARAMETER_SCRIPT);
		if (scriptValue != null) {
			// try retrieving as text
			Text text = scriptValue.as(Text.class);
			if (text != null) {
				script = text.getText();
			}
			else {
				// fall back to string value
				script = scriptValue.as(String.class);
			}
		}

		if (script != null) {
			if (dropScript) {
				// script is dropped and not added to parameters
				String msg = "Script from Groovy Retype could not be transfered to migrated cell:\n\n"
						+ script;
				log.warn(msg);
			}
			else {
				if (addToScript) {
					// add to existing script

					String matchScript = null;
					ParameterValue matchScriptValue = CellUtil.getFirstParameter(match,
							GroovyConstants.PARAMETER_SCRIPT);
					if (matchScriptValue != null) {
						// try retrieving as text
						Text text = matchScriptValue.as(Text.class);
						if (text != null) {
							matchScript = text.getText();
						}
						else {
							// fall back to string value
							matchScript = matchScriptValue.as(String.class);
						}
					}

					if (matchScript != null) {
						script = matchScript + "\n\n\n// FIXME Script from merged Groovy Retype\n\n"
								+ comment(script);
					}

					log.warn(
							"A script was associated to the merged Groovy Retype this cell is derived from, it was added commented out to this cell's script. Please check how the functionality can be combined.");

				}
				else {
					// set as new script
					if (!RetypeFunction.ID.equals(match.getTransformationIdentifier())) {
						script = "// FIXME Script taken from merged Groovy Retype:\n\n" + script;
						log.warn(
								"A script was associated to the merged Groovy Retype this cell is derived from, it was used as script for this cell's script and needs to be verified/adapted.");
					}
				}

				params.put(GroovyConstants.PARAMETER_SCRIPT,
						new ParameterValue(Value.of(new Text(script))));
			}
		}

		cell.setTransformationParameters(params);
	}

	private String comment(String script) {
		StringBuilder result = new StringBuilder();
		for (String line : script.split("\\r?\\n")) {
			result.append("// ");
			result.append(line);
			result.append("\n");
		}
		return result.toString();
	}

}
