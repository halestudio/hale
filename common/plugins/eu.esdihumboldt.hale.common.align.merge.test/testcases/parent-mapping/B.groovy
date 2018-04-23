import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty

schema('B') {
	B1 {
		id()
		geom {
			Point(GeometryProperty)
		}
	}
}