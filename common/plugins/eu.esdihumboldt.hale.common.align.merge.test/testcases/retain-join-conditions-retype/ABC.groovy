schema('ABC') {
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