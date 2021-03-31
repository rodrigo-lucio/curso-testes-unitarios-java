
package br.ce.wcaquino.servicos;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import br.ce.wcaquino.exception.NaoPodeDividirPor0Exception;

public class CalculadoraTest {
	
	private Calculadora calc;
	
	@Before
	public void iniciarVariaveis() {
		calc = new Calculadora(); 
	}
	
	@Test
	public void deveSomarDoisValores() {
		//Cen�rio
		int a = 5;
		int b = 3;
		//A��o
		int resultado = calc.somar(a, b);
		//Verifica��o
		assertEquals(8, resultado);
		
	}
	
	@Test
	public void deveSubtrairDoisValores() {
		//Cen�rio
		int a = 5;
		int b = 3;
		//A��o
		int resultado = calc.subtrair(a, b);
		//Verifica��o
		assertEquals(2, resultado);	
	}
	
	@Test
	public void deveDividirDoisValores() {
		//Cen�rio
		int a = 6;
		int b = 3;
		//A��o
		int resultado = calc.dividir(a, b);
		//Verifica��o
		assertEquals(2, resultado);	
	}
	
	@Test(expected = NaoPodeDividirPor0Exception.class)
	public void deveLancarExcecaoAoDividirPor0() throws NaoPodeDividirPor0Exception{
		//Cen�rio
		int a = 6;
		int b = 0;
		//A��od
		int resultado = calc.dividir(a, b);
		//Verifica��o
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
