/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.align.io.impl.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Strings;

import eu.esdihumboldt.hale.common.align.extension.function.custom.CustomPropertyFunction;
import eu.esdihumboldt.hale.common.align.migrate.impl.UnmigratedCell;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.BaseAlignmentCell;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.ModifiableCell;
import eu.esdihumboldt.hale.common.align.model.MutableAlignment;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.TransformationMode;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultAlignment;
import eu.esdihumboldt.hale.common.core.io.PathUpdate;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;
import eu.esdihumboldt.util.io.IOUtils;

/**
 * Base class for converting alignment representations to alignments.
 * 
 * @author Kai Schwierczek
 * @param <A> the alignment representation type
 * @param <C> the cell representation type
 * @param <M> the cell modifier representation type
 */
public abstract class AbstractBaseAlignmentLoader<A, C, M> {

	/**
	 * Load a alignment representation from the given stream. This method must
	 * close the stream after it is done.
	 * 
	 * @param in the input stream
	 * @param reporter the I/O reporter to report any errors to, may be
	 *            <code>null</code>
	 * @return a alignment representation
	 * @throws IOException if some kind of exception occurs while loading the
	 *             alignment
	 */
	protected abstract A loadAlignment(InputStream in, IOReporter reporter) throws IOException;

	/**
	 * Returns a map of prefix, URI pairs of base alignments for the given
	 * alignment. The returned map must be modifiable.
	 * 
	 * @param alignment the alignment representation in question
	 * @return a map of prefix, URI pairs of base alignments
	 */
	protected abstract Map<String, URI> getBases(A alignment);

	/**
	 * Returns a collection of cell representations of the given alignment
	 * representation.
	 * 
	 * @param alignment the alignment representation in question
	 * @return cell representations
	 */
	protected abstract Collection<C> getCells(A alignment);

	/**
	 * Returns a collection of property function definitions of the given
	 * alignment representation.
	 * 
	 * @param alignment the alignment representation in question
	 * @param sourceTypes the source types to use for resolving definition
	 *            references
	 * @param targetTypes the target types to use for resolving definition
	 *            references
	 * @return list of property functions representations
	 */
	protected abstract Collection<CustomPropertyFunction> getPropertyFunctions(A alignment,
			TypeIndex sourceTypes, TypeIndex targetTypes);

	/**
	 * Returns the cell id of the given cell.
	 * 
	 * @param cell the cell in question
	 * @return the cell id of the given cell
	 */
	protected abstract String getCellId(C cell);

	/**
	 * Create a cell from the given cell representation. Implementations can
	 * return {@link UnmigratedCell}s which will be migrated by this alignment
	 * loader.
	 * 
	 * @param cell the cell representation
	 * @param sourceTypes the source types to use for resolving definition
	 *            references
	 * @param targetTypes the target types to use for resolving definition
	 *            references
	 * @param reporter the I/O reporter to report any errors to, may be
	 *            <code>null</code>
	 * @return a cell for the given cell representation
	 */
	protected abstract MutableCell createCell(C cell, TypeIndex sourceTypes, TypeIndex targetTypes,
			IOReporter reporter);

	/**
	 * Returns a collection of modifier representations of the given alignment
	 * representation.
	 * 
	 * @param alignment the alignment representation in question
	 * @return modifier representations
	 */
	protected abstract Collection<M> getModifiers(A alignment);

	/**
	 * Returns the raw cell id that is modified by the given modifier.
	 * 
	 * @param modifier the modifier representation in question
	 * @return the cell id that is modified
	 */
	protected abstract String getModifiedCell(M modifier);

	/**
	 * Returns the disabled for list of the given modifier representation.
	 * 
	 * @param modifier the modifier representation in question
	 * @return the disabled for list
	 */
	protected abstract Collection<String> getDisabledForList(M modifier);

	/**
	 * Get the transformation mode specified in a modifier.
	 * 
	 * @param modifier the modifier
	 * @return the transformation mode or <code>null</code> if none is specified
	 */
	protected abstract TransformationMode getTransformationMode(M modifier);

	/**
	 * Private class to save alignment information.
	 */
	private class AlignmentInfo {

		AlignmentInfo(String prefix, URIPair uri) {
			this.prefix = prefix;
			this.uri = uri;
		}

		final String prefix;
		final URIPair uri;
	}

