
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
		//Cenário
		int a = 5;
		int b = 3;
		//Ação
		int resultado = calc.somar(a, b);
		//Verificação
		assertEquals(8, resultado);
		
	}
	
	@Test
	public void deveSubtrairDoisValores() {
		//Cenário
		int a = 5;
		int b = 3;
		//Ação
		int resultado = calc.subtrair(a, b);
		//Verificação
		assertEquals(2, resultado);	
	}
	
	@Test
	public void deveDividirDoisValores() {
		//Cenário
		int a = 6;
		int b = 3;
		//Ação
		int resultado = calc.dividir(a, b);
		//Verificação
		assertEquals(2, resultado);	
	}
	
	@Test(expected = NaoPodeDividirPor0Exception.class)
	public void deveLancarExcecaoAoDividirPor0() throws NaoPodeDividirPor0Exception{
		//Cenário
		int a = 6;
		int b = 0;
		//Açãod
		int resultado = calc.dividir(a, b);
		//Verificação
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
