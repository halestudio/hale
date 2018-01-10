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

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Iterables;

import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil;
import eu.esdihumboldt.hale.common.align.merge.MergeCellMigrator;
import eu.esdihumboldt.hale.common.align.merge.MergeIndex;
import eu.esdihumboldt.hale.common.align.merge.impl.DefaultMergeCellMigrator;
import eu.esdihumboldt.hale.common.align.merge.impl.MatchingMigration;
import eu.esdihumboldt.hale.common.align.merge.impl.TargetIndex;
import eu.esdihumboldt.hale.common.align.migrate.AlignmentMigration;
import eu.esdihumboldt.hale.common.align.migrate.CellMigrator;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
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
	public static ProjectTransformationEnvironment loadProject(URI uri) throws IOException {
		return new ProjectTransformationEnvironment(null, new DefaultInputSupplier(uri),
				HaleCLIUtil.createReportHandler());
	}

	/**
	 * Perform merging using a specific merge migrator.
	 * 
	 * @param migrator the migrator to test
	 * @param cellToMigrate the cell to migrate
	 * @param matchingProject the project providing the matching information
	 * @return the merge result
	 */
	protected Collection<MutableCell> runMergeMigrator(MergeCellMigrator migrator,
			Cell cellToMigrate, ProjectTransformationEnvironment matchingProject) {
		MergeIndex mergeIndex = new TargetIndex(matchingProject.getAlignment());
		AlignmentMigration migration = new MatchingMigration(matchingProject, true);
		Iterable<MutableCell> result = migrator.mergeCell(cellToMigrate, mergeIndex, migration,
				this::getCellMigrator, SimpleLog.CONSOLE_LOG);
		List<MutableCell> cells = new ArrayList<>();
		Iterables.addAll(cells, result);
		return cells;
	}

	/**
	 * Perform merging using a specific merge migrator.
	 * 
	 * @param migrator the migrator to test
	 * @param cellId the ID of the cell to migrate
	 * @param projectToMigrate the location of the project containing the cell
	 *            (Mapping from B to C)
	 * @param matchingProject the location of the project providing the matching
	 *            (Mapping from A to B) information
	 * @return the merge result
	 * @throws Exception if preparing or running the test fails
	 */
	public Collection<MutableCell> runMergeMigrator(MergeCellMigrator migrator, String cellId,
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
		return runMergeMigrator(migrator, cellToMigrate, matchingProjectEnv);
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

}
