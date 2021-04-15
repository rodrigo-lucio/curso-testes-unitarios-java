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
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.Times;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import br.ce.wcaquino.builder.LocacaoBuilder;
import br.ce.wcaquino.dao.LocacaoDao;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exception.FilmeSemEstoqueException;
import br.ce.wcaquino.exception.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ LocacaoService.class })
public class LocacaoServiceTestComPowerMock {

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
		locacaoService = PowerMockito.spy(locacaoService);
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
		// Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(),
		// Calendar.SATURDAY));
		// Comentamos essa linha acima, agora utilizaremos o powermock
		// PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(9,
		// 4, 2021));
		// Agora utilizando com Calendar Static
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 14);
		calendar.set(Calendar.MONTH, Calendar.APRIL);
		calendar.set(Calendar.YEAR, 2021);

		PowerMockito.mockStatic(Calendar.class);
		PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);

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
	public void deveDevolverNaSegundaAoAlugarNoSabado() throws Exception {
		// Só executa o teste no sabado
		// Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(),
		// Calendar.SATURDAY));
		// Comentamos por que agora vamos utilizar o powermock para a classe date

		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());

		// O que isso quer dizer: Quando chamar um New (construtor) - sem nenhum
		// argumento (withNoArguments) entao retorne dia 10/04/2021
		// PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(10,
		// 4, 2021));
		// alem do mais, tivemos que adicionar esses decorators la em cima
		/*
		 * @RunWith(PowerMockRunner.class)
		 * 
		 * @PrepareForTest(LocacaoService.class)
		 */
		// ALTERAMOS Para Calendar o service de locacao
		// Sendo assim, para estaticos faremos assim =>
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 10);
		calendar.set(Calendar.MONTH, Calendar.APRIL);
		calendar.set(Calendar.YEAR, 2021);

		PowerMockito.mockStatic(Calendar.class);
		PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);

		Locacao retorno = locacaoService.alugarFilme(usuario, filmes);
		System.out.println("Tetetetetet" + retorno.getDataRetorno());
		boolean isSegunda = DataUtils.verificarDiaSemana(retorno.getDataRetorno(), Calendar.MONDAY);
		assertTrue(isSegunda);

		assertThat(retorno.getDataRetorno(), caiEm(Calendar.MONDAY));
		assertThat(retorno.getDataRetorno(), caiNumaSegunda());

		// Verifica se o new Date() foi dado o new() duas vezes
		// Comentamos aqui pois comecamos a utilizar o Celandar estatico
		// PowerMockito.verifyNew(Date.class, Mockito.times(2)).withNoArguments();
		PowerMockito.verifyNoMoreInteractions(Calendar.class, Mockito.times(2));

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

		// Verifica no mock spc, se o metodo possui negativação foi chamado para tal
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
		// == Times = diz que o método foi chamado exatamente duas vezes para o usuario
		// Poderiamos utilizar Mockito.atLeast(2) = Se foi chamado pelo menos duas vezes
		// Ou Mockito.atMost(5) = No maximo 5

		// Verifica se o metodo nunca foi chamado para o usuario, que nesse caso a
		// locacao dele nao esta atrasada, logo ele nao recebeu o email
		Mockito.verify(email, Mockito.never()).notificarAtraso(usuario2);

		// Garante que mais nenhum email foi enviado fora os que foram informados acima
		Mockito.verifyNoMoreInteractions(email);

		// Caso queiramos garantir que o serviço de SPC não foi chamado nesse teste
		// Mockito.verifyZeroInteractions(spc);
		// Claro que fica comentado por não teria logica nesse teste, colocado aqui
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

		// Captura Exatamente o que foi modificado dentro de prorrogar locacao, e
		// passado para o salvar la dentro
		ArgumentCaptor<Locacao> argumentCaptor = ArgumentCaptor.forClass(Locacao.class);
		Mockito.verify(dao).salvar(argumentCaptor.capture());
		Locacao locacaoRetornada = argumentCaptor.getValue();
		System.out.println(locacaoRetornada.toString());
		error.checkThat(locacaoRetornada.getValor(), is(12.0));
		error.checkThat(locacaoRetornada.getDataLocacao(), isHoje());
		error.checkThat(locacaoRetornada.getDataRetorno(), isHojeComDiferencaDias(3));

	}

	@Test
	public void deveAlugarFilmeSemCalcularValor() throws Exception {
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		//Mocando o metodo privado "calcularValorLocacao", nao queremos nos preocupar com isso nesse teste
		//Entao estamos dizendo que queremos voltar o valor 1.0, com o parametro filmes
		PowerMockito.doReturn(1.0).when(locacaoService, "calcularValorLocacao", filmes);
		//E o metodo original do service nao vai ser chamado
		
		Locacao locacao = locacaoService.alugarFilme(usuario, filmes);
		
		Assert.assertThat(locacao.getValor(), is(1.0));
		
		//Verifica se o metodo privado foi invocado
		PowerMockito.verifyPrivate(locacaoService).invoke("calcularValorLocacao", filmes);
	}

	@Test
	public void deveCalcularValorLocacao() throws Exception {
		//Executar de fato os metodos privados diretamente com o Whitebox
		List<Filme> filmes = Arrays.asList(umFilme().agora());		
		Double valor = (Double) Whitebox.invokeMethod(locacaoService, "calcularValorLocacao", filmes);
		Assert.assertThat(valor, is(4.0));
	}
}
