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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Strings;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.BaseAlignmentCell;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.ModifiableCell;
import eu.esdihumboldt.hale.common.align.model.MutableAlignment;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultAlignment;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;
import eu.esdihumboldt.util.Pair;
import eu.esdihumboldt.util.io.PathUpdate;

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
	 * Returns the cell id of the given cell.
	 * 
	 * @param cell the cell in question
	 * @return the cell id of the given cell
	 */
	protected abstract String getCellId(C cell);

	/**
	 * Create a cell from the given cell representation
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
	 * Adds the given base alignment to the given alignment.
	 * 
	 * @param alignment the alignment to add a base alignment to
	 * @param newBase URI of the new base alignment
	 * @param sourceTypes the source types to use for resolving definition
	 *            references
	 * @param targetTypes the target types to use for resolving definition
	 *            references
	 * @param reporter the I/O reporter to report any errors to, may be
	 *            <code>null</code>
	 * @throws IOException if adding the base alignment fails
	 */
	protected final void internalAddBaseAlignment(MutableAlignment alignment, URI newBase,
			TypeIndex sourceTypes, TypeIndex targetTypes, IOReporter reporter) throws IOException {
		Map<A, Map<String, String>> prefixMapping = new HashMap<A, Map<String, String>>();
		Map<A, Pair<String, URI>> alignmentToInfo = new HashMap<A, Pair<String, URI>>();

		generatePrefixMapping(newBase, alignment.getBaseAlignments(), prefixMapping,
				alignmentToInfo, reporter);
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
			Map<A, Pair<String, URI>> alignmentToInfo, IOReporter reporter) throws IOException {
		for (Entry<A, Pair<String, URI>> base : alignmentToInfo.entrySet()) {
			Collection<C> baseCells = getCells(base.getKey());
			boolean hasIds = true;
			for (C baseCell : baseCells)
				if (Strings.isNullOrEmpty(getCellId(baseCell))) {
					hasIds = false;
					break;
				}
			if (!hasIds) {
				throw new IOException("At least one base alignment (" + base.getValue().getSecond()
						+ ") has no cell ids. Please load and save it to generate them.");
			}
		}

		for (Entry<A, Pair<String, URI>> base : alignmentToInfo.entrySet()) {
			Collection<C> baseCells = getCells(base.getKey());
			Collection<BaseAlignmentCell> createdCells = new ArrayList<BaseAlignmentCell>(
					baseCells.size());
			for (C baseCell : baseCells) {
				// add cells of base alignments
				MutableCell cell = createCell(baseCell, sourceTypes, targetTypes, reporter);
				if (cell != null)
					createdCells.add(new BaseAlignmentCell(cell, base.getValue().getSecond(), base
							.getValue().getFirst()));
			}
			alignment.addBaseAlignment(base.getValue().getFirst(), base.getValue().getSecond(),
					createdCells);
		}

		// add modifiers of base alignments
		for (Entry<A, Pair<String, URI>> base : alignmentToInfo.entrySet())
			applyModifiers(alignment, getModifiers(base.getKey()),
					prefixMapping.get(base.getKey()), base.getValue().getFirst(), true, reporter);
	}

	/**
	 * Function to fill the prefixMapping and alignmentToInfo maps.
	 * 
	 * @param newBase the URI of the new base alignment to add
	 * @param existingBases the map of existing bases
	 * @param prefixMapping gets filled with a mapping from local to global
	 *            prefixes
	 * @param alignmentToInfo gets filled with a mapping from base alignment
	 *            representations to prefixes and URIs
	 * @param reporter the reporter
	 * @return whether newBase actually is a new base and the add process should
	 *         continue
	 */
	private boolean generatePrefixMapping(URI newBase, Map<String, URI> existingBases,
			Map<A, Map<String, String>> prefixMapping, Map<A, Pair<String, URI>> alignmentToInfo,
			IOReporter reporter) {
		// set of already seen URIs
		Set<URI> knownURIs = new HashSet<URI>();

		// reverse map of base
		Map<URI, String> uriToPrefix = new HashMap<URI, String>();

		// create URI to prefix map and known URIs
		// URIs come from alignment, so they are absolute
		for (Entry<String, URI> baseEntry : existingBases.entrySet()) {
			knownURIs.add(baseEntry.getValue());
			uriToPrefix.put(baseEntry.getValue(), baseEntry.getKey());
		}

		if (uriToPrefix.containsKey(newBase)) {
			reporter.info(new IOMessageImpl("The base alignment (" + newBase
					+ ") is already included.", null));
			return false;
		}

		Set<String> existingPrefixes = new HashSet<String>(existingBases.keySet());
		String newPrefix = generatePrefix(existingPrefixes);
		existingPrefixes.add(newPrefix);
		uriToPrefix.put(newBase, newPrefix);

		/*
		 * XXX Adding a base alignment could only use a PathUpdate for the
		 * movement of the base alignment which is not known in the current
		 * project. Maybe could try the one of the current project either way?
		 */

		// find all alignments to load (also missing ones) and load the beans
		LinkedList<URI> queue = new LinkedList<URI>();
		queue.add(newBase);
		while (!queue.isEmpty()) {
			URI baseURI = queue.pollFirst();
			A baseA;
			try {
				baseA = loadAlignment(new DefaultInputSupplier(baseURI).getInput(), reporter);
			} catch (IOException e) {
				reporter.error(new IOMessageImpl("Couldn't load an included base alignment ("
						+ baseURI + ").", e));
				reporter.setSuccess(false);
				return false;
			}

			// add to alignment info map
			alignmentToInfo.put(baseA, new Pair<String, URI>(uriToPrefix.get(baseURI), baseURI));
			prefixMapping.put(baseA, new HashMap<String, String>());

			// load "missing" base alignments, too, add prefix mapping
			for (Entry<String, URI> baseEntry : getBases(baseA).entrySet()) {
				// rawUri may be relative
				URI rawUri = baseEntry.getValue();
				URI uri = newBase.resolve(rawUri);
				// check whether this base alignment is missing
				if (!knownURIs.contains(uri)) {
					reporter.info(new IOMessageImpl(
							"A base alignment referenced another base alignment (" + uri
									+ ") that was not yet known. It is now included, too.", null));
					queue.add(uri);
					knownURIs.add(uri);
					String prefix = generatePrefix(existingPrefixes);
					existingPrefixes.add(prefix);
					uriToPrefix.put(uri, prefix);
				}
				// add prefix mapping
				prefixMapping.get(baseA).put(baseEntry.getKey(), uriToPrefix.get(uri));
			}
		}

		return true;
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
		Map<A, Pair<String, URI>> alignmentToInfo = new HashMap<A, Pair<String, URI>>();

		// fill needed maps
		generatePrefixMapping(start, prefixMapping, alignmentToInfo, updater, reporter);

		// create alignment
		DefaultAlignment alignment = new DefaultAlignment();

		// add cells of base alignments
		processBaseAlignments(alignment, sourceTypes, targetTypes, prefixMapping, alignmentToInfo,
				reporter);

		// add cells of main alignment
		for (C mainCell : getCells(start)) {
			MutableCell cell = createCell(mainCell, sourceTypes, targetTypes, reporter);
			if (cell != null)
				alignment.addCell(cell);
		}

		// add modifiers of main alignment
		applyModifiers(alignment, getModifiers(start), prefixMapping.get(start), null, false,
				reporter);

		return alignment;
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
			Map<A, Pair<String, URI>> alignmentToInfo, PathUpdate updater, IOReporter reporter)
			throws IOException {
		Map<String, URI> base = getBases(start);

		// also a mapping for this alignment itself in case the same URI is
		// defined for two prefixes
		prefixMapping.put(start, new HashMap<String, String>());

		// set of already seen URIs
		Set<URI> knownURIs = new HashSet<URI>();

		// reverse map of base
		Map<URI, String> uriToPrefix = new HashMap<URI, String>();

		// check base for doubles, and invert it for later
		for (Entry<String, URI> baseEntry : base.entrySet()) {
			URI rawBaseURI = baseEntry.getValue();
			// resolve here, to not include the same file through different
			// relative paths...
			URI baseURI = updater.findLocation(rawBaseURI, true, false);
			if (baseURI == null) {
				throw new IOException("Couldn't load an included alignment (" + rawBaseURI
						+ "). File not found.", null);
			}
			if (knownURIs.contains(baseURI)) {
				reporter.warn(new IOMessageImpl("The same base alignment (" + rawBaseURI
						+ ") was included twice.", null));
				prefixMapping.get(start).put(baseEntry.getKey(), uriToPrefix.get(baseURI));
			}
			else {
				knownURIs.add(baseURI);
				prefixMapping.get(start).put(baseEntry.getKey(), baseEntry.getKey());
				uriToPrefix.put(baseURI, baseEntry.getKey());
			}
		}

		// find all alignments to load (also missing ones) and load the beans
		LinkedList<URI> queue = new LinkedList<URI>(knownURIs);
		while (!queue.isEmpty()) {
			URI baseURI = queue.pollFirst();
			A baseA;
			try {
				baseA = loadAlignment(new DefaultInputSupplier(baseURI).getInput(), reporter);
			} catch (IOException e) {
				throw new IOException("Couldn't load an included alignment (" + baseURI + ").", e);
			}

			// add to alignment info map
			alignmentToInfo.put(baseA, new Pair<String, URI>(uriToPrefix.get(baseURI), baseURI));
			prefixMapping.put(baseA, new HashMap<String, String>());

			// load "missing" base alignments, too, add prefix mapping
			for (Entry<String, URI> baseEntry : getBases(baseA).entrySet()) {
				// rawUri may be relative
				URI rawUri = baseEntry.getValue();
				// First resolve relative (if needed) to the current alignment.
				URI uri = baseURI.resolve(rawUri);
				// Then try path update in case several alignments that
				// reference each other were moved in the same way as the
				// project.
				uri = updater.findLocation(uri, true, false);
				if (uri == null)
					throw new IOException("Couldn't load an included alignment (" + rawUri
							+ "). File not found.");
				if (!knownURIs.contains(uri)) {
					reporter.info(new IOMessageImpl(
							"A base alignment referenced another base alignment (" + uri
									+ ") that was not yet known. It is now included, too.", null));
					queue.add(uri);
					knownURIs.add(uri);
					String prefix = generatePrefix(base.keySet());
					base.put(prefix, uri);
					uriToPrefix.put(uri, prefix);
					prefixMapping.get(start).put(prefix, prefix);
				}
				// add prefix mapping
				prefixMapping.get(baseA).put(baseEntry.getKey(), uriToPrefix.get(uri));
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
				else if (!alignment.getPropertyCells(other, true).contains(cell)) {
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
			reporter.warn(new IOMessageImpl("A cell referenced by a modifier could not be found",
					null));

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
