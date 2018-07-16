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

import eu.esdihumboldt.cst.functions.groovy.GroovyConstants;
import eu.esdihumboldt.cst.functions.groovy.GroovyJoin;
import eu.esdihumboldt.cst.functions.groovy.GroovyRetype;
import eu.esdihumboldt.hale.common.align.merge.MergeIndex;
import eu.esdihumboldt.hale.common.align.merge.impl.AbstractMergeCellMigrator;
import eu.esdihumboldt.hale.common.align.merge.impl.AbstractMigration;
import eu.esdihumboldt.hale.common.align.migrate.AlignmentMigration;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.functions.JoinFunction;
import eu.esdihumboldt.hale.common.align.model.functions.RetypeFunction;
import eu.esdihumboldt.hale.common.align.model.functions.join.JoinParameter;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.core.io.Text;
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
		case GroovyJoin.ID:
			groovy = true;
		case JoinFunction.ID:
			mergeJoinSource(cell, source, match, originalCell, log, context, groovy, migration);
			break;
		case GroovyRetype.ID:
			groovy = true;
		case RetypeFunction.ID:
			mergeRetypeSource(cell, source, match, originalCell, log, context, groovy, migration);
			break;
		default:
			// fall-back
			super.mergeSource(cell, sourceName, source, match, originalCell, log, context,
					migration, mergeIndex);
		}
	}

	@SuppressWarnings("unused")
	private void mergeRetypeSource(MutableCell cell, EntityDefinition source, Cell match,
			Cell originalCell, SimpleLog log, JoinContext context, boolean groovy,
			AlignmentMigration migration) {
		/*
		 * Sources: Add all from match (should be one)
		 */
		addSources(cell, source, match, migration, log, true);

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

		if (groovy) {
			addScript(match, context);
		}
	}

	@SuppressWarnings("unused")
	private void mergeJoinSource(MutableCell cell, EntityDefinition source, Cell match,
			Cell originalCell, SimpleLog log, JoinContext context, boolean groovy,
			AlignmentMigration migration) {
		/*
		 * Sources: Add all from match (should be at least two)
		 */
		addSources(cell, source, match, migration, log, false);

		// add source that was replaced and filter/contexts were not retained
		context.addStrippedSource(source);

		/*
		 * Join order: Replace type with matched sources (use match join order)
		 */
		JoinParameter matchParameter = CellUtil
				.getFirstParameter(match, JoinFunction.PARAMETER_JOIN).as(JoinParameter.class);
		if (matchParameter != null) {
			context.addOrderReplacement((TypeEntityDefinition) source, matchParameter.getTypes()
					.toArray(new TypeEntityDefinition[matchParameter.getTypes().size()]));
		}

		// add join match to context (for match conditions)
		context.addJoinMatch(match);

		if (groovy) {
			addScript(match, context);
		}
	}

	private void addScript(Cell match, JoinContext context) {
		ParameterValue scriptValue = CellUtil.getFirstParameter(match,
				GroovyConstants.PARAMETER_SCRIPT);
		if (scriptValue != null) {
			String script;
			// try retrieving as text
			Text text = scriptValue.as(Text.class);
			if (text != null) {
				script = text.getText();
			}
			else {
				// fall back to string value
				script = scriptValue.as(String.class);
			}

			context.addGroovyScript(match, script);
		}
	}

	private void addSources(MutableCell cell, EntityDefinition source, Cell match,
			AlignmentMigration migration, SimpleLog log, boolean transferContext) {
		if (match.getSource() != null) {
			ListMultimap<String, Entity> sources = ArrayListMultimap.create(cell.getSource());
			for (Entry<String, ? extends Entity> entry : match.getSource().entries()) {
				Entity entity = entry.getValue();

				if (transferContext) {
					// transfer filter and contexts if possible
					EntityDefinition withContexts = AbstractMigration.translateContexts(source,
							entity.getDefinition(), migration, log);
					entity = AlignmentUtil.createEntity(withContexts);
				}

				if (!sources.containsEntry(entry.getKey(), entity)) {
					// add if not already present
					sources.put(entry.getKey(), entity);
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
		JoinContext ctx = new JoinContext(originalCell);

		if (GroovyJoin.ID.equals(originalCell.getTransformationIdentifier())) {
			addScript(originalCell, ctx);
		}

		return ctx;
	}

	@Override
	protected void finalize(MutableCell newCell, AlignmentMigration migration, JoinContext context,
			SimpleLog log) {
		context.apply(newCell, migration, log);
	}

}