	/**
	 * Private class for a pair of URIs. The used URI and the absolute URI.
	 * 
	 * {@link #equals(Object)} and {@link #hashCode()} only use the absolute
	 * URI.
	 */
	private class URIPair {

		URIPair(URI absoluteURI, URI usedURI) {
			this.absoluteURI = absoluteURI;
			this.usedURI = usedURI;
		}

		final URI absoluteURI;
		final URI usedURI;

		/**
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			return 31 + ((absoluteURI == null) ? 0 : absoluteURI.hashCode());
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof AbstractBaseAlignmentLoader.URIPair)
				return absoluteURI.equals(((URIPair) obj).absoluteURI);
			else
				return false;
		}

	}

	/**
	 * Adds the given base alignment to the given alignment.
	 * 
	 * @param alignment the alignment to add a base alignment to
	 * @param newBase URI of the new base alignment
	 * @param projectLocation the project location or <code>null</code>
	 * @param sourceTypes the source types to use for resolving definition
	 *            references
	 * @param targetTypes the target types to use for resolving definition
	 *            references
	 * @param reporter the I/O reporter to report any errors to, may be
	 *            <code>null</code>
	 * @throws IOException if adding the base alignment fails
	 */
	protected final void internalAddBaseAlignment(MutableAlignment alignment, URI newBase,
			URI projectLocation, TypeIndex sourceTypes, TypeIndex targetTypes, IOReporter reporter)
			throws IOException {
		Map<A, Map<String, String>> prefixMapping = new HashMap<A, Map<String, String>>();
		Map<A, AlignmentInfo> alignmentToInfo = new HashMap<A, AlignmentInfo>();

		generatePrefixMapping(newBase, projectLocation, alignment.getBaseAlignments(),
				prefixMapping, alignmentToInfo, reporter);
		processBaseAlignments(alignment, sourceTypes, targetTypes, prefixMapping, alignmentToInfo,
				reporter);
	}

	/**
	 * Creates and adds cells and modifiers of the base alignments to the main
	 * alignment.
	 * 
	 * @param alignment the alignment to add base alignments to
	 * @param sourceTypes the source types to use for resolving definition
	 *            references
	 * @param targetTypes the target types to use for resolving definition
	 *            references
	 * @param prefixMapping gets filled with a mapping from local to global
	 *            prefixes
	 * @param alignmentToInfo gets filled with a mapping from base alignment
	 *            representations to prefixes and URIs
	 * @param reporter the I/O reporter to report any errors to, may be
	 *            <code>null</code>
	 * @throws IOException if one of the base alignments does not have cell ids
	 */
	private void processBaseAlignments(MutableAlignment alignment, TypeIndex sourceTypes,
			TypeIndex targetTypes, Map<A, Map<String, String>> prefixMapping,
			Map<A, AlignmentInfo> alignmentToInfo, IOReporter reporter) throws IOException {
		for (Entry<A, AlignmentInfo> base : alignmentToInfo.entrySet()) {
			Collection<C> baseCells = getCells(base.getKey());
			boolean hasIds = true;
			for (C baseCell : baseCells)
				if (Strings.isNullOrEmpty(getCellId(baseCell))) {
					hasIds = false;
					break;
				}
			if (!hasIds) {
				throw new IOException(
						"At least one base alignment (" + base.getValue().uri.absoluteURI
								+ ") has no cell ids. Please load and save it to generate them.");
			}
		}

		for (Entry<A, AlignmentInfo> base : alignmentToInfo.entrySet()) {
			if (alignment.getBaseAlignments().containsValue(base.getValue().uri.usedURI)) {
				// base alignment already present
				// can currently happen with base alignments included in base
				// alignments
				reporter.warn(new IOMessageImpl("Base alignment at " + base.getValue().uri.usedURI
						+ " has already been added", null));
			}
			else {
				Collection<CustomPropertyFunction> baseFunctions = getPropertyFunctions(
						base.getKey(), sourceTypes, targetTypes);
				Collection<C> baseCells = getCells(base.getKey());
				Collection<BaseAlignmentCell> createdBaseCells = new ArrayList<BaseAlignmentCell>(
						baseCells.size());
				List<MutableCell> createdCells = new ArrayList<>(baseCells.size());
				for (C baseCell : baseCells) {
					// add cells of base alignments
					MutableCell cell = createCell(baseCell, sourceTypes, targetTypes, reporter);
					if (cell != null) {
						createdCells.add(cell);
					}
				}

				// Migrate UnmigratedCells
				migrateCells(createdCells);

				for (MutableCell cell : createdCells) {
					createdBaseCells.add(new BaseAlignmentCell(cell, base.getValue().uri.usedURI,
							base.getValue().prefix));
				}

				alignment.addBaseAlignment(base.getValue().prefix, base.getValue().uri.usedURI,
						createdBaseCells, baseFunctions);
			}
		}

		// add modifiers of base alignments
		for (Entry<A, AlignmentInfo> base : alignmentToInfo.entrySet())
			applyModifiers(alignment, getModifiers(base.getKey()), prefixMapping.get(base.getKey()),
					base.getValue().prefix, true, reporter);
	}

