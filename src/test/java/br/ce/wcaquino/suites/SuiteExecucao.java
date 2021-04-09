package br.ce.wcaquino.suites;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import br.ce.wcaquino.servicos.CalculadoraTest;
import br.ce.wcaquino.servicos.CalculoValorLocacaoTest;
import br.ce.wcaquino.servicos.LocacaoServiceTest;

/*
 * Executa todos os testes de uma vez só
 */
@RunWith(Suite.class)
@SuiteClasses({ CalculadoraTest.class, CalculoValorLocacaoTest.class, LocacaoServiceTest.class })
public class SuiteExecucao {

	@BeforeClass
	public static void before() {
		// Executa tudo antes de toda a bateria
		System.out.println("before");
	}

	@AfterClass
	public static void after() {
		// Executa tudo depois de toda a bateria
		System.out.println("after");
	}
}
