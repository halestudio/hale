schema('B') {
	River {
		id()
		name()
		length(Double)
		width(Double)
	}
	
	Lake {
		id()
		name()
		maxDepth(Double)
	}
	
	LakeFlow {
		lakeId()
		inflow(cardinality: '*') {
			riverId()
		}
		outflow(cardinality: '*') {
			riverId()
		}
	}
	
	Measurement {
		objectId()
		measure()
		value(Double)
	}
}