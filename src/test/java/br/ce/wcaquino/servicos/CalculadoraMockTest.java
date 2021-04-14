package br.ce.wcaquino.servicos;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class CalculadoraMockTest {

	@Mock
	private Calculadora calcMock;

	@Spy
	private Calculadora calcSpy;

//	Spy nao funciona com interfaces
//	@Spy
//	private EmailService email;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void teste() {
		Calculadora calculadora = Mockito.mock(Calculadora.class);

		ArgumentCaptor<Integer> argumentCaptor = ArgumentCaptor.forClass(Integer.class);

		Mockito.when(calculadora.somar(1, 2)).thenReturn(3);

		// Se fizemos isso
		System.out.println(calculadora.somar(1, 8));
		// Vai retornar 0, pois nao ensinamos isso para o mock

		// Sendo assim,
		// Se usarmos mais de dois parametros, os dois tem que ser matchers
		// Entao, somando 1 a qualquer numero, vai retornar 5, se somar 2 a qualquer
		// numero vai retornar 0
		Mockito.when(calculadora.somar(argumentCaptor.capture(), argumentCaptor.capture())).thenReturn(5);

		System.out.println(calculadora.somar(1, 8)); // ==Retorna 5
		System.out.println(calculadora.somar(2, 8)); // ==Retorna 0
		System.out.println(argumentCaptor.getAllValues());
	}

	@Test
	public void devoMostrarDiferencaEntreMockESpy() {
		Mockito.when(calcMock.somar(1, 6)).thenReturn(7);
		Mockito.when(calcSpy.somar(1, 6)).thenReturn(7);

		
		//A diferenca entre o mock e o spy, é que o mock quando nao sabe o que fazer retorna 0, ja o spy executa o metodo de fato
		
		//Caso queiramos que o mock excecute de fato o metodo para tais casos devemos fazer assim:
		Mockito.when(calcMock.somar(1, 5)).thenCallRealMethod();
		System.out.println("Mock:" + calcMock.somar(1, 5 ));
		System.out.println("Spy:" + calcSpy.somar(1, 6));
		
		
		System.out.println("Para metodos void:");
		System.out.println("Mock: ");
		calcMock.imprime();  //nao faz nada
		System.out.println("Spy: ");
		calcSpy.imprime();  //imprime o qua tem no metodo normalmente
		
	}
}
