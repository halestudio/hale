package json.topojson.topology;

import java.awt.Color;

import json.graphic.Display;
import json.topojson.geom.sub.Position;

public class Arc {

	Position[] _points;
	int _id;
	boolean _shared;
	
	public Arc(Position[] iSeq){
		_points = iSeq;
		_shared = false;
	}
	
	public void setId(int iId){
		_id = iId;
	}

	public int getId(){
		return _id;
	}

	public void setShared(){
		_shared = true;
	}
	
	public Position first(){
		return _points[0];
	}
	
	public Position last(){
		return _points[_points.length-1];
	}
	
	public int firstHash(){
		return _points[0].hashCode();
	}
	
	public int lastHash(){
		return _points[_points.length-1].hashCode();
	}
	

	@Override
	public boolean equals(java.lang.Object iObject){
		return !(equals_reverse(iObject)==0);
	}
	
	public int equals_reverse(java.lang.Object iObject){
		
		if (!(iObject instanceof Arc)) return 0;
		Arc aArc = (Arc) iObject;
		if (aArc._points.length!=_points.length) return 0;
		
		
		boolean idem=true;
		for (int i=0; i<_points.length;i++){
			if (_points[i].hashCode() != aArc._points[i].hashCode()) {
				idem = false;
				break;
			}
		}
		
		if (!idem) {
			for (int i=0; i<_points.length;i++){
				if (_points[i].hashCode() != aArc._points[_points.length-i-1].hashCode()) return 0;
			}
			return -1;
		}
		
		return 1;
	}
	
	public void draw(Display iDisp) {
		
		iDisp.drawPoint(_points[0]._x, _points[0]._y, 2, Color.GREEN);
		iDisp.drawPoint(_points[_points.length-1]._x, _points[_points.length-1]._y, 2, Color.GREEN);
		
		for (int i=0; i<_points.length-1; i++) {
			
			iDisp.drawLine(_points[i]._x,_points[i]._y,
						   _points[i+1]._x,_points[i+1]._y, _shared ? Color.YELLOW : Color.BLUE);
			
		}
		
	}
	
}
