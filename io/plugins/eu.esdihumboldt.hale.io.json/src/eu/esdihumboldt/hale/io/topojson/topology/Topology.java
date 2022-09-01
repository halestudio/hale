package json.topojson.topology;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map.Entry;

import json.algorithm.DouglasPeucker;
import json.geojson.objects.Bounding;
import json.geojson.objects.Point;
import json.graphic.Colorifier;
import json.graphic.Display;
import json.topojson.algorithm.ArcMap;
import json.topojson.geom.Object;

public class Topology {

	String type;
	Transform transform;
	public HashMap<String,Object> objects;
	public java.lang.Object[][][] arcs;
	transient boolean _quantized;
	
	transient boolean _notsaved;
	transient java.lang.Object[][][] _saved_arcs;
	transient public HashMap<String, java.lang.Object> _meta_properties;
	
	public transient Bounding _bnd;
	
	public Topology(){
		type = "Topology";
		transform = null;
		arcs = null;
		_quantized = false;
		_notsaved = true;
	}
	
	public void addObject(String iName, Object iObject){
		if (objects==null) objects = new HashMap<String,Object>();
		objects.put(iName, iObject);
	}
	
	public void setBound(Bounding iBound){
		_bnd = iBound;
	}
	
	public void draw(Display iDisplay){
		draw(0,0, iDisplay);
	}
	
	public void fill(Display iDisplay, Colorifier iColorifier){
		fill(0,0, iDisplay, iColorifier);
	}
	
	public void draw(double tx, double ty, Display iDisplay){
		
		// Here we have to draw all arcs
		for (java.lang.Object[][] arc:arcs) {
			for (int i=0; i<arc.length-1; i++) {
				if (((Double) arc[i][0] != (Double)arc[i+1][0]) ||
				     ((Double) arc[i][1] != (Double)arc[i+1][1])) {
					iDisplay.drawLine(tx+(Double) arc[i][0], ty+(Double)arc[i][1], tx+(Double)arc[i+1][0], ty+(Double)arc[i+1][1], Color.WHITE);
				}
			}
		}
		
	}
	
	public int getLength(){
		int len = 0;
		for (java.lang.Object[][] arc:arcs) {
			len+=arc.length;
		}
		return len;
	}
	
	public void fill(double tx, double ty, Display iDisplay, Colorifier iColorifier){
		
		 for (Object aObj:objects.values()){
			 aObj.fill(this, iDisplay, iColorifier);
		 }
		
	}
	
	
	public void setArcs(ArcMap iArcMap){
		
		int max = -1;
		if (objects!=null) {
			
			for (Object aObject:objects.values()){
				int index = aObject.findMaxArcIndex();
				if (index>max) max=index;
			}
			max++;
		} else {
			max = iArcMap._arcs.size();
		}
		
		arcs = new java.lang.Object[max][][];
		
		int na = 0;
		for (Arc aArc : iArcMap._arcs) {
			
			arcs[na] = new java.lang.Object[aArc._points.length][];
					
			for (int i=0; i<aArc._points.length; i++){
				arcs[na][i] = new java.lang.Object[2];
				arcs[na][i][0] = new Double( aArc._points[i]._x );
				arcs[na][i][1] = new Double( aArc._points[i]._y );
			}
			
			na++;
			if (na>=max) break;
			
		}
		
	}
	
	public void simplify(int iFact){
		
		if (_notsaved) {
			_saved_arcs = arcs;
			_notsaved = false;
		} else {
			arcs = _saved_arcs;
		}
		
		java.lang.Object[][][] tmp_arcs = new java.lang.Object[arcs.length][][];
		
		for (int i=0; i<arcs.length; i++){
			
			Point[] aPs = new Point[arcs[i].length];
			
			int n=0;
			for (java.lang.Object[] position:arcs[i]){
				aPs[n] = new Point((Double) position[0],(Double) position[1]); 
				n++;
			}
			
			aPs = DouglasPeucker.GDouglasPeucker(aPs, iFact);
			
			n=0;
			tmp_arcs[i] = new java.lang.Object[aPs.length][];
			for (int j=0; j<aPs.length; j++){
				
				tmp_arcs[i][j] = new java.lang.Object[2];
				tmp_arcs[i][j][0] = (Double) aPs[j].x;
				tmp_arcs[i][j][1] = (Double) aPs[j].y; 
				
			}
			
		}
		
		arcs = tmp_arcs;

	}
	
	public void quantize(double iPowTen){
		
		if (!_quantized) {
			
			double aX = 0;
			double aY = 0;
			int n=0;
			
			for (java.lang.Object[][] arc:arcs){
				for (java.lang.Object[] position:arc){
					aX += (Double) position[0]; 
					aY += (Double) position[1];
					n++;
				}
			}
			
			if (n>0) {
				aX = aX/n;
				aY = aY/n;
			}
			
			// Quantize
			double scale = Math.pow(10, iPowTen);
			for (java.lang.Object[][] arc:arcs){
				for (java.lang.Object[] position:arc){
					position[0] = new Integer((int) (((Double) position[0] - aX)*scale)); 
					position[1] = new Integer((int) (((Double) position[1] - aY)*scale));
					n++;
				}
			}
			
			// Delta compute
			for (java.lang.Object[][] arc:arcs){
				
				for (int i=arc.length-1; i>0; i--){
					
					arc[i][0] = (Integer) arc[i][0]-(Integer) arc[i-1][0]; 
					arc[i][1] = (Integer) arc[i][1]-(Integer) arc[i-1][1];
					
				}
			}
			
			double[] scales = new double[] { 1/scale, 1/scale };
			double[] translates = new double[] { aX, aY };
			
			transform = new Transform(scales, translates);
			
		}
		
	}

}
