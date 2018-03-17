import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty

schema('A') {
	A1 {
		id()
		geom(GeometryProperty)
	}
}