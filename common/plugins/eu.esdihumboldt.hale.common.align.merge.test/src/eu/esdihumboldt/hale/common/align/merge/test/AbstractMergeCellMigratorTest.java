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

package eu.esdihumboldt.hale.common.align.merge.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.junit.Assert;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Iterables;

import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil;
import eu.esdihumboldt.hale.common.align.merge.MergeCellMigrator;
import eu.esdihumboldt.hale.common.align.merge.MergeIndex;
import eu.esdihumboldt.hale.common.align.merge.extension.MigratorExtension;
import eu.esdihumboldt.hale.common.align.merge.impl.DefaultMergeCellMigrator;
import eu.esdihumboldt.hale.common.align.merge.impl.MatchingMigration;
import eu.esdihumboldt.hale.common.align.merge.impl.TargetIndex;
import eu.esdihumboldt.hale.common.align.migrate.AlignmentMigration;
import eu.esdihumboldt.hale.common.align.migrate.CellMigrator;
import eu.esdihumboldt.hale.common.align.migrate.MigrationOptions;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.annotations.messages.Message;
import eu.esdihumboldt.hale.common.align.model.annotations.messages.MessageDescriptor;
import eu.esdihumboldt.hale.common.cli.HaleCLIUtil;
import eu.esdihumboldt.hale.common.core.HalePlatform;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.core.report.SimpleLog;
import eu.esdihumboldt.hale.common.headless.impl.ProjectTransformationEnvironment;

/**
 * Base class for {@link MergeCellMigrator} tests.
 * 
 * @author Simon Templer
 */
public abstract class AbstractMergeCellMigratorTest {

	/**
	 * Cache of loaded projects
	 */
	protected final LoadingCache<URI, ProjectTransformationEnvironment> projectCache = CacheBuilder
			.newBuilder().build(new CacheLoader<URI, ProjectTransformationEnvironment>() {

				@Override
				public ProjectTransformationEnvironment load(URI key) throws Exception {
					return loadProject(key);
				}

			});

	/**
	 * Load a transformation project.
	 * 
	 * @param uri the project location
	 * @return the loaded project
	 * @throws IOException if loading the project fails
	 */
	private static ProjectTransformationEnvironment loadProject(URI uri) throws IOException {
		return new ProjectTransformationEnvironment(null, new DefaultInputSupplier(uri),
				HaleCLIUtil.createReportHandler());
	}

	/**
	 * Perform merging using a specific merge migrator.
	 * 
	 * @param migrator the migrator to test, <code>null</code> if the migrator
	 *            configured in the system should be used
	 * @param cellToMigrate the cell to migrate
	 * @param matchingProject the project providing the matching information
	 * @return the merge result
	 */
	protected List<MutableCell> mergeWithMigrator(MergeCellMigrator migrator, Cell cellToMigrate,
			ProjectTransformationEnvironment matchingProject) {
		MergeIndex mergeIndex = new TargetIndex(matchingProject.getAlignment());
		AlignmentMigration migration = new MatchingMigration(matchingProject, true);
		List<MutableCell> cells = new ArrayList<>();

		if (migrator == null) {
			try {
				migrator = MigratorExtension.getInstance()
						.getMigrator(cellToMigrate.getTransformationIdentifier()).orElse(null);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		if (migrator == null) {
			CellMigrator mig = getCellMigrator(cellToMigrate.getTransformationIdentifier());
			if (mig instanceof MergeCellMigrator) {
				migrator = (MergeCellMigrator) mig;
			}
			else if (mig == null) {
				throw new IllegalStateException("No cell migrator could be retrieved");
			}
			else {
				// perform migration with "ordinary" CellMigrator
				MigrationOptions options = new MigrationOptions() {

					@Override
					public boolean updateTarget() {
						return false;
					}

					@Override
					public boolean updateSource() {
						return true;
					}

					@Override
					public boolean transferBase() {
						return false;
					}
				};
				mig.updateCell(cellToMigrate, migration, options, SimpleLog.CONSOLE_LOG);
			}
		}
		if (migrator != null) {
			// perform merge with MergeCellMigrator
			Iterable<MutableCell> result = migrator.mergeCell(cellToMigrate, mergeIndex, migration,
					this::getCellMigrator, SimpleLog.CONSOLE_LOG);
			Iterables.addAll(cells, result);
		}

		return cells;
	}

	/**
	 * Perform merging using a specific merge migrator.
	 * 
	 * @param migrator the migrator to test, <code>null</code> if the migrator
	 *            configured in the system should be used
	 * @param cellId the ID of the cell to migrate
	 * @param projectToMigrate the location of the project containing the cell
	 *            (Mapping from B to C)
	 * @param matchingProject the location of the project providing the matching
	 *            (Mapping from A to B) information
	 * @return the merge result
	 * @throws Exception if preparing or running the test fails
	 */
	public List<MutableCell> mergeWithMigrator(MergeCellMigrator migrator, String cellId,
			URL projectToMigrate, URL matchingProject) throws Exception {
		ProjectTransformationEnvironment projectToMigrateEnv = projectCache
				.get(projectToMigrate.toURI());
		Alignment migrateAlignment = projectToMigrateEnv.getAlignment();
		Cell cellToMigrate = migrateAlignment.getCell(cellId);
		assertNotNull(
				MessageFormat.format("Cell with ID {0} not found in alignment to migrate", cellId),
				cellToMigrate);

		ProjectTransformationEnvironment matchingProjectEnv = projectCache
				.get(matchingProject.toURI());
		return mergeWithMigrator(migrator, cellToMigrate, matchingProjectEnv);
	}

	/**
	 * Perform merging using the configured cell migrator.
	 * 
	 * @param cellId the ID of the cell to migrate
	 * @param projectToMigrate the location of the project containing the cell
	 *            (Mapping from B to C)
	 * @param matchingProject the location of the project providing the matching
	 *            (Mapping from A to B) information
	 * @return the merge result
	 * @throws Exception if preparing or running the test fails
	 */
	public List<MutableCell> merge(String cellId, URL projectToMigrate, URL matchingProject)
			throws Exception {
		return mergeWithMigrator(null, cellId, projectToMigrate, matchingProject);
	}

	/**
	 * Get a loaded transformation project.
	 * 
	 * @param location the project location
	 * @return the loaded project
	 * @throws Exception if loading the project fails
	 */
	protected ProjectTransformationEnvironment getProject(URL location) throws Exception {
		return projectCache.get(location.toURI());
	}

	/**
	 * Retrieve the default cell migrator for a given transformation.
	 * 
	 * @param transformationIdentifier the identifier of the transformation
	 *            function
	 * @return the cell migrator
	 */
	protected CellMigrator getCellMigrator(String transformationIdentifier) {
		return FunctionUtil.getFunction(transformationIdentifier, HalePlatform.getServiceProvider())
				.getCustomMigrator().orElse(new DefaultMergeCellMigrator());
	}

	// test helpers

	/**
	 * Check if the given cell's target matches the expected target.
	 * 
	 * @param cell the cell to check
	 * @param targetDef the expected target entity (simple definition as name
	 *            list)
	 */
	protected void assertCellTargetEquals(Cell cell, List<String> targetDef) {
		Entity entity = CellUtil.getFirstEntity(cell.getTarget());
		assertNotNull("Target entity", entity);
		EntityDefinition def = entity.getDefinition();
		assertDefEquals(targetDef, def);
	}

	/**
	 * Check if the given cell's sources match the expected sources (order does
	 * not matter).
	 * 
	 * @param cell the cell to check
	 * @param expected the expected source entities (simple definition as name
	 *            list)
	 */
	protected void assertCellSourcesEqual(Cell cell,
			@SuppressWarnings("unchecked") List<String>... expected) {
		assertNotNull("Source entities", cell.getSource());

		List<List<String>> expList = new ArrayList<>(Arrays.asList(expected));

		for (Entry<String, ? extends Entity> source : cell.getSource().entries()) {
			List<String> match = assertDefEqualsOneOf(expList, source.getValue().getDefinition());
			expList.remove(match);
		}

		assertTrue(MessageFormat.format("Sources {0} expected but not found", expList),
				expList.isEmpty());
	}

	/**
	 * Covnert an entity definition to a representation as simple name list
	 * definition.
	 * 
	 * @param def the entity definition
	 * @return the simple definition representation
	 */
	protected List<String> toSimpleDef(EntityDefinition def) {
		List<String> names = new ArrayList<>();
		names.add(def.getType().getName().getLocalPart());
		for (ChildContext child : def.getPropertyPath()) {
			names.add(child.getChild().getName().getLocalPart());
		}
		return names;
	}

	/**
	 * Check if an entity equals the given simple definition
	 * 
	 * @param expected the expected entity (simple definition as name list)
	 * @param def the entity definition to check
	 */
	protected void assertDefEquals(List<String> expected, EntityDefinition def) {
		List<String> names = toSimpleDef(def);

		assertEquals(expected, names);
	}

	/**
	 * Check if an entity equals the given simple definition
	 * 
	 * @param expected the list of expected entities (each a simple definition
	 *            as name list)
	 * @param def the entity definition to check
	 * @return the match from the expected entities
	 */
	protected List<String> assertDefEqualsOneOf(List<List<String>> expected, EntityDefinition def) {
		List<String> names = toSimpleDef(def);

		for (List<String> exp : expected) {
			if (names.equals(exp)) {
				return exp;
			}
		}

		Assert.fail(MessageFormat.format("No match for entity {0} found in expected entities {1}",
				names, expected));
		return null;
	}

	/**
	 * Get the list of migration messages from the cell.
	 * 
	 * @param cell the cell
	 * @return the list of migration messages
	 */
	protected List<Message> getMigrationMessages(Cell cell) {
		return cell.getAnnotations(MessageDescriptor.ID).stream()
				// only messages in the right category
				.filter(msg -> msg instanceof Message
						&& CellMigrator.CELL_LOG_CATEGORY.equals(((Message) msg).getCategory()))
				.map(msg -> (Message) msg).collect(Collectors.toList());
	}

}
