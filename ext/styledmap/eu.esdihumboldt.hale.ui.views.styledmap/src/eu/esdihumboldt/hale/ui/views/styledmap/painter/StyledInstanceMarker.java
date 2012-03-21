/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.ui.views.styledmap.painter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import org.eclipse.ui.PlatformUI;
import org.geotools.styling.Fill;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.SLD;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;

import de.fhg.igd.mapviewer.marker.BoundingBoxMarker;
import de.fhg.igd.mapviewer.waypoints.SelectableWaypoint;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;
import eu.esdihumboldt.hale.ui.style.StyleHelper;
import eu.esdihumboldt.hale.ui.style.service.StyleService;
import eu.esdihumboldt.hale.ui.style.service.internal.StylePreferences;

/**
 * Instance marker support styles provided through the {@link StyleService}.
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
public class StyledInstanceMarker extends InstanceMarker {
	
	private boolean styleInitialized = false;
	private volatile Color styleFillColor;
	private volatile Color styleStrokeColor;
	private volatile java.awt.Stroke styleStroke;
	private boolean hasFill = true;
	
	/**
	 * Initialize the style information.
	 * @param context the context
	 */
	private synchronized void initStyle(InstanceWaypoint context) {
		if (styleInitialized) {
			return;
		}
		else {
			styleInitialized = true;
		}
		
		//check if there is a Rule from the Rulestyle-Page and apply to the instancemarker on the map
		Rule honoredRule = honorRules(context);
		fillStyle(honoredRule, context);
		strokeStyle(honoredRule, context);	

	}
	
	/**
	 * Checks if there is a rule for the certain Instance
	 * @param context the context
	 * @return a certain style rule for the intance, else-rule if nothing found or null
	 * 	       if there is no else-rule
	 */
	private Rule honorRules(InstanceWaypoint context){
		
		Style style = getStyle(context);
		Rule[] rules = SLD.rules(style);
		
		//do rules exist?
		
		if(rules == null || rules.length == 0){
			return null;
		}
		
		
		//sort the elserules at the end
		if(rules.length > 1){
			rules = sortRules(rules);
		}
		
		//if rule exists
		InstanceReference ir = context.getValue();
		InstanceService is = (InstanceService) PlatformUI.getWorkbench().getService(InstanceService.class);
		Instance inst = is.getInstance(ir);
			
		for (int i = 0; i < rules.length; i++){
			
			if(rules[i].getFilter() != null){						
				
				if(rules[i].getFilter().evaluate(inst)){
					return rules[i];
				}
			}
			
			//if a rule exist without a filter and without being an else-filter,
			//the found rule applies to all types
			else{
				if(!rules[i].isElseFilter()){
					return rules[i];
				}
			}
		}
		
		//if there is no appropriate rule, check if there is an else-rule
		for (int i = 0; i < rules.length; i++){
			if(rules[i].isElseFilter()){
				return rules[i];
			}
		}
		
	    
		//return null if no rule was found
		return null;
	
	}
	
	
	/**
	 * Sorts an array of rules, so the else-filter-rules are at the end
	 * @param rules an array of Rules
	 * @return a new array of Rules with sorted elements
	 */
	private Rule[] sortRules(Rule[] rules){
		
		ArrayList<Rule> temp = new ArrayList<Rule>();
		
		for(int i = 0; i < rules.length; i++){
			
			if(!rules[i].isElseFilter()){
				temp.add(rules[i]);
			}
		}
		
		for(int i = 0; i < rules.length; i++){
			
			if(rules[i].isElseFilter()){
				temp.add(rules[i]);
			}
			
		}
		
		Rule[] newRules = new Rule[temp.size()];		
		return temp.toArray(newRules);
	}
	
	/**
	 * retrieves the fill for the map marker
	 * @param rule a certain rule to apply, maybe null
	 * @param context the InstanceWayPoint, wich gets marked
	 */
	private synchronized void fillStyle(Rule rule, InstanceWaypoint context){
				
		// retrieve fill
		Fill fill = null;
		
		//try the Symbolizers from the Rule
		int i = -1;
		while(rule != null && fill == null && i < rule.getSymbolizers().length){
			i++;
			if (rule.getSymbolizers()[i] instanceof PolygonSymbolizer){
				fill = SLD.fill((PolygonSymbolizer)rule.getSymbolizers()[i]);				
				}
			else if (rule.getSymbolizers()[i] instanceof PointSymbolizer){
				fill = SLD.fill((PointSymbolizer)rule.getSymbolizers()[i]);				
				}
			}
	
		
		//if we have a fill now
		if (fill != null) {
			Color sldColor = SLD.color(fill);
			double opacity = SLD.opacity(fill);
			if (sldColor != null) {
				styleFillColor = new Color(sldColor.getRed(), sldColor.getGreen(), 
						sldColor.getBlue(), (int) (opacity * 255));
			}
			else {
				styleFillColor = super.getPaintColor(context);
			}
			hasFill = true;
		}
		//if we still don't have a fill
		else {
			styleFillColor = null;
			hasFill = false;
		}
		
		
	}		
		
	/**
	 * retrieves the stroke for the map marker
	 * @param rule a certain rule to apply, maybe null
	 * @param context the InstanceWayPoint, wich gets marked
	 */
	private synchronized void strokeStyle(Rule rule, InstanceWaypoint context){	
		
		// retrieve stroke
		Stroke stroke = null;
		
		//try the Symbolizers from the Rule
		int i = -1;
		while(rule != null && stroke == null && i < rule.getSymbolizers().length){
			i++;
			if (rule.getSymbolizers()[i] instanceof LineSymbolizer){
				stroke = SLD.stroke((LineSymbolizer)rule.getSymbolizers()[i]);				
				}
			else if (rule.getSymbolizers()[i] instanceof PolygonSymbolizer){
				stroke = SLD.stroke((PolygonSymbolizer)rule.getSymbolizers()[i]);				
				}
			else if (rule.getSymbolizers()[i] instanceof PointSymbolizer){
				stroke = SLD.stroke((PointSymbolizer)rule.getSymbolizers()[i]);				
				}
			}
		
	
		//if we have a stroke now
		if (stroke != null) {
			//XXX is there any Geotools stroke to AWT stroke lib/code somewhere?!
			//XXX have a look at the renderer code (StreamingRenderer)
			
			// stroke color
			Color sldColor = SLD.color(stroke);
			double opacity = SLD.opacity(stroke);
			if (Double.isNaN(opacity)) {
				// fall back to default opacity
				opacity = StyleHelper.DEFAULT_FILL_OPACITY;
			}
			if (sldColor != null) {
				styleStrokeColor = new Color(sldColor.getRed(), sldColor.getGreen(), 
						sldColor.getBlue(), (int) (opacity * 255));
			}
			else {
				styleStrokeColor = super.getBorderColor(context);
			}
			
			// stroke width
			int strokeWidth = SLD.width(stroke);
			if (strokeWidth == SLD.NOTFOUND) {
				// fall back to default width
				strokeWidth = StylePreferences.getDefaultWidth(); 
			}
			styleStroke = new BasicStroke(strokeWidth);
		}
		else {
			styleStroke = null;
			styleStrokeColor = null;
		}
	}
	

	/**
	 * Reset the marker style
	 */
	public synchronized void resetStyle() {
		styleInitialized = false;
	}

	/**
	 * @see InstanceMarker#getPaintColor(InstanceWaypoint)
	 */
	@Override
	protected Color getPaintColor(InstanceWaypoint context) {
		initStyle(context);
		
		if (styleFillColor == null || context.isSelected()) {
			// for selection don't use style
			return super.getPaintColor(context);
		}
		
		return styleFillColor;
	}

	/**
	 * @see InstanceMarker#getBorderColor(InstanceWaypoint)
	 */
	@Override
	protected Color getBorderColor(InstanceWaypoint context) {
		initStyle(context);
		
		if (styleStrokeColor == null || context.isSelected()) {
			return super.getBorderColor(context);
		}
		
		return styleStrokeColor;
	}
	
	/**
	 * Get the stroke for drawing lines.
	 * @param context the context
	 * @return the stroke
	 */
	@Override
	protected java.awt.Stroke getLineStroke(InstanceWaypoint context) {
		initStyle(context);
		
		if (styleStroke != null && !context.isSelected()) {
			return styleStroke;
		}
		else {
			return super.getLineStroke(context);
		}
	}

	/**
	 * @see BoundingBoxMarker#applyFill(Graphics2D, SelectableWaypoint)
	 */
	@Override
	protected boolean applyFill(Graphics2D g, InstanceWaypoint context) {
		initStyle(context);
		
		if (hasFill) {
			g.setPaint(getPaintColor(context));
			return true;
		}
		
		return false;
	}

	/**
	 * @see BoundingBoxMarker#applyStroke(Graphics2D, SelectableWaypoint)
	 */
	@Override
	protected boolean applyStroke(Graphics2D g, InstanceWaypoint context) {
		initStyle(context);
		
		return super.applyStroke(g, context);
	}
	
	/**
	 * Get the style for a given way-point.
	 * @param context the way-point
	 * @return the style
	 */
	private Style getStyle(InstanceWaypoint context) {
		StyleService ss = (StyleService) PlatformUI.getWorkbench().getService(StyleService.class);
		InstanceService is = (InstanceService) PlatformUI.getWorkbench().getService(InstanceService.class);
		
		InstanceReference ref = context.getValue();
		Instance instance = is.getInstance(ref);
		
		return ss.getStyle(instance.getDefinition(), ref.getDataSet());
	}

}
