
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

//@RunWith(ParallelRunner.class) // Rodando com a nossa classe de execução paralela -- Comentado pois comecamos a utilizar o surfire
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
		// Cenário
		int a = 5;
		int b = 3;
		// Ação
		int resultado = calc.somar(a, b);
		// Verificação
		assertEquals(8, resultado);

	}

	@Test
	public void deveSubtrairDoisValores() {
		// Cenário
		int a = 5;
		int b = 3;
		// Ação
		int resultado = calc.subtrair(a, b);
		// Verificação
		assertEquals(2, resultado);
	}

	@Test
	public void deveDividirDoisValores() {
		// Cenário
		int a = 6;
		int b = 3;
		// Ação
		int resultado = calc.dividir(a, b);
		// Verificação
		assertEquals(2, resultado);
	}

	@Test(expected = NaoPodeDividirPor0Exception.class)
	public void deveLancarExcecaoAoDividirPor0() throws NaoPodeDividirPor0Exception {
		// Cenário
		int a = 6;
		int b = 0;
		// Açãod
		int resultado = calc.dividir(a, b);
		// Verificação
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
