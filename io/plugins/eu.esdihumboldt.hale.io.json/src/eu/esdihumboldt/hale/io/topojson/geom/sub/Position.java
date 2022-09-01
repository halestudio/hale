package json.topojson.geom.sub;

import java.awt.Color;
import java.nio.ByteBuffer;
import java.util.Arrays;

import json.graphic.Display;

public class Position {

	public double _x;
	public double _y;
	public int hash;
	
	private final static double FACT = 10000000;
	
	public Position(double iX, double iY){
		_x = iX;//Math.round(iX*FACT)/FACT;
		_y = iY;//Math.round(iY*FACT)/FACT;
		
		byte[] aBuffer = new byte[8*2];
		ByteBuffer.wrap(aBuffer).putDouble(_x).putDouble(_y);
		
		hash = Arrays.hashCode(aBuffer);
		
	}
	
	public Position clone(){
		return new Position(_x,_y);
	}
	
	@Override
	public int hashCode(){
		return hash;
	}
	
	public void draw(Display iDisp){
		iDisp.drawPoint(_x, _y, 2, Color.RED);
	}

}