	/**
	 * Function to fill the prefixMapping and alignmentToInfo maps.
	 * 
	 * @param addBase the URI of the new base alignment to add
	 * @param projectLocation the project location or <code>null</code>
	 * @param existingBases the map of existing bases
	 * @param prefixMapping gets filled with a mapping from local to global
	 *            prefixes
	 * @param alignmentToInfo gets filled with a mapping from base alignment
	 *            representations to prefixes and URIs
	 * @param reporter the reporter
	 * @return whether newBase actually is a new base and the add process should
	 *         continue
	 */
	private boolean generatePrefixMapping(URI addBase, URI projectLocation,
			Map<String, URI> existingBases, Map<A, Map<String, String>> prefixMapping,
			Map<A, AlignmentInfo> alignmentToInfo, IOReporter reporter) {
		// Project location may be null if the project wasn't saved yet
		// Then, it still is okay, if all bases are absolute.
		URI currentAbsolute = projectLocation;
		URI usedAddBaseURI = addBase;
		URI absoluteAddBaseURI = resolve(currentAbsolute, usedAddBaseURI);

		// set of already seen URIs
		Set<URI> knownURIs = new HashSet<URI>();

		// reverse map of base
		Map<URI, String> uriToPrefix = new HashMap<URI, String>();

		// create URI to prefix map and known URIs
		for (Entry<String, URI> baseEntry : existingBases.entrySet()) {
			// make sure to use absolute URIs here for comparison
			URI absoluteBase = resolve(currentAbsolute, baseEntry.getValue());
			knownURIs.add(absoluteBase);
			uriToPrefix.put(absoluteBase, baseEntry.getKey());
		}

		if (uriToPrefix.containsKey(absoluteAddBaseURI)) {
			reporter.info(new IOMessageImpl(
					"The base alignment (" + addBase + ") is already included.", null));
			return false;
		}

		Set<String> existingPrefixes = new HashSet<String>(existingBases.keySet());
		String newPrefix = generatePrefix(existingPrefixes);
		existingPrefixes.add(newPrefix);
		uriToPrefix.put(absoluteAddBaseURI, newPrefix);

		/*
		 * XXX Adding a base alignment could only use a PathUpdate for the
		 * movement of the base alignment which is not known in the current
		 * project. Maybe could try the one of the current project either way?
		 */

		// find all alignments to load (also missing ones) and load the beans
		LinkedList<URIPair> queue = new LinkedList<URIPair>();
		queue.add(new URIPair(absoluteAddBaseURI, usedAddBaseURI));
		while (!queue.isEmpty()) {
			URIPair baseURI = queue.pollFirst();
			A baseA;
			try {
				baseA = loadAlignment(new DefaultInputSupplier(baseURI.absoluteURI).getInput(),
						reporter);
			} catch (IOException e) {
				reporter.error(new IOMessageImpl(
						"Couldn't load an included base alignment (" + baseURI.absoluteURI + ").",
						e));
				reporter.setSuccess(false);
				return false;
			}

			// add to alignment info map
			alignmentToInfo.put(baseA,
					new AlignmentInfo(uriToPrefix.get(baseURI.absoluteURI), baseURI));
			prefixMapping.put(baseA, new HashMap<String, String>());

			// load "missing" base alignments, too, add prefix mapping
			for (Entry<String, URI> baseEntry : getBases(baseA).entrySet()) {
				// rawURI may be relative
				URI rawURI = baseEntry.getValue();
				URI absoluteURI = baseURI.absoluteURI.resolve(rawURI);
				URI usedURI = absoluteURI;
				// If the added base alignment URI, the used URI for A
				// and rawURI are relative, continue using a relative URI.
				if (!usedAddBaseURI.isAbsolute() && !baseURI.usedURI.isAbsolute()
						&& !rawURI.isAbsolute()) {
					usedURI = IOUtils.getRelativePath(absoluteURI, currentAbsolute);
				}
				// check whether this base alignment is missing
				if (!knownURIs.contains(absoluteURI)) {
					reporter.info(new IOMessageImpl(
							"A base alignment referenced another base alignment (" + absoluteURI
									+ ") that was not yet known. It is now included, too.",
							null));
					queue.add(new URIPair(absoluteURI, usedURI));
					knownURIs.add(absoluteURI);
					String prefix = generatePrefix(existingPrefixes);
					existingPrefixes.add(prefix);
					uriToPrefix.put(absoluteURI, prefix);
				}
				// add prefix mapping
				prefixMapping.get(baseA).put(baseEntry.getKey(), uriToPrefix.get(absoluteURI));
			}
		}

		return true;
	}

