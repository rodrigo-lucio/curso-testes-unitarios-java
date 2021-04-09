package br.ce.wcaquino.servicos;

import org.junit.Test;
import org.mockito.Mockito;

public class CalculadoraMockTest {

	@Test
	public void teste() {
		Calculadora calculadora = Mockito.mock(Calculadora.class);
		
		Mockito.when(calculadora.somar(1, 2)).thenReturn(3);
		
		//Se fizemos isso
		System.out.println(calculadora.somar(1, 8));
		//Vai retornar 0, pois nao ensinamos isso para o mock
		
		//Sendo assim,
		//Se usarmos mais de dois parametros, os dois tem que ser matchers
		//Entao, somando 1 a qualquer numero, vai retornar 5, se somar 2 a qualquer numero vai retornar 0
		Mockito.when(calculadora.somar(Mockito.eq(1), Mockito.anyInt())).thenReturn(5);
		
		System.out.println(calculadora.somar(1, 8)); //==Retorna 5
		System.out.println(calculadora.somar(2, 8)); //==Retorna 0
	}
}
