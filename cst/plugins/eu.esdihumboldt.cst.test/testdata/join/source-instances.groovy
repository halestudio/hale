createCollection {
	
	/*
	 * Sets of instances that are joined 
	 */
	
	A {
		a('a1')
	}
	B {
		a('a1')
		b('b1')
	}
	C {
		b('b1')
		c('c1')
	}
	
	A {
		a('a3')
	}
	B {
		a('a3')
		b('b1')
	}
	
	/*
	 * Sets of instances that are not joined because of missing links 
	 */
	
	A {
		a('a2')
	}
	B {
		a('a2')
		b('b2')
	}
	C {
		b('c2')
		c('c2')
	}
	
	A {
		a('a4')
	}
	B {
		a('a4')
		b('b4')
	}
	
	A {
		a('a5')
	}

}