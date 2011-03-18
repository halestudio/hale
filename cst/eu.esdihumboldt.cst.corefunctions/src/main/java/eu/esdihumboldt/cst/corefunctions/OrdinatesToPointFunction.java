/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.cst.corefunctions;

import java.util.ArrayList;
import java.util.List;

import org.opengis.feature.Feature;

import com.iabcinc.jmep.Environment;
import com.iabcinc.jmep.Expression;
import com.iabcinc.jmep.XExpression;
import com.iabcinc.jmep.hooks.Constant;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import eu.esdihumboldt.cst.CstFunction;
import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.ext.IParameter;
import eu.esdihumboldt.goml.align.Cell;
import eu.esdihumboldt.goml.oml.ext.Parameter;
import eu.esdihumboldt.goml.oml.ext.Transformation;
import eu.esdihumboldt.goml.omwg.ComposedProperty;
import eu.esdihumboldt.goml.omwg.Property;
import eu.esdihumboldt.goml.rdf.About;

/**
 * @author Thorsten Reitz, Stefan Gessner
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class OrdinatesToPointFunction 
	implements CstFunction {

	/**
	 * The name of the parameter for the x ordinate (latitude) expression.
	 */
	public static final String X_EXPRESSION_PARAMETER = "xExpression"; //$NON-NLS-1$
	
	/**
	 * The name of the parameter for the y ordinate (longitude) expression.
	 */
	public static final String Y_EXPRESSION_PARAMETER = "yExpression"; //$NON-NLS-1$
	
	/**
	 * 
	 */
	private String xOrdinateName;
	
	/**
	 * 
	 */
	private String yOrdinateName;
	
	/**
	 * 
	 */
	private String xExpression;
	/**
	 * 
	 */
	private String yExpression;
	
	/**
	 * 
	 */
	private String targetPropertyname;
	
	/**
	 * @param cell
	 * @return true
	 * @see eu.esdihumboldt.cst.CstFunction#configure(eu.esdihumboldt.cst.align.ICell)
	 */
	@Override
	public boolean configure(ICell cell) {
		List<Property> properties = ((ComposedProperty)cell.getEntity1()).getCollection();
		this.xOrdinateName = properties.get(0).getLocalname();
		this.yOrdinateName = properties.get(1).getLocalname();
		
		Transformation t = (Transformation) cell.getEntity1().getTransformation();
		this.xExpression = t.getParameterMap().get(X_EXPRESSION_PARAMETER).getValue();
		this.yExpression = t.getParameterMap().get(Y_EXPRESSION_PARAMETER).getValue();
		
		this.targetPropertyname = ((Property)cell.getEntity2()).getLocalname();
		return true;
	}
	
	/**
	 * @return parameterCell
	 * @see eu.esdihumboldt.cst.CstFunction#getParameters()
	 */
	@Override
	public ICell getParameters() {

		Cell parameterCell = new Cell();
		ComposedProperty composedEntity1 = new ComposedProperty(new About("")); //$NON-NLS-1$
		Property entity1 = new Property(new About("")); //$NON-NLS-1$
		
		// Setting of type condition for entity1
		List <String> entityTypes = new ArrayList <String>();
		entityTypes.add(Number.class.getName());
		entityTypes.add(String.class.getName());
		entity1.setTypeCondition(entityTypes);
		
		Property entity2 = new Property(new About("")); //$NON-NLS-1$
		 
		// Setting of type condition for entity2
		entityTypes = new ArrayList <String>();
		entityTypes.add(com.vividsolutions.jts.geom.Point.class.getName());
		entityTypes.add(com.vividsolutions.jts.geom.Geometry.class.getName());
		entity2.setTypeCondition(entityTypes);
		
		List<IParameter> params = new ArrayList<IParameter>();
		Transformation t = new Transformation();
		IParameter xExpression = new Parameter(OrdinatesToPointFunction.X_EXPRESSION_PARAMETER , ""); //$NON-NLS-1$
		IParameter yExpression = new Parameter(OrdinatesToPointFunction.Y_EXPRESSION_PARAMETER , ""); //$NON-NLS-1$
		params.add(xExpression);
		params.add(yExpression);
		t.setParameters(params);
		composedEntity1.setTransformation(t);		
		
		composedEntity1.getCollection().add(entity1);
		composedEntity1.getCollection().add(entity2);
		parameterCell.setEntity1(composedEntity1);	
		parameterCell.setEntity2(entity2);
		
		return parameterCell;
	}	
	
	/**
	 * @param source
	 * @param target
	 * @return target
	 * @see eu.esdihumboldt.cst.CstFunction#transform(org.opengis.feature.Feature, org.opengis.feature.Feature)
	 */
	@Override
	public Feature transform(Feature source, Feature target) {
		
		// set up environment for calculation of new coordinate
		Environment env = new Environment();

		double x_value = Double.parseDouble(source.getProperty(this.xOrdinateName).getValue().toString());
		env.addVariable(this.xOrdinateName,	new Constant(x_value));
		double y_value = Double.parseDouble(source.getProperty(this.yOrdinateName).getValue().toString());
		env.addVariable(this.yOrdinateName,	new Constant(y_value));	

		try {
			Expression xExpr = new Expression(this.xExpression, env);
			double xResult = Double.parseDouble(xExpr.evaluate().toString());
			Expression yExpr = new Expression(this.yExpression, env);
			double yResult = Double.parseDouble(yExpr.evaluate().toString());
			
			GeometryFactory geomFactory = new GeometryFactory();
			Geometry new_geometry = geomFactory.createPoint(new Coordinate(xResult, yResult));

			target.getProperty(this.targetPropertyname).setValue(new_geometry);
		} catch (XExpression e) {
			throw new RuntimeException(Messages.getString("OrdinatesToPointFunction.7"), e); //$NON-NLS-1$
		}

		return target;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

}
