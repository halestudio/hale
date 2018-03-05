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
	
	Lake {
		id()
		name()
	}
	
	LakeProperties {
		lakeId()
		maxDepth(Double)
	}
	
	def directionType = DirectionType(binding: String, enum: ['in', 'out'])
	
	Connection {
		streamId()
		standingId()
		direction(directionType)
	}
	
	Observation {
		objectId()
		type()
		value(Double)
	}
}