package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.builder.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builder.FilmeBuilder.umFilmeSemEstoque;
import static br.ce.wcaquino.builder.LocacaoBuilder.umLocacao;
import static br.ce.wcaquino.builder.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.matchers.MatchersProprios.caiEm;
import static br.ce.wcaquino.matchers.MatchersProprios.caiNumaSegunda;
import static br.ce.wcaquino.matchers.MatchersProprios.isHoje;
import static br.ce.wcaquino.matchers.MatchersProprios.isHojeComDiferencaDias;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.ce.wcaquino.builder.LocacaoBuilder;
import br.ce.wcaquino.dao.LocacaoDao;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exception.FilmeSemEstoqueException;
import br.ce.wcaquino.exception.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoServiceTest {

	@Rule
	public ErrorCollector error = new ErrorCollector();

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@InjectMocks
	private LocacaoService locacaoService;

	// Deixado estatico pois o JUnit reinicializa todas as variaveis a cada teste
	private static int contadorDeTestes = 0;

	@Mock
	private LocacaoDao dao;
	@Mock
	private SPCService spc;
	@Mock
	private EmailService email;

	@Before
	public void inicializarCenarios() {
		MockitoAnnotations.initMocks(this);
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

		// Executa o teste s� quando n�o � sabado
		Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

		// Cenario
		Usuario usuario = umUsuario().agora();

		Filme filme = umFilme().comValorLocacao(5.0).agora();

		// A��o
		Locacao locacao = locacaoService.alugarFilme(usuario, Arrays.asList(filme));

		/*
		 * Passamos a utilizar o ErrorCollector para retornar todas as falhas de uma vez
		 * s� Nao bloqueando as proximas verifica��es se uma anterior falhar
		 */
		error.checkThat(locacao.getValor(), is(equalTo(5.0)));

		// error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
		// trocado a chamada acima pela de baixo porem agora com machers
		error.checkThat(locacao.getDataLocacao(), isHoje());

		// error.checkThat(isMesmaData(locacao.getDataRetorno(),
		// obterDataComDiferencaDias(1)), is(true));
		// trocado a chamada acima pela de baixo porem agora com machers
		error.checkThat(locacao.getDataRetorno(), isHojeComDiferencaDias(1));

		// Verifica��o
		/*
		 * assertTrue(isMesmaData(locacao.getDataLocacao(), new Date()));
		 * assertTrue(isMesmaData( locacao.getDataRetorno(),
		 * obterDataComDiferencaDias(1))); assertEquals(Double.valueOf(5.01),
		 * locacao.getValor(), 0.01);
		 */

		// Verifique que o valor da loca��o � igual a 5 - Mais c�digo mas tamb�m mais
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
	 * exception , quando apenas a exce��o importa para o teste, mas se precisar da
	 * mensagem nao se encaixa
	 */
	@Test(expected = FilmeSemEstoqueException.class)
	public void deveLancarExcecaoAoAlugarFilmeSemEstoque() throws Exception {
		Usuario usuario = umUsuario().agora();

		// Filme filme = umFilme().semEstoque().agora(); - tambem poderia ser assim, mas
		// criamos um especifico que esta abaixo
		Filme filme = umFilmeSemEstoque().agora();

		// A��o
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
			assertThat(e.getMessage(), is("Usu�rio vazio"));
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

		// Aqui n�o continua
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
		// S� executa o teste no sabado
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
	public void naoDeveAlugarFilmeParaUsuarioNegativadoSPC() throws Exception {
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());

		// Quando possuiNegativacao for chamado para tal usuario, retorne true
		// Esta linha altera o comportamento do mock, por padrao ele retorna false,
		// nesse caso alteramos para true, ensinando isso pra ele
		Mockito.when(spc.possuiNegativacao(usuario)).thenReturn(true);

		try {
			locacaoService.alugarFilme(usuario, filmes);
			Assert.fail();
		} catch (LocadoraException e) {
			Assert.assertThat(e.getMessage(), is("Usuario negativado no SPC"));
		}

		// Verifica no mock spc, se o metodo possui negativa��o foi chamado para tal
		// usuario
		Mockito.verify(spc).possuiNegativacao(usuario);
	}

	@Test
	public void deveEnviarEmailParaLocacoesAtrasadas() {
		Usuario usuario = umUsuario().agora();
		Usuario usuario2 = umUsuario().comNome("user 1").agora();
		Usuario usuario3 = umUsuario().comNome("user 3").agora();
		List<Locacao> locacoes = Arrays.asList(umLocacao().atrasada().comUsuario(usuario).agora(),
				umLocacao().comUsuario(usuario2).agora(), umLocacao().atrasada().comUsuario(usuario3).agora(),
				umLocacao().atrasada().comUsuario(usuario3).agora());

		// Alterando o comportamento do mock, estamos ensinando ele
		// Quando for chamado locacoes pendentes, retorne a lista de locacoes
		Mockito.when(dao.obterLocacoesPendentes()).thenReturn(locacoes);

		locacaoService.notificarAtrasos();

		// Verifica no mock, se o metodo notificar atrasos foi chamado para esse usuario
		Mockito.verify(email).notificarAtraso(usuario);

		Mockito.verify(email, Mockito.times(2)).notificarAtraso(usuario3);
		// == Times = diz que o m�todo foi chamado exatamente duas vezes para o usuario
		// Poderiamos utilizar Mockito.atLeast(2) = Se foi chamado pelo menos duas vezes
		// Ou Mockito.atMost(5) = No maximo 5

		// Verifica se o metodo nunca foi chamado para o usuario, que nesse caso a
		// locacao dele nao esta atrasada, logo ele nao recebeu o email
		Mockito.verify(email, Mockito.never()).notificarAtraso(usuario2);

		// Garante que mais nenhum email foi enviado fora os que foram informados acima
		Mockito.verifyNoMoreInteractions(email);

		// Caso queiramos garantir que o servi�o de SPC n�o foi chamado nesse teste
		// Mockito.verifyZeroInteractions(spc);
		// Claro que fica comentado por n�o teria logica nesse teste, colocado aqui
		// apenas para conhecimento

		// Aqui podemos verificar se o notificar atraso foi chamado pelo menos 3 vezes
		// para qualquer usuario
		Mockito.verify(email, Mockito.times(3)).notificarAtraso(Mockito.any(Usuario.class));
		// No caso, foi chamado uma vez para usuario e duas vezes para usuario3
	}

	@Test
	public void deveTratarErrosSpc() throws Exception {
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());

		Mockito.when(spc.possuiNegativacao(usuario)).thenThrow(new Exception("Falha catastrofica"));

		expectedException.expect(LocadoraException.class);
		expectedException.expectMessage("Problemas com o SPC, tente novamente");

		locacaoService.alugarFilme(usuario, filmes);
	}

	@Test
	public void deveProrrogarUmaLocacao() {
		Locacao locacao = LocacaoBuilder.umLocacao().agora();
		locacaoService.prorrogarLoacao(locacao, 3);
		
		//Captura Exatamente o que foi  
		ArgumentCaptor<Locacao> argumentCaptor = ArgumentCaptor.forClass(Locacao.class);
		Mockito.verify(dao).salvar(argumentCaptor.capture());
		Locacao locacaoRetornada = argumentCaptor.getValue();
		
		error.checkThat(locacaoRetornada.getValor(), is(12.0));
		error.checkThat(locacaoRetornada.getDataLocacao(), isHoje());
		error.checkThat(locacaoRetornada.getDataRetorno(), isHojeComDiferencaDias(3));
	
	}	

}
