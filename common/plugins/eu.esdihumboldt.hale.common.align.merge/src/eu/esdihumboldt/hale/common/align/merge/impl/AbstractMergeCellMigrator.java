/*
 * Copyright (c) 2017 wetransform GmbH
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

package eu.esdihumboldt.hale.common.align.merge.impl;

import static eu.esdihumboldt.hale.common.align.migrate.util.MigrationUtil.isDirectMatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.merge.MergeCellMigrator;
import eu.esdihumboldt.hale.common.align.merge.MergeIndex;
import eu.esdihumboldt.hale.common.align.migrate.AlignmentMigration;
import eu.esdihumboldt.hale.common.align.migrate.CellMigrator;
import eu.esdihumboldt.hale.common.align.migrate.MigrationOptions;
import eu.esdihumboldt.hale.common.align.migrate.impl.DefaultCellMigrator;
import eu.esdihumboldt.hale.common.align.migrate.impl.MigrationOptionsImpl;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.annotations.messages.CellLog;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultCell;
import eu.esdihumboldt.hale.common.core.report.SimpleLog;

/**
 * Cell merger base class.
 * 
 * @author Simon Templer
 * @param <C> the context class
 */
public abstract class AbstractMergeCellMigrator<C> extends DefaultCellMigrator
		implements MergeCellMigrator {

	@Override
	public Iterable<MutableCell> mergeCell(Cell originalCell, MergeIndex mergeIndex,
			AlignmentMigration migration, Function<String, CellMigrator> getCellMigrator,
			SimpleLog log) {
		MutableCell copy = new DefaultCell(originalCell);

		// update source entities
		if (copy.getSource() != null && !copy.getSource().isEmpty()) {
			ListMultimap<String, ? extends Entity> sources = copy.getSource();
			return mergeSources(sources, mergeIndex, originalCell, migration, getCellMigrator, log);
		}
		else {
			// no sources - return original
			return Collections.singleton(copy);
		}
	}

	/**
	 * Create a new context object.
	 * 
	 * @param originalCell the original cell
	 * 
	 * @return the new context object
	 */
	protected abstract C newContext(Cell originalCell);

	/**
	 * Update the cell sources.
	 * 
	 * @param sources the old sources
	 * @param mergeIndex the merge index
	 * @param originalCell the original cell
	 * @param migration the alignment migration (may be useful for cases where
	 *            only entity replacement needs to be done)
	 * @param getCellMigrator functions that yields a cell migrator for a
	 *            function (may be useful for cases where only entity
	 *            replacement needs to be done)
	 * @param log the migration process log
	 * @return the merged cell or cells
	 */
	protected Iterable<MutableCell> mergeSources(ListMultimap<String, ? extends Entity> sources,
			MergeIndex mergeIndex, Cell originalCell, AlignmentMigration migration,
			Function<String, CellMigrator> getCellMigrator, SimpleLog log) {
		boolean transferBase = true; // XXX relevant here at all?

		if (sources.size() == 1) {
			EntityDefinition source = sources.values().iterator().next().getDefinition();
			List<Cell> matches = mergeIndex.getCellsForTarget(source);
			if (!matches.isEmpty()) {
				List<MutableCell> cells = new ArrayList<>();
				for (Cell match : matches) {
					// create a result cell for each match

					// if the matching is a Retype/Rename, replace source of
					// the original cell
					if (isDirectMatch(match)) {
						MigrationOptions replaceSource = new MigrationOptionsImpl(true, false,
								transferBase);
						cells.add(getCellMigrator.apply(originalCell.getTransformationIdentifier())
								.updateCell(originalCell, migration, replaceSource, log));
					}
					// if the cell is a Retype/Rename, replace the target of
					// matching cell
					else if (isDirectMatch(originalCell)) {
						// FIXME respect any conditions/contexts on the original
						// source?
						// XXX at least try to transfer them
						MigrationOptions replaceTarget = new MigrationOptionsImpl(false, true,
								transferBase);
						AlignmentMigration cellMigration = new AbstractMigration() {

							@Override
							protected Optional<EntityDefinition> findMatch(
									EntityDefinition entity) {
								Entity target = CellUtil.getFirstEntity(originalCell.getTarget());
								if (target != null) {
									return Optional.ofNullable(target.getDefinition());
								}
								return Optional.empty();
							}
						};
						cells.add(getCellMigrator.apply(match.getTransformationIdentifier())
								.updateCell(match, cellMigration, replaceTarget, log));
					}
					else {
						// otherwise, use custom logic to try to combine cells

						MutableCell newCell = new DefaultCell(originalCell);
						SimpleLog cellLog = SimpleLog.all(log,
								new CellLog(newCell, CELL_LOG_CATEGORY));
						C context = newContext(originalCell);

						// reset source
						newCell.setSource(ArrayListMultimap.create());

						mergeSource(newCell, sources.keys().iterator().next(), source, match,
								originalCell, cellLog, context, migration, mergeIndex);

						finalize(newCell, migration, context, log);

						cells.add(newCell);
					}
				}

				return cells;
			}
			else {
				// no match -> remove?
				// rather add original + documentation
				MutableCell newCell = new DefaultCell(originalCell);
				SimpleLog cellLog = SimpleLog.all(log, new CellLog(newCell, CELL_LOG_CATEGORY));

				cellLog.warn("No match for source found, dropping cell "
						+ CellUtil.getCellDescription(originalCell, null));

				return Collections.emptyList();
			}
		}
		else {
			// handle each source
			MutableCell newCell = new DefaultCell(originalCell);
			SimpleLog cellLog = SimpleLog.all(log, new CellLog(newCell, CELL_LOG_CATEGORY));
			C context = newContext(originalCell);

			// reset source
			newCell.setSource(ArrayListMultimap.create());

			for (Entry<String, ? extends Entity> source : sources.entries()) {
				List<Cell> matches = mergeIndex
						.getCellsForTarget(source.getValue().getDefinition());
				if (!matches.isEmpty()) {
					Cell match = matches.get(0);

					mergeSource(newCell, source.getKey(), source.getValue().getDefinition(), match,
							originalCell, cellLog, context, migration, mergeIndex);

					if (matches.size() > 1) {
						// FIXME
						cellLog.warn("Multiple matches for source {0}, only one was handled",
								source.getValue().getDefinition());
					}
				}
				else {
					// not match, just not add source?
					// FIXME definitely add comment
				}
			}

			finalize(newCell, migration, context, log);

			return Collections.singleton(newCell);
		}
	}

	/**
	 * Finalize a created cell based on the context.
	 * 
	 * @param newCell the new merged cell
	 * @param migration the alignment migration
	 * @param context the cell merge context
	 * @param log the cell log
	 */
	protected void finalize(MutableCell newCell, AlignmentMigration migration, C context,
			SimpleLog log) {
		// override me
	}

	/**
	 * Merge a source according to a matching cell.
	 * 
	 * @param cell the target cell to merge the source to (contains only already
	 *            merged sources)
	 * @param sourceName the name of the source
	 * @param source the source definition
	 * @param match the match for the source
	 * @param originalCell the original cell
	 * @param log the migration process log
	 * @param context the context for the creation of the cell (possibly shared
	 *            across multiple calls to
	 *            {@link #mergeSource(MutableCell, String, EntityDefinition, Cell, Cell, SimpleLog, Object, AlignmentMigration, MergeIndex)}
	 *            )
	 * @param migration the alignment migration
	 * @param mergeIndex the merge index
	 */
	protected void mergeSource(MutableCell cell, String sourceName, EntityDefinition source,
			Cell match, Cell originalCell, SimpleLog log, C context, AlignmentMigration migration,
			MergeIndex mergeIndex) {
		// override me
		log.warn("Unsupported combination {0} / {1} for source {2}",
				match.getTransformationIdentifier(), cell.getTransformationIdentifier(), source);
	}

}
