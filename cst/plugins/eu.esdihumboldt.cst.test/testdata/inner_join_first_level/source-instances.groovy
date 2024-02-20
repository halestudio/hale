createCollection {
	
	/*
	 * Sets of instances that are joined 
	 */
	
	A {
		a('a1')
	}
	B1 {
		a('a1')
		b1('b1_1')
	}
	B2 {
		a('a1')
		b2('b1_2')
	}
	
	/*
	 * Sets of instances that are not joined because of missing links 
	 */
	
	A {
		a('a3')
	}
	B1 {
		a('a3')
		b1('b3')
	}
	
	A {
		a('a2')
	}
	B2 {
		a('a2')
		b2('b2')
	}
	
	A {
		a('a4')
	}

}