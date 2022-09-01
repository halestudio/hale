package json.topojson.geom;

import java.awt.Color;
import java.util.List;
import java.util.Set;

import json.graphic.Colorifier;
import json.graphic.Display;
import json.topojson.geom.sub.Entity;
import json.topojson.topology.Topology;


public class Polygon extends Object {
	
	int[][] arcs;

	public Polygon(List<Entity> iEntities){
		type = "Polygon" ;
		
		Entity[] aArr = new Entity[iEntities.size()];
		aArr = iEntities.toArray(aArr);
		
		arcs = new int[aArr.length][];
		for (int i=0; i<aArr.length; i++){
			arcs[i] = aArr[i]._indexes;
		}
		
	}

	@Override
	public int findMaxArcIndex() {
		int max = -1;
		for (int[] arc:arcs) {
			
			for (int index:arc) {
				if (Math.abs(index)>max) {
					max = index;
				}
			}
		}
		return max;
	}

	public int getLength(Topology iTopo, int[] arcs){
		int count = 0;
		for (int i:arcs){
			count+=iTopo.arcs[i<0?-i-1:i].length;
		}
		return count;
	}
	
	@Override
	public void fill(Topology iTopo, Display iDisplay, Colorifier iColorifier) {
		// TODO Auto-generated method stub
		
		for (int[] arc:arcs){
			
			int length = getLength(iTopo, arc);
			
			double[] x = new double[length];
			double[] y = new double[length];
			
			int count = 0;
			for (int innerArc:arc){
				
					boolean invert=false;
					if (innerArc<0) {
						invert = true;
						innerArc = -innerArc-1;
					}
				
					java.lang.Object[][] aDataArc = iTopo.arcs[innerArc];
					
					if (invert) {
						for (int i=aDataArc.length-1; i>=0; i--){
							x[count] = (Double) aDataArc[i][0];
							y[count] = (Double) aDataArc[i][1];
							count++;
						}
					} else {
						for (int i=0; i<aDataArc.length; i++){
							x[count] = (Double) aDataArc[i][0];
							y[count] = (Double) aDataArc[i][1];
							count++;
						}
					}
					
			}
			
			iDisplay.fillPolygons(x, y, count, iColorifier.getColor(properties));
		}
		
	}

	
}