	private URI resolve(URI base, URI relative) {
		if (relative.isAbsolute())
			return relative;
		else
			return base.resolve(relative);
	}

	/**
	 * Creates an alignment from the given alignment representation.
	 * 
	 * @param start the main alignment representation
	 * @param sourceTypes the source types to use for resolving definition
	 *            references
	 * @param targetTypes the target types to use for resolving definition
	 *            references
	 * @param updater the path updater to use for base alignments
	 * @param reporter the I/O reporter to report any errors to, may be
	 *            <code>null</code>
	 * @return the alignment for the given alignment representation
	 * @throws IOException if a base alignment couldn't be loaded
	 */
	protected final MutableAlignment createAlignment(A start, TypeIndex sourceTypes,
			TypeIndex targetTypes, PathUpdate updater, IOReporter reporter) throws IOException {
		Map<A, Map<String, String>> prefixMapping = new HashMap<A, Map<String, String>>();
		Map<A, AlignmentInfo> alignmentToInfo = new HashMap<A, AlignmentInfo>();

		// fill needed maps
		generatePrefixMapping(start, prefixMapping, alignmentToInfo, updater, reporter);

		// create alignment
		DefaultAlignment alignment = new DefaultAlignment();

		// add cells of base alignments
		processBaseAlignments(alignment, sourceTypes, targetTypes, prefixMapping, alignmentToInfo,
				reporter);

		loadCustomFunctions(start, alignment, sourceTypes, targetTypes);

		// collect cells for main alignment
		List<MutableCell> cells = new ArrayList<>();
		for (C mainCell : getCells(start)) {
			MutableCell cell = createCell(mainCell, sourceTypes, targetTypes, reporter);
			if (cell != null) {
				cells.add(cell);
			}
		}

		// Migrate all UnmigratedCells and add them to the alignment
		List<MutableCell> migratedCells = migrateCells(cells);
		migratedCells.forEach(cell -> alignment.addCell(cell));

		// add modifiers of main alignment
		applyModifiers(alignment, getModifiers(start), prefixMapping.get(start), null, false,
				reporter);

		return alignment;
	}

	private List<MutableCell> migrateCells(List<MutableCell> cells) {
		List<MutableCell> result = new ArrayList<>();

		// Collect mappings from all UnmigratedCells
		Map<EntityDefinition, EntityDefinition> allMappings = new HashMap<>();
		cells.stream().filter(c -> c instanceof UnmigratedCell).map(c -> (UnmigratedCell) c)
				.forEach(uc -> allMappings.putAll(uc.getEntityMappings()));

		// Add cells to the alignment, migrate UnmigratedCells
		for (MutableCell cell : cells) {
			if (cell instanceof UnmigratedCell) {
				result.add(((UnmigratedCell) cell).migrate(allMappings));
			}
			else {
				result.add(cell);
			}
		}

		return result;
	}

	/**
	 * Load custom functions and add them to the alignment.
	 * 
	 * @param source the alignment source
	 * @param alignment the alignment
	 * @param sourceTypes the source types
	 * @param targetTypes the target types
	 */
	protected void loadCustomFunctions(A source, DefaultAlignment alignment, TypeIndex sourceTypes,
			TypeIndex targetTypes) {
		Collection<CustomPropertyFunction> functions = getPropertyFunctions(source, sourceTypes,
				targetTypes);
		for (CustomPropertyFunction cf : functions) {
			alignment.addCustomPropertyFunction(cf);
		}
	}

