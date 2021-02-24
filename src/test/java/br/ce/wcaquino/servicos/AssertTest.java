package br.ce.wcaquino.servicos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import br.ce.wcaquino.entidades.Usuario;

public class AssertTest {

	@Test
	public void test() {
		assertTrue(true);
		
		//Com margem de erro de 0.01
		assertEquals(0.512, 0.513, 0.01);	
		assertEquals(0, 0);
	
		int i = 10;
		Integer i2 = 10;
		assertEquals(Integer.valueOf(i), i2);
		assertTrue("bola".equalsIgnoreCase("BOla"));
		assertTrue("bola".startsWith("bol"));
	
		Usuario u1 = new Usuario("Rodrigo");
		Usuario u2 = new Usuario("Rodrigo");
		Usuario u3 = null;
		Usuario u4 = u1;
		assertEquals(u1, u2);
		
		assertTrue(u3 == null);	
		assertNull(u3);		
		
		//Se sao da mesma instancia
		assertSame(u1, u4);
		
		
	}

}
