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
		a1()
		a4()
	}
	
	A4 {
		a4()
	}
	
	A5 {
		a5()
		a4()
	}
	
	A6 {
		a6()
		a4()
	}
	
	A7 {
		a7()
		a6()
	}
}