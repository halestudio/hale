/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.cst.transformer.service.impl;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.cst.NameHelper;
import eu.esdihumboldt.cst.align.IAlignment;
import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.ICell.RelationType;
import eu.esdihumboldt.cst.transformer.service.rename.RenameFeatureFunction;
import eu.esdihumboldt.goml.align.Alignment;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.omwg.FeatureClass;
import eu.esdihumboldt.goml.rdf.About;
import eu.esdihumboldt.goml.rdf.Resource;

/**
 * FIXME Add Type description.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class AlignmentIndexTest {
	
	/**
	 * Test method for {@link eu.esdihumboldt.cst.transformer.service.impl.AlignmentIndex#getFeatureTypeKey(String key, String namespace)}
	 */
	@Test
	public void testGetFeatureTypeKey() {
		String key1 = "http://xsdi.org/namespace/FeatureTypeName"; //$NON-NLS-1$
		String key2 = "http://xsdi.org/namespace/FeatureTypeName/AttributeName"; //$NON-NLS-1$
		String namespace1 = "http://xsdi.org/namespace"; //$NON-NLS-1$
		String namespace2 = "http://xsdi.org/namespace/"; //$NON-NLS-1$
		
		String[] results = new String[4];
		
		results[0] = AlignmentIndex.getFeatureTypeKey(key1, namespace1);
		results[1] = AlignmentIndex.getFeatureTypeKey(key1, namespace2);
		
		results[2] = AlignmentIndex.getFeatureTypeKey(key2, namespace1);
		results[3] = AlignmentIndex.getFeatureTypeKey(key2, namespace2);
		
		for (String result : results) {
			assertTrue(result.equals(key1));
		}
	}
	
	@Test
	public void testGetCellsPerEntity() {
		// create four FeatureTypes and put into TSP.
		SimpleFeatureType ftA = getFeatureType(NameHelper.sourceNamespace, 
				NameHelper.sourceLocalname + "_A", null); //$NON-NLS-1$
		SimpleFeatureType ftB = getFeatureType(NameHelper.sourceNamespace, 
				NameHelper.sourceLocalname + "_B", ftA); //$NON-NLS-1$
		SimpleFeatureType ftC = getFeatureType(NameHelper.sourceNamespace, 
				NameHelper.sourceLocalname + "_C", ftB); //$NON-NLS-1$
		SimpleFeatureType ftD = getFeatureType(NameHelper.sourceNamespace, 
				NameHelper.sourceLocalname + "_D", ftA); //$NON-NLS-1$
		SimpleFeatureType ftE = getFeatureType(NameHelper.sourceNamespace, 
				NameHelper.sourceLocalname + "_E", null); //$NON-NLS-1$
		Collection<FeatureType> types = new HashSet<FeatureType>();
		types.add(ftA);
		types.add(ftB);
		types.add(ftC);
		types.add(ftD);
		TargetSchemaProvider.getInstance().addTypes(types);
		
		// set up Alignment and put it into AlignmentIndex
		IAlignment al = new Alignment();
		al.getMap().add(buildCell(ftE, ftA));
		al.getMap().add(buildCell(ftE, ftB));
		al.getMap().add(buildCell(ftE, ftC));
		al.getMap().add(buildCell(ftE, ftD));
		
		AlignmentIndex ai = new AlignmentIndex(al);
		
		List<ICell> cells_A = ai.getCellsPerEntity(ftA.getName().getNamespaceURI() + "/" + ftA.getName().getLocalPart()); //$NON-NLS-1$
		List<ICell> cells_B = ai.getCellsPerEntity(ftB.getName().getNamespaceURI() + "/" + ftB.getName().getLocalPart()); //$NON-NLS-1$
		List<ICell> cells_C = ai.getCellsPerEntity(ftC.getName().getNamespaceURI() + "/" + ftC.getName().getLocalPart()); //$NON-NLS-1$
		List<ICell> cells_D = ai.getCellsPerEntity(ftD.getName().getNamespaceURI() + "/" + ftD.getName().getLocalPart()); //$NON-NLS-1$
		
		assertTrue(cells_A.size() == 1);
		assertTrue(cells_B.size() == 2);
		assertTrue(cells_C.size() == 3);
		assertTrue(cells_D.size() == 2);
		
	}
	
	private static Cell buildCell(FeatureType entity1, FeatureType entity2) {
		// add cells
		Cell cell = new Cell();
		cell.setEntity1(
				new FeatureClass(new About(
						entity1.getName().getNamespaceURI(), 
						entity1.getName().getLocalPart())));
		cell.setEntity2(new FeatureClass(new About(
				entity2.getName().getNamespaceURI(), 
				entity2.getName().getLocalPart())));
		cell.setRelation(RelationType.Equivalence);
		
		Transformation t = new Transformation();
		t.setLabel(RenameFeatureFunction.class.getName());
		t.setService(new Resource(RenameFeatureFunction.class.getName()));
		((FeatureClass)cell.getEntity1()).setTransformation(t);
		return cell;
	}
	
	
	
	private static SimpleFeatureType getFeatureType(String featureTypeNamespace, 
			String featureTypeName, SimpleFeatureType superType) {
	
		SimpleFeatureType ft = null;
		try {
			SimpleFeatureTypeBuilder ftbuilder = new SimpleFeatureTypeBuilder();
			ftbuilder.setName(featureTypeName);
			ftbuilder.setNamespaceURI(featureTypeNamespace);
			ftbuilder.setSuperType(superType);
			ft = ftbuilder.buildFeatureType();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return ft;
	}

}