	/**
	 * Function to fill the prefixMapping and alignmentToInfo maps.
	 * 
	 * @param start the main alignment representation
	 * @param prefixMapping gets filled with a mapping from local to global
	 *            prefixes
	 * @param alignmentToInfo gets filled with a mapping from base alignment
	 *            representations to prefixes and URIs
	 * @param updater the location updater to use for base alignments
	 * @param reporter the reporter
	 * @throws IOException if a base alignment couldn't be loaded
	 */
	private void generatePrefixMapping(A start, Map<A, Map<String, String>> prefixMapping,
			Map<A, AlignmentInfo> alignmentToInfo, PathUpdate updater, IOReporter reporter)
			throws IOException {
		// XXX What if the project file path would change?
		// Alignment is a project file, so it is in the same directory.
		URI currentAbsolute = updater.getNewLocation();

		Map<String, URI> base = getBases(start);

		// also a mapping for this alignment itself in case the same URI is
		// defined for two prefixes
		prefixMapping.put(start, new HashMap<String, String>());

		// set of already seen URIs
		Set<URI> knownURIs = new HashSet<URI>();

		// reverse map of base
		Map<URI, String> uriToPrefix = new HashMap<URI, String>();

		// queue of base alignments to process
		LinkedList<URIPair> queue = new LinkedList<URIPair>();

		// check base for doubles, and invert it for later
		for (Entry<String, URI> baseEntry : base.entrySet()) {
			URI rawBaseURI = baseEntry.getValue();
			URI usedBaseURI = updater.findLocation(rawBaseURI, true, false, true);
			if (usedBaseURI == null) {
				throw new IOException(
						"Couldn't load an included alignment (" + rawBaseURI + "). File not found.",
						null);
			}
			URI absoluteBaseURI = usedBaseURI;
			if (!absoluteBaseURI.isAbsolute())
				absoluteBaseURI = currentAbsolute.resolve(absoluteBaseURI);

			if (knownURIs.contains(absoluteBaseURI)) {
				reporter.warn(new IOMessageImpl(
						"The same base alignment (" + rawBaseURI + ") was included twice.", null));
				prefixMapping.get(start).put(baseEntry.getKey(), uriToPrefix.get(absoluteBaseURI));
			}
			else {
				knownURIs.add(absoluteBaseURI);
				prefixMapping.get(start).put(baseEntry.getKey(), baseEntry.getKey());
				uriToPrefix.put(absoluteBaseURI, baseEntry.getKey());
				queue.add(new URIPair(absoluteBaseURI, usedBaseURI));
			}
		}

		// find all alignments to load (also missing ones) and load the beans
		while (!queue.isEmpty()) {
			URIPair baseURI = queue.pollFirst();
			A baseA;
			try {
				baseA = loadAlignment(new DefaultInputSupplier(baseURI.absoluteURI).getInput(),
						reporter);
			} catch (IOException e) {
				throw new IOException("Couldn't load an included alignment (" + baseURI + ").", e);
			}

			// add to alignment info map
			alignmentToInfo.put(baseA,
					new AlignmentInfo(uriToPrefix.get(baseURI.absoluteURI), baseURI));
			prefixMapping.put(baseA, new HashMap<String, String>());

			// load "missing" base alignments, too, add prefix mapping
			for (Entry<String, URI> baseEntry : getBases(baseA).entrySet()) {
				// rawURI may be relative
				URI rawURI = baseEntry.getValue();
				URI absoluteURI = baseURI.absoluteURI.resolve(rawURI);
				// try updater again, it might help, and it shows whether the
				// file is readable
				absoluteURI = updater.findLocation(absoluteURI, true, false, false);
				if (absoluteURI == null)
					throw new IOException("Couldn't find an included alignment (" + rawURI + ").");
				URI usedURI = absoluteURI;
				// If the used URI for A and rawURI are relative, continue using
				// a relative URI.
				if (!baseURI.usedURI.isAbsolute() && !rawURI.isAbsolute())
					usedURI = IOUtils.getRelativePath(absoluteURI, currentAbsolute);

				if (!knownURIs.contains(absoluteURI)) {
					reporter.info(new IOMessageImpl(
							"A base alignment referenced another base alignment (" + absoluteURI
									+ ") that was not yet known. It is now included, too.",
							null));
					queue.add(new URIPair(absoluteURI, usedURI));
					knownURIs.add(absoluteURI);
					String prefix = generatePrefix(base.keySet());
					base.put(prefix, usedURI);
					uriToPrefix.put(absoluteURI, prefix);
					prefixMapping.get(start).put(prefix, prefix);
				}
				// add prefix mapping
				prefixMapping.get(baseA).put(baseEntry.getKey(), uriToPrefix.get(absoluteURI));
			}
		}
	}

