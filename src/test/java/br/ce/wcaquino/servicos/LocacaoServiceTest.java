package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.builder.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builder.FilmeBuilder.umFilmeSemEstoque;
import static br.ce.wcaquino.builder.UsuarioBuilder.umUsuario;
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
import org.mockito.Mockito;

import br.ce.wcaquino.builder.FilmeBuilder;
import br.ce.wcaquino.builder.UsuarioBuilder;
import br.ce.wcaquino.dao.LocacaoDao;
import br.ce.wcaquino.dao.LocacaoDaoFake;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exception.FilmeSemEstoqueException;
import br.ce.wcaquino.exception.LocadoraException;
import br.ce.wcaquino.matchers.DiaSemanaMatcher;
import br.ce.wcaquino.matchers.MatchersProprios;
import br.ce.wcaquino.utils.DataUtils;
import buildermaster.BuilderMaster;

public class LocacaoServiceTest {

	@Rule
	public ErrorCollector error = new ErrorCollector();

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private LocacaoService locacaoService;

	// Deixado estatico pois o JUnit reinicializa todas as variaveis a cada teste
	private static int contadorDeTestes = 0;
	
	private LocacaoDao dao;
	private SPCService spc;

	@Before
	public void inicializarCenarios() {
		System.out.println("Before");
		locacaoService = new LocacaoService();
		dao = new Mockito().mock(LocacaoDao.class);
		locacaoService.setLocacaoDao(dao);
		contadorDeTestes++;
		System.out.println(String.format("Iniciando teste numeri %s", contadorDeTestes));
		spc = Mockito.mock(SPCService.class);
		locacaoService.setSPCService(spc);

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

		// Executa o teste só quando não é sabado
		Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

		// Cenario
		Usuario usuario = umUsuario().agora();

		Filme filme = umFilme().comValorLocacao(5.0).agora();

		// Ação
		Locacao locacao = locacaoService.alugarFilme(usuario, Arrays.asList(filme));

		/*
		 * Passamos a utilizar o ErrorCollector para retornar todas as falhas de uma vez
		 * só Nao bloqueando as proximas verificações se uma anterior falhar
		 */
		error.checkThat(locacao.getValor(), is(equalTo(5.0)));

		// error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
		// trocado a chamada acima pela de baixo porem agora com machers
		error.checkThat(locacao.getDataLocacao(), isHoje());

		// error.checkThat(isMesmaData(locacao.getDataRetorno(),
		// obterDataComDiferencaDias(1)), is(true));
		// trocado a chamada acima pela de baixo porem agora com machers
		error.checkThat(locacao.getDataRetorno(), isHojeComDiferencaDias(1));

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
		Usuario usuario = umUsuario().agora();

		// Filme filme = umFilme().semEstoque().agora(); - tambem poderia ser assim, mas
		// criamos um especifico que esta abaixo
		Filme filme = umFilmeSemEstoque().agora();

		// Ação
		Locacao locacao = locacaoService.alugarFilme(usuario, Arrays.asList(filme));
	}

	// Forma robusta - se ficar na duvida qual usar, utiliza a forma robusta
	@Test
	public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException {
		// Cenario
		Filme f = umFilme().agora();
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
		Usuario usuario = umUsuario().agora();

		expectedException.expect(LocadoraException.class);
		expectedException.expectMessage("Sem filmes");
		locacaoService.alugarFilme(usuario, null);

		// Aqui não continua
		System.out.println("Continua o codigo - Forma nova");

	}

	@Test
	public void devePagar75PctNoFilme3() throws FilmeSemEstoqueException, LocadoraException {
		Usuario usuario = umUsuario().agora();
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
		Usuario usuario = umUsuario().agora();
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
		Usuario usuario = umUsuario().agora();
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
		Usuario usuario = umUsuario().agora();
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
		// Só executa o teste no sabado
		Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = new ArrayList<Filme>();
		filmes.add(umFilme().agora());
		Locacao retorno = locacaoService.alugarFilme(usuario, filmes);

		boolean isSegunda = DataUtils.verificarDiaSemana(retorno.getDataRetorno(), Calendar.MONDAY);
		assertTrue(isSegunda);

		assertThat(retorno.getDataRetorno(), caiEm(Calendar.MONDAY));
		assertThat(retorno.getDataRetorno(), caiNumaSegunda());

	}

	@Test
	public void naoDeveAlugarFilmeParaUsuarioNegativadoSPC() throws FilmeSemEstoqueException, LocadoraException {
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		//Quando possuiNegativacao for chamado, retorne true
		Mockito.when(spc.possuiNegativacao(usuario)).thenReturn(true);
		expectedException.expect(LocadoraException.class);
		expectedException.expectMessage("Usuario negativado no SPC");

		locacaoService.alugarFilme(usuario, filmes);

	}
 
}
