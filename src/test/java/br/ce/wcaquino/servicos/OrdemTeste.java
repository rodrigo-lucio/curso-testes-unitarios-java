package br.ce.wcaquino.servicos;

import static org.junit.Assert.assertEquals;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING) // Executa os testes em ordem alfabetica
public class OrdemTeste {

	public static int contador = 0;

	@Test
	public void inicia() {
		contador = 1;
	}

	@Test
	public void verifica() {
		assertEquals(1, contador);
	}

}
