/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
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
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.exolab.castor.xml.XMLContext;
import org.xml.sax.InputSource;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.BaseAlignmentCell;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.MutableAlignment;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultAlignment;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * Alignment bean serving as model for alignment I/O
 * 
 * @author Simon Templer
 */
public class AlignmentBean {

	private Map<String, URI> base = new HashMap<String, URI>();
	private int nextCellId;
	private Collection<CellBean> cells = new LinkedHashSet<CellBean>();
	private Collection<ModifierBean> modifiers = new ArrayList<ModifierBean>();

	/**
	 * Default constructor
	 */
	public AlignmentBean() {
		super();
	}

	/**
	 * Create a bean for the given alignment
	 * 
	 * @param alignment the alignment
	 */
	public AlignmentBean(Alignment alignment) {
		super();

		// populate bean from alignment
		for (Cell cell : alignment.getCells()) {
			generateModifier(cell);
			if (cell instanceof BaseAlignmentCell)
				continue;
			CellBean cellBean = new CellBean(cell);
			cells.add(cellBean);
		}
	}

	/**
	 * Load an AlignmentBean from an input stream. The stream is closed at the
	 * end.
	 * 
	 * @param in the input stream
	 * @param reporter the I/O reporter to report any errors to, may be
	 *            <code>null</code>
	 * @return the AlignmentBean
	 * 
	 * @throws MappingException if the mapping could not be loaded
	 * @throws MarshalException if the alignment could not be read
	 * @throws ValidationException if the input stream did not provide valid XML
	 */
	public static AlignmentBean load(InputStream in, IOReporter reporter) throws MappingException,
			MarshalException, ValidationException {
		Mapping mapping = new Mapping(AlignmentBean.class.getClassLoader());
		mapping.loadMapping(new InputSource(AlignmentBean.class
				.getResourceAsStream("AlignmentBean.xml")));

		XMLContext context = new XMLContext();
		context.addMapping(mapping);

		Unmarshaller unmarshaller = context.createUnmarshaller();
		try {
			return (AlignmentBean) unmarshaller.unmarshal(new InputSource(in));
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}

	/**
	 * Generates and adds a modifier for the given cell if necessary.
	 * 
	 * @param cell the cell to generate a modifier for
	 */
	private void generateModifier(Cell cell) {
		Collection<Cell> disabledFor;
		if (cell instanceof BaseAlignmentCell)
			disabledFor = ((BaseAlignmentCell) cell).getAdditionalDisabledFor();
		else
			disabledFor = cell.getDisabledFor();
		if (!disabledFor.isEmpty()) {
			ModifierBean modifier = new ModifierBean(cell.getId());
			ArrayList<String> disabledForIds = new ArrayList<String>(disabledFor.size());
			for (Cell other : disabledFor)
				disabledForIds.add(other.getId());
			modifier.setDisableForRelation(disabledForIds);
			modifiers.add(modifier);
		}
	}

	/**
	 * Create an alignment from the information in the bean.
	 * 
	 * @param reporter the I/O reporter to report any errors to, may be
	 *            <code>null</code>
	 * @param sourceTypes the source types to use for resolving definition
	 *            references
	 * @param targetTypes the target types to use for resolving definition
	 *            references
	 * @return the alignment
	 * @throws MappingException if the mapping could not be loaded
	 * @throws MarshalException if the alignment could not be read
	 * @throws ValidationException if the input stream did not provide valid XML
	 */
	public MutableAlignment createAlignment(IOReporter reporter, TypeIndex sourceTypes,
			TypeIndex targetTypes) throws MappingException, MarshalException, ValidationException {
		DefaultAlignment alignment = new DefaultAlignment();

		// Map that maps local prefixes to the global prefix
		Map<AlignmentBean, Map<String, String>> prefixMapping = new HashMap<AlignmentBean, Map<String, String>>();
		// also a mapping for this alignment itself in case the same URI is
		// defined for two prefixes
		prefixMapping.put(this, new HashMap<String, String>());

		// set of already seen URIs
		Set<URI> knownURIs = new HashSet<URI>();

		// reverse map of base
		Map<URI, String> uriToPrefix = new HashMap<URI, String>();

		// check base for doubles, and invert it for later
		for (Entry<String, URI> baseEntry : base.entrySet()) {
			if (knownURIs.contains(baseEntry.getValue())) {
				reporter.warn(new IOMessageImpl("The same base alignment was included twice.", null));
				prefixMapping.get(this).put(baseEntry.getKey(),
						uriToPrefix.get(baseEntry.getValue()));
			}
			else {
				knownURIs.add(baseEntry.getValue());
				prefixMapping.get(this).put(baseEntry.getKey(), baseEntry.getKey());
				uriToPrefix.put(baseEntry.getValue(), baseEntry.getKey());
			}
		}

		// find all alignments to load (also missing ones) and load the beans
		LinkedList<URI> queue = new LinkedList<URI>(knownURIs);
		Map<AlignmentBean, URI> beanToUri = new HashMap<AlignmentBean, URI>();
		while (!queue.isEmpty()) {
			URI baseURI = queue.pollFirst();
			AlignmentBean baseBean;
			try {
				// XXX update path according to path change of main alignment?
				baseBean = load(new DefaultInputSupplier(baseURI).getInput(), reporter);
			} catch (IOException e) {
				reporter.warn(new IOMessageImpl("Couldn't load a base alignment.", e));
				continue;
			}
			beanToUri.put(baseBean, baseURI);
			// load "missing" base alignments, too, add prefix mapping
			for (Entry<String, URI> baseEntry : baseBean.getBase().entrySet()) {
				URI uri = baseEntry.getValue();
				// check whether this base alignment is missing
				if (!knownURIs.contains(uri)) {
					reporter.info(new IOMessageImpl(
							"A base alignment referenced another base alignment that was not yet known. It is now included, too.",
							null));
					queue.add(uri);
					knownURIs.add(uri);
					String prefix = generatePrefix(base.keySet());
					base.put(prefix, uri);
					uriToPrefix.put(uri, prefix);
					prefixMapping.get(this).put(prefix, prefix);
				}
				// add prefix mapping
				if (!prefixMapping.containsKey(baseBean))
					prefixMapping.put(baseBean, new HashMap<String, String>());
				prefixMapping.get(baseBean).put(baseEntry.getKey(), uriToPrefix.get(uri));
			}
		}

		// add cells from the base alignments one by one
		for (Entry<AlignmentBean, URI> entry : beanToUri.entrySet()) {
			AlignmentBean baseBean = entry.getKey();
			URI baseURI = entry.getValue();
			Collection<BaseAlignmentCell> baseCells = new ArrayList<BaseAlignmentCell>(
					baseBean.cells.size());
			for (CellBean cellBean : baseBean.cells) {
				MutableCell cell = cellBean.createCell(reporter, sourceTypes, targetTypes);
				if (cell != null)
					baseCells.add(new BaseAlignmentCell(cell, baseURI, uriToPrefix.get(baseURI)));
			}
			alignment.addBaseAlignment(uriToPrefix.get(baseURI), baseURI, baseCells);
		}

		// add cells from the main alignment
		for (CellBean cellBean : cells) {
			MutableCell cell = cellBean.createCell(reporter, sourceTypes, targetTypes);
			if (cell != null)
				alignment.addCell(cell);
		}

		// work through modifiers of base alignments
		for (Entry<AlignmentBean, URI> entry : beanToUri.entrySet())
			applyModifiers(alignment, entry.getKey().modifiers, prefixMapping.get(entry.getKey()),
					uriToPrefix.get(entry.getValue()), reporter);

		// work on modifiers of main alignment
		applyModifiers(alignment, modifiers, prefixMapping.get(this), null, reporter);

		alignment.setNextCellId(nextCellId);

		return alignment;
	}

	/**
	 * Generates a new prefix.
	 * 
	 * @param prefixes the existing prefixes
	 * @return a new prefix
	 */
	private String generatePrefix(Set<String> prefixes) {
		// XXX a more beautiful way to generate simple prefixes?
		// first assign a to z
		for (char c = 'a'; c <= 'z'; c++)
			if (!prefixes.contains(String.valueOf(c)))
				return String.valueOf(c);

		// then search for 2-letter combinations
		// more than 26 base alignments shouldn't happen too often
		for (char c = 'a'; c <= 'z'; c++)
			for (char c2 = 'a'; c <= 'z'; c++)
				if (!prefixes.contains(c + "" + c2))
					return c + "" + c2;

		// more than 26^2? Way too many.
		throw new IllegalStateException("Having more than 26*26 base alignments is not supported.");
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
	 * @param reporter the I/O reporter to report any errors to, may be
	 *            <code>null</code>
	 */
	private void applyModifiers(Alignment alignment, Collection<ModifierBean> modifiers,
			Map<String, String> prefixMapping, String defaultPrefix, IOReporter reporter) {
		for (ModifierBean modifier : modifiers) {
			MutableCell cell = getCell(alignment, modifier.getCell(), defaultPrefix, prefixMapping,
					reporter);
			if (cell == null)
				continue;

			for (String disabledForId : modifier.getDisableForRelation()) {
				Cell other = getCell(alignment, disabledForId, defaultPrefix, prefixMapping,
						reporter);
				if (other == null)
					continue;
				else if (!AlignmentUtil.isTypeCell(other)) {
					reporter.warn(new IOMessageImpl(
							"The cell referenced in disable-for is not a type cell.", null));
					continue;
				}
				else if (!alignment.getPropertyCells(other).contains(cell)) {
					reporter.warn(new IOMessageImpl(
							"The cell referenced in disable-for does not contain the cell that gets modified.",
							null));
					continue;
				}

				cell.setDisabledFor(other, true);
			}
			// handle additional properties
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
	private MutableCell getCell(Alignment alignment, String cellId, String defaultPrefix,
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
		MutableCell cell = (MutableCell) alignment.getCell(cellId);
		if (cell == null)
			reporter.warn(new IOMessageImpl("A cell referenced by a modifier could not be found",
					null));

		return cell;
	}

	/**
	 * Get the defined cells
	 * 
	 * @return the cells
	 */
	public Collection<CellBean> getCells() {
		return cells;
	}

	/**
	 * Set the defined cells
	 * 
	 * @param cells the cells to set
	 */
	public void setCells(Collection<CellBean> cells) {
		this.cells = cells;
	}

	/**
	 * Get the alignment modifiers
	 * 
	 * @return the modifiers
	 */
	public Collection<ModifierBean> getModifiers() {
		return modifiers;
	}

	/**
	 * Set the alignment modifiers
	 * 
	 * @param modifiers the modifiers to set
	 */
	public void setModifiers(Collection<ModifierBean> modifiers) {
		this.modifiers = modifiers;
	}

	/**
	 * @return the nextCellId
	 */
	public int getNextCellId() {
		return nextCellId;
	}

	/**
	 * @param nextCellId the nextCellId to set
	 */
	public void setNextCellId(int nextCellId) {
		this.nextCellId = nextCellId;
	}

	/**
	 * @return the base
	 */
	public Map<String, URI> getBase() {
		return base;
	}

	/**
	 * @param base the base to set
	 */
	public void setBase(Map<String, URI> base) {
		this.base = base;
	}

}
