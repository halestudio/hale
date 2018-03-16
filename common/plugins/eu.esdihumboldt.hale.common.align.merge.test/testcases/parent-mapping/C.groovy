import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty

schema('C') {
	C1 {
		id()
		geom(GeometryProperty)
	}
}