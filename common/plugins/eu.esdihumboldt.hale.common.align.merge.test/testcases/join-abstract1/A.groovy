schema('A') {
	A1 {
		a1()
	}
	
	A2 {
		a2()
		a1(cardinality: '?')
	}
	
	A3 {
		a3()
		a1(cardinality: '?')
	}
	
	A4 {
		a4()
		a3()
	}
}