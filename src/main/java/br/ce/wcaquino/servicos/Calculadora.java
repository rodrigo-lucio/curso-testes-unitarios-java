package br.ce.wcaquino.servicos;

import br.ce.wcaquino.exception.NaoPodeDividirPor0Exception;

public class Calculadora {

	public int somar(int a, int b) {
		return a + b;
	}

	public int subtrair(int a, int b) {
		return a - b;
	}

	public int dividir(int a, int b) throws NaoPodeDividirPor0Exception {

		if (b == 0) {
			throw new NaoPodeDividirPor0Exception();
		}

		return a / b;
	}

	public int divide(String a, String b) {
		return Integer.valueOf(a) / Integer.valueOf(b);
	}
	
	public void imprime() {
		System.out.println("passei aqui");
	}

}
