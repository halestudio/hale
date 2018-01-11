schema('C') {
	Watercourse {
		id()
		name()
		length(Double)
		width(Double)
	}
	
	StandingWater {
		id()
		name()
		maxDepth(Double)
		inflow(cardinality: '*')
		outflow(cardinality: '*')
	}
}