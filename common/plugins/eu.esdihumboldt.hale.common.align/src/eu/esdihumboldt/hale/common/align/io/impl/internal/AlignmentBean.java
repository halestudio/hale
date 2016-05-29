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
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.exolab.castor.xml.XMLContext;
import org.xml.sax.InputSource;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

import eu.esdihumboldt.hale.common.align.extension.function.custom.CustomPropertyFunction;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.BaseAlignmentCell;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.MutableAlignment;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.TransformationMode;
import eu.esdihumboldt.hale.common.core.io.PathUpdate;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * Alignment bean serving as model for alignment I/O
 * 
 * @author Simon Templer
 */
public class AlignmentBean extends
		AbstractBaseAlignmentLoader<AlignmentBean, CellBean, ModifierBean> {

	private Map<String, URI> base = new HashMap<String, URI>();
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
	 * @param pathUpdate to update relative paths in case of a path change
	 */
	public AlignmentBean(Alignment alignment, final PathUpdate pathUpdate) {
		super();

		base = new HashMap<String, URI>(Maps.transformValues(alignment.getBaseAlignments(),
				new Function<URI, URI>() {

					@Override
					public URI apply(URI input) {
						return pathUpdate.findLocation(input, true, false, true);
					}
				}));

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
	public static void addBaseAlignment(MutableAlignment alignment, URI newBase,
			URI projectLocation, TypeIndex sourceTypes, TypeIndex targetTypes, IOReporter reporter)
			throws IOException {
		new AlignmentBean().internalAddBaseAlignment(alignment, newBase, projectLocation,
				sourceTypes, targetTypes, reporter);
	}

	/**
	 * Generates and adds a modifier for the given cell if necessary.
	 * 
	 * @param cell the cell to generate a modifier for
	 */
	private void generateModifier(Cell cell) {
		Collection<String> disabledFor;
		if (cell instanceof BaseAlignmentCell)
			disabledFor = ((BaseAlignmentCell) cell).getAdditionalDisabledFor();
		else
			disabledFor = cell.getDisabledFor();
		if (!disabledFor.isEmpty()) {
			ModifierBean modifier = new ModifierBean(cell.getId());
			modifier.setDisableForRelation(disabledFor);
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
	 * @param updater the path updater to use for base alignments
	 * @return the alignment
	 * @throws IOException if creating the alignment fails
	 */
	public MutableAlignment createAlignment(IOReporter reporter, TypeIndex sourceTypes,
			TypeIndex targetTypes, PathUpdate updater) throws IOException {
		return super.createAlignment(this, sourceTypes, targetTypes, updater, reporter);
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

	/**
	 * @see eu.esdihumboldt.hale.common.align.io.impl.internal.AbstractBaseAlignmentLoader#loadAlignment(java.io.InputStream,
	 *      eu.esdihumboldt.hale.common.core.io.report.IOReporter)
	 */
	@Override
	protected AlignmentBean loadAlignment(InputStream in, IOReporter reporter) throws IOException {
		try {
			return load(in, reporter);
		} catch (MarshalException e) {
			throw new IOException(e);
		} catch (ValidationException e) {
			throw new IOException(e);
		} catch (MappingException e) {
			throw new IOException(e);
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.io.impl.internal.AbstractBaseAlignmentLoader#getBases(java.lang.Object)
	 */
	@Override
	protected Map<String, URI> getBases(AlignmentBean alignment) {
		return alignment.base;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.io.impl.internal.AbstractBaseAlignmentLoader#getCells(java.lang.Object)
	 */
	@Override
	protected Collection<CellBean> getCells(AlignmentBean alignment) {
		return alignment.cells;
	}

	@Override
	protected Collection<CustomPropertyFunction> getPropertyFunctions(AlignmentBean alignment,
			TypeIndex sourceTypes, TypeIndex targetTypes) {
		// not supported
		return Collections.emptyList();
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.io.impl.internal.AbstractBaseAlignmentLoader#createCell(java.lang.Object,
	 *      eu.esdihumboldt.hale.common.schema.model.TypeIndex,
	 *      eu.esdihumboldt.hale.common.schema.model.TypeIndex,
	 *      eu.esdihumboldt.hale.common.core.io.report.IOReporter)
	 */
	@Override
	protected MutableCell createCell(CellBean cell, TypeIndex sourceTypes, TypeIndex targetTypes,
			IOReporter reporter) {
		return cell.createCell(reporter, sourceTypes, targetTypes);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.io.impl.internal.AbstractBaseAlignmentLoader#getModifiers(java.lang.Object)
	 */
	@Override
	protected Collection<ModifierBean> getModifiers(AlignmentBean alignment) {
		return alignment.modifiers;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.io.impl.internal.AbstractBaseAlignmentLoader#getModifiedCell(java.lang.Object)
	 */
	@Override
	protected String getModifiedCell(ModifierBean modifier) {
		return modifier.getCell();
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.io.impl.internal.AbstractBaseAlignmentLoader#getDisabledForList(java.lang.Object)
	 */
	@Override
	protected Collection<String> getDisabledForList(ModifierBean modifier) {
		return modifier.getDisableForRelation();
	}

	@Override
	protected TransformationMode getTransformationMode(ModifierBean modifier) {
		// not supported
		return null;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.io.impl.internal.AbstractBaseAlignmentLoader#getCellId(java.lang.Object)
	 */
	@Override
	protected String getCellId(CellBean cell) {
		return cell.getId();
	}

}
