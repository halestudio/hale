schema('source') {
	A {
		a()
	}

	B {
		a()
		b(cardinality: '?')
	}

	C {
		b()
		c(cardinality: '?')
	}
}