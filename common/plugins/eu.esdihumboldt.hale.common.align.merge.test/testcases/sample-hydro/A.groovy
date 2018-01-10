schema('A') {
	River {
		id()
		name()
	}
	
	RiverProperties {
		riverId()
		length(Double)
		width(Double)
	}
	
	Crossing {
		firstId()
		secondId()
	}
}