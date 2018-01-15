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

import java.util.Map.Entry;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.merge.MergeIndex;
import eu.esdihumboldt.hale.common.align.merge.impl.AbstractMergeCellMigrator;
import eu.esdihumboldt.hale.common.align.migrate.AlignmentMigration;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.functions.JoinFunction;
import eu.esdihumboldt.hale.common.align.model.functions.RetypeFunction;
import eu.esdihumboldt.hale.common.align.model.functions.join.JoinParameter;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.core.report.SimpleLog;

/**
 * Merge migrator for Join functions.
 * 
 * @author Simon Templer
 */
public class JoinMergeMigrator extends AbstractMergeCellMigrator<JoinContext> {

	@Override
	protected void mergeSource(MutableCell cell, String sourceName, EntityDefinition source,
			Cell match, Cell originalCell, SimpleLog log, JoinContext context,
			AlignmentMigration migration, MergeIndex mergeIndex) {

		String matchFunction = match.getTransformationIdentifier();

		boolean groovy = false;

		switch (matchFunction) {
		case "eu.esdihumboldt.cst.functions.groovy.join":
			groovy = true;
		case JoinFunction.ID:
			mergeJoinSource(cell, source, match, originalCell, log, context, groovy);
			break;
		case "eu.esdihumboldt.cst.functions.groovy.retype":
			groovy = true;
		case RetypeFunction.ID:
			mergeRetypeSource(cell, source, match, originalCell, log, context, groovy);
			break;
		default:
			// fall-back
			super.mergeSource(cell, sourceName, source, match, originalCell, log, context,
					migration, mergeIndex);
		}
	}

	@SuppressWarnings("unused")
	private void mergeRetypeSource(MutableCell cell, EntityDefinition source, Cell match,
			Cell originalCell, SimpleLog log, JoinContext context, boolean groovy) {
		/*
		 * Sources: Add all from match (should be one)
		 */
		addSources(cell, source, match, log);

		/*
		 * Join order: Replace type with matched source
		 */
		Entity matchEntity = CellUtil.getFirstEntity(match.getSource());
		TypeEntityDefinition matchSource = null;
		if (matchEntity != null) {
			EntityDefinition def = matchEntity.getDefinition();
			if (def instanceof TypeEntityDefinition) {
				matchSource = (TypeEntityDefinition) def;
			}
		}
		if (matchSource != null) {
			context.addOrderReplacement((TypeEntityDefinition) source, matchSource);
		}
		else {
			log.error("Match for source {0} is invalid and does not have a type source",
					source.getDefinition());
			return;
		}

		// TODO conditions
	}

	@SuppressWarnings("unused")
	private void mergeJoinSource(MutableCell cell, EntityDefinition source, Cell match,
			Cell originalCell, SimpleLog log, JoinContext context, boolean groovy) {
		/*
		 * Sources: Add all from match (should be at least two)
		 */
		addSources(cell, source, match, log);

		/*
		 * Join order: Replace type with matched sources (use match join order)
		 */
		JoinParameter matchParameter = CellUtil
				.getFirstParameter(match, JoinFunction.PARAMETER_JOIN).as(JoinParameter.class);
		if (matchParameter != null) {
			context.addOrderReplacement((TypeEntityDefinition) source, matchParameter.types
					.toArray(new TypeEntityDefinition[matchParameter.types.size()]));
		}

		// TODO conditions

		// add join match to context (for match conditions)
		context.addJoinMatch(match);
	}

	private void addSources(MutableCell cell, EntityDefinition source, Cell match, SimpleLog log) {
		if (match.getSource() != null) {
			ListMultimap<String, Entity> sources = ArrayListMultimap.create(cell.getSource());
			for (Entry<String, ? extends Entity> entry : match.getSource().entries()) {
				if (!sources.containsEntry(entry.getKey(), entry.getValue())) {
					// add if not already present
					sources.put(entry.getKey(), entry.getValue());
				}
			}
			cell.setSource(sources);
		}
		else {
			log.error("Match for source {0} is invalid and does not have a source",
					source.getDefinition());
			return;
		}
	}

	@Override
	protected JoinContext newContext(Cell originalCell) {
		return new JoinContext(originalCell);
	}

	@Override
	protected void finalize(MutableCell newCell, JoinContext context, SimpleLog log) {
		context.apply(newCell, log);
	}

}
