package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.matchers.MatchersProprios.*;
import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.security.Provider.Service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exception.FilmeSemEstoqueException;
import br.ce.wcaquino.exception.LocadoraException;
import br.ce.wcaquino.matchers.DiaSemanaMatcher;
import br.ce.wcaquino.matchers.MatchersProprios;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoServiceTest {

	@Rule
	public ErrorCollector error = new ErrorCollector();

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private LocacaoService locacaoService;

	// Deixado estatico pois o JUnit reinicializa todas as variaveis a cada teste
	private static int contadorDeTestes = 0;

	@Before
	public void inicializarCenarios() {
		System.out.println("Before");
		locacaoService = new LocacaoService();
		contadorDeTestes++;
		System.out.println(String.format("Iniciando teste numeri %s", contadorDeTestes));

	}

	@After
	public void depoisDoTeste() {
		System.out.println("After");
	}

	@BeforeClass
	public static void antesDeInstanciarClasses() {
		System.out.println("antesDeInstanciarClasses");
	}

	@AfterClass
	public static void depoisDaClasseSerDestruida() {
		System.out.println("depoisDaClasseSerDestruida");
	}

	@Test
	public void deveAlugarFilme() throws Exception {
		
		//Executa o teste só quando não é sabado
		Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		// Cenario
		Usuario usuario = new Usuario();
		usuario.setNome("Rodrigo");

		Filme filme = new Filme();
		filme.setNome("Harry Potter");
		filme.setPrecoLocacao(5.0);
		filme.setEstoque(3);

		// Ação
		Locacao locacao = locacaoService.alugarFilme(usuario, Arrays.asList(filme));

		/*
		 * Passamos a utilizar o ErrorCollector para retornar todas as falhas de uma vez
		 * só Nao bloqueando as proximas verificações se uma anterior falhar
		 */
		error.checkThat(locacao.getValor(), is(equalTo(5.0)));
		error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
		error.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));

		// Verificação
		/*
		 * assertTrue(isMesmaData(locacao.getDataLocacao(), new Date()));
		 * assertTrue(isMesmaData( locacao.getDataRetorno(),
		 * obterDataComDiferencaDias(1))); assertEquals(Double.valueOf(5.01),
		 * locacao.getValor(), 0.01);
		 */

		// Verifique que o valor da locação é igual a 5 - Mais código mas também mais
		// fluidez com o assertThat
		/*
		 * assertThat(locacao.getValor(), is(equalTo(5.0)));
		 * assertThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
		 * assertThat(isMesmaData(locacao.getDataRetorno(),
		 * obterDataComDiferencaDias(1)), is(true));
		 */
	}

	/*
	 * Testes para tratamento de exceptions Forma elegante - Teste espera uma
	 * exception , quando apenas a exceção importa para o teste, mas se precisar da
	 * mensagem nao se encaixa
	 */
	@Test(expected = FilmeSemEstoqueException.class)
	public void deveLancarExcecaoAoAlugarFilmeSemEstoque() throws Exception {
		Usuario usuario = new Usuario();
		usuario.setNome("Rodrigo");

		Filme filme = new Filme();
		filme.setNome("Harry Potter");
		filme.setPrecoLocacao(5.0);
		filme.setEstoque(0);

		// Ação
		Locacao locacao = locacaoService.alugarFilme(usuario, Arrays.asList(filme));
	}

	// Forma robusta - se ficar na duvida qual usar, utiliza a forma robusta
	@Test
	public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException {
		// Cenario
		Filme f = new Filme("Rodrigo FIlm", 1, 4.0);
		// acao
		try {
			locacaoService.alugarFilme(null, Arrays.asList(f));
			fail();
		} catch (LocadoraException e) {
			assertThat(e.getMessage(), is("Usuário vazio"));
		}

		// Continua na forma robusta, essa mensagem vai ser exibida, diferente da forma
		// nova , abaixo
		System.out.println("Continua o codigo - forma robusta");
	}

	// Forma nova
	@Test
	public void naoDeveAlugarFilmeSemFilme() throws FilmeSemEstoqueException, LocadoraException {
		// Cenario
		Usuario usuario = new Usuario("Rodrigo");

		expectedException.expect(LocadoraException.class);
		expectedException.expectMessage("Sem filmes");
		locacaoService.alugarFilme(usuario, null);

		// Aqui não continua
		System.out.println("Continua o codigo - Forma nova");

	}

	@Test
	public void devePagar75PctNoFilme3() throws FilmeSemEstoqueException, LocadoraException {
		Usuario usuario = new Usuario("Usuario 01");
		List<Filme> filmes = new ArrayList<Filme>();
		filmes.add(new Filme("Harry potter", 2, 4.0));
		filmes.add(new Filme("Harry potter 2", 2, 4.0));
		filmes.add(new Filme("Harry potter 3", 2, 4.0));

		Locacao locacao = locacaoService.alugarFilme(usuario, filmes);

		// Verificacao de valores dos filmes
		assertThat(locacao.getValor(), is(11.0));

	}

	@Test
	public void devePagar50PctNoFilme4() throws FilmeSemEstoqueException, LocadoraException {
		Usuario usuario = new Usuario("Usuario 01");
		List<Filme> filmes = new ArrayList<Filme>();
		filmes.add(new Filme("Harry potter", 2, 4.0));
		filmes.add(new Filme("Harry potter 2", 2, 4.0));
		filmes.add(new Filme("Harry potter 3", 2, 4.0));
		filmes.add(new Filme("Harry potter 3", 2, 4.0));

		Locacao locacao = locacaoService.alugarFilme(usuario, filmes);

		// Verificacao de valores dos filmes
		assertThat(locacao.getValor(), is(13.0));

	}

	@Test
	public void devePagar25PctNoFilme5() throws FilmeSemEstoqueException, LocadoraException {
		Usuario usuario = new Usuario("Usuario 01");
		List<Filme> filmes = new ArrayList<Filme>();
		filmes.add(new Filme("Harry potter", 2, 4.0));
		filmes.add(new Filme("Harry potter 2", 2, 4.0));
		filmes.add(new Filme("Harry potter 3", 2, 4.0));
		filmes.add(new Filme("Harry potter 4", 2, 4.0));
		filmes.add(new Filme("Harry potter 5", 2, 4.0));

		Locacao locacao = locacaoService.alugarFilme(usuario, filmes);

		// Verificacao de valores dos filmes
		assertThat(locacao.getValor(), is(14.0));

	}

	@Test
	public void devePagar0PctNoFilme6() throws FilmeSemEstoqueException, LocadoraException {
		Usuario usuario = new Usuario("Usuario 01");
		List<Filme> filmes = new ArrayList<Filme>();
		filmes.add(new Filme("Harry potter", 2, 4.0));
		filmes.add(new Filme("Harry potter 2", 2, 4.0));
		filmes.add(new Filme("Harry potter 3", 2, 4.0));
		filmes.add(new Filme("Harry potter 4", 2, 4.0));
		filmes.add(new Filme("Harry potter 5", 2, 4.0));
		filmes.add(new Filme("Harry potter 6", 2, 4.0));

		Locacao locacao = locacaoService.alugarFilme(usuario, filmes);

		// Verificacao de valores dos filmes
		assertThat(locacao.getValor(), is(14.0));

	}

	@Test 
	public void deveDevolverNaSegundaAoAlugarNoSabado() throws FilmeSemEstoqueException, LocadoraException {
		//Só executa o teste no sabado
		Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		Usuario usuario = new Usuario("Usuario1");
		List<Filme> filmes = new ArrayList<Filme>();
		filmes.add(new Filme("MIB Homens de preto", 3, 4.3));
		Locacao retorno = locacaoService.alugarFilme(usuario, filmes);
		
		boolean isSegunda = DataUtils.verificarDiaSemana(retorno.getDataRetorno(), Calendar.MONDAY);
		assertTrue(isSegunda);
		
		assertThat(retorno.getDataRetorno(), caiEm(Calendar.MONDAY));
		assertThat(retorno.getDataRetorno(), caiNumaSegunda());
	
	}
}
