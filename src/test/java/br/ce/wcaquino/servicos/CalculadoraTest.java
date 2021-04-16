
package br.ce.wcaquino.servicos;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import br.ce.wcaquino.exception.NaoPodeDividirPor0Exception;
import br.ce.wcaquino.runner.ParallelRunner;

//@RunWith(ParallelRunner.class) // Rodando com a nossa classe de execu��o paralela -- Comentado pois comecamos a utilizar o surfire
public class CalculadoraTest {

	private Calculadora calc;
	
	public static StringBuffer ordem = new StringBuffer();

	@Before
	public void iniciarVariaveis() {
		calc = new Calculadora();
		System.out.println("inicializando");
		ordem.append("1");
	}

	@After
	public void finalizando() {
		System.out.println("finalizando");
	}

	@AfterClass
	public static void depoisDeExecutarAClasse() {
		System.out.println(ordem.toString());
	}
	
	@Test
	public void deveSomarDoisValores() {
		// Cen�rio
		int a = 5;
		int b = 3;
		// A��o
		int resultado = calc.somar(a, b);
		// Verifica��o
		assertEquals(8, resultado);

	}

	@Test
	public void deveSubtrairDoisValores() {
		// Cen�rio
		int a = 5;
		int b = 3;
		// A��o
		int resultado = calc.subtrair(a, b);
		// Verifica��o
		assertEquals(2, resultado);
	}

	@Test
	public void deveDividirDoisValores() {
		// Cen�rio
		int a = 6;
		int b = 3;
		// A��o
		int resultado = calc.dividir(a, b);
		// Verifica��o
		assertEquals(2, resultado);
	}

	@Test(expected = NaoPodeDividirPor0Exception.class)
	public void deveLancarExcecaoAoDividirPor0() throws NaoPodeDividirPor0Exception {
		// Cen�rio
		int a = 6;
		int b = 0;
		// A��od
		int resultado = calc.dividir(a, b);
		// Verifica��o
		assertEquals(2, resultado);
	}

	@Test
	public void deveDividir() {
		String a = "6";
		String b = "3";
		int resultado = calc.divide(a, b);
		assertEquals(2, resultado);
	}
}