	/**
	 * Apply modifiers on the alignment.
	 * 
	 * @param alignment the alignment to work on
	 * @param modifiers the modifiers to apply
	 * @param prefixMapping the mapping of prefixes (see
	 *            {@link #getCell(Alignment, String, String, Map, IOReporter)})
	 * @param defaultPrefix the default prefix (may be <code>null</code>) (see
	 *            {@link #getCell(Alignment, String, String, Map, IOReporter)})
	 * @param base whether the added modifiers are from a base alignment or the
	 *            main alignment
	 * @param reporter the I/O reporter to report any errors to, may be
	 *            <code>null</code>
	 */
	private void applyModifiers(Alignment alignment, Collection<M> modifiers,
			Map<String, String> prefixMapping, String defaultPrefix, boolean base,
			IOReporter reporter) {
		for (M modifier : modifiers) {
			Cell cell = getCell(alignment, getModifiedCell(modifier), defaultPrefix, prefixMapping,
					reporter);
			if (cell == null)
				continue;

			// disabledFor
			for (String disabledForId : getDisabledForList(modifier)) {
				Cell other = getCell(alignment, disabledForId, defaultPrefix, prefixMapping,
						reporter);
				if (other == null)
					continue;
				else if (!AlignmentUtil.isTypeCell(other)) {
					reporter.warn(new IOMessageImpl(
							"A cell referenced in disable-for is not a type cell.", null));
					continue;
				}
				else if (!alignment.getPropertyCells(other, true, false).contains(cell)) {
					reporter.warn(new IOMessageImpl(
							"A cell referenced in disable-for does not contain the cell that gets modified.",
							null));
					continue;
				}

				// base is true -> modified cell has to be of a base alignment
				// so it has to be a BaseAlignmentCell
				if (base)
					((BaseAlignmentCell) cell).setBaseDisabledFor(other, true);
				else
					((ModifiableCell) cell).setDisabledFor(other, true);
			}

			// transformation mode
			TransformationMode mode = getTransformationMode(modifier);
			if (mode != null) {
				if (base)
					((BaseAlignmentCell) cell).setBaseTransformationMode(mode);
				else
					((ModifiableCell) cell).setTransformationMode(mode);
			}

			// XXX handle additional properties
		}
	}

	/**
	 * Returns the cell in question or null, if it could not be found in which
	 * case a suitable warning was generated.
	 * 
	 * @param alignment the alignment which contains the cell
	 * @param cellId the cell id
	 * @param defaultPrefix the prefix to use if the cell id does not contain a
	 *            prefix, may be <code>null</code>
	 * @param prefixMapping the prefix map to transform the prefix of the cell
	 *            id with, if it has one
	 * @param reporter the I/O reporter to report any errors to, may be
	 *            <code>null</code>
	 * @return the cell in question or <code>null</code>
	 */
	private Cell getCell(Alignment alignment, String cellId, String defaultPrefix,
			Map<String, String> prefixMapping, IOReporter reporter) {
		String prefix = defaultPrefix;
		// check if the cell id references another base alignment
		int prefixSplit = cellId.indexOf(':');
		if (prefixSplit != -1) {
			prefix = prefixMapping.get(cellId.substring(0, prefixSplit));
			if (prefix == null) {
				reporter.warn(new IOMessageImpl("A modifier used an unknown cell prefix", null));
				return null;
			}
			cellId = cellId.substring(prefixSplit + 1);
		}

		if (prefix != null)
			cellId = prefix + ':' + cellId;
		Cell cell = alignment.getCell(cellId);
		if (cell == null)
			reporter.warn(
					new IOMessageImpl("A cell referenced by a modifier could not be found", null));

		return cell;
	}

	/**
	 * Generates a new prefix.
	 * 
	 * @param prefixes the existing prefixes
	 * @return a new prefix
	 */
	private String generatePrefix(Set<String> prefixes) {
		int prefixNumber = 1;
		while (prefixes.contains("ba" + prefixNumber))
			prefixNumber++;
		return "ba" + prefixNumber;
	}
}
