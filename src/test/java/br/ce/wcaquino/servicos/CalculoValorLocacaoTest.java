package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.builder.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builder.UsuarioBuilder.umUsuario;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.ce.wcaquino.dao.LocacaoDao;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exception.FilmeSemEstoqueException;
import br.ce.wcaquino.exception.LocadoraException;

@RunWith(Parameterized.class)
public class CalculoValorLocacaoTest {

	@InjectMocks
	private LocacaoService locacaoService;

	@Parameter // Primeiro registro do array do metodo getParametros
	public List<Filme> filmes;

	@Parameter(value = 1) // Segundo registro do array do metodo getParametros
	public Double valorLocacao;

	@Parameter(value = 2) 
	public String cenario;

	@Mock
	private LocacaoDao dao;
	@Mock
	private SPCService spc;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		System.out.println("Iniciando 3");
		CalculadoraTest.ordem.append("3");
	}

	@After
	public void finalizando() {
		System.out.println("finalizando 3");
	}

	@AfterClass
	public static void depoisDeExecutarAClasse() {
		System.out.println(CalculadoraTest.ordem.toString());
	}
	
	private static Filme filme1 = umFilme().agora();
	private static Filme filme2 = umFilme().agora();
	private static Filme filme3 = umFilme().agora();

	private static Filme filme4 = umFilme().agora();
	private static Filme filme5 = umFilme().agora();
	private static Filme filme6 = umFilme().agora();

	/*
	 * para cada linha dessas, sera criado um teste pelo JUnit
	 */
	@Parameters(name = "{2}")
	public static Collection<Object[]> getParametros() {
		return Arrays.asList(new Object[][] { { Arrays.asList(filme1, filme2), 8.0, "2 Filmes: Sem desconto" },
				{ Arrays.asList(filme1, filme2, filme3), 11.0, "3 Filmes: 25% de desconto" },
				{ Arrays.asList(filme1, filme2, filme3, filme4), 13.0, "4 Filmes: 50% de desconto" },
				{ Arrays.asList(filme1, filme2, filme3, filme4, filme5), 14.0, "5 Filmes: 75% de desconto" },
				{ Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6), 14.0,
						"6 Filmes: 100% de desconto" }, });
	}

	/*
	 * Aqui testamos todos os m�todos da outra classe em apenas 01, com uma massa de
	 * dados
	 */
	@Test
	public void deveCalcularValorLocacaoConsiderandoDescontos() throws FilmeSemEstoqueException, LocadoraException {
		Usuario usuario = umUsuario().agora();
		Locacao locacao = locacaoService.alugarFilme(usuario, filmes);
		assertThat(locacao.getValor(), is(valorLocacao));
	}

	@Test
	public void imprimir() {
		System.out.println(valorLocacao);
	}

}
