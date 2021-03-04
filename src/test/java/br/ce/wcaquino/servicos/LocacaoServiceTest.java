package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;

import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

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

	@Test
	public void testeLocacao() throws Exception {
		// Cenario
		LocacaoService locacaoService = new LocacaoService();
		System.out.println("DSADAS");
		Usuario usuario = new Usuario();
		usuario.setNome("Rodrigo");

		Filme filme = new Filme();
		filme.setNome("Harry Potter");
		filme.setPrecoLocacao(5.0);
		filme.setEstoque(3);

		// Ação
		Locacao locacao = locacaoService.alugarFilme(usuario, filme);

		// Passamos a utilizar o ErrorCollector para retornar todas as falhas de uma vez
		// só
		// Nao bloqueando as proximas verificações se uma anterior falhar
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

	// Testes para tratamento de exceptions
	@Test(expected = FilmeSemEstoqueException.class) // Forma elegante - Teste espera uma exception
	public void testeLocacaFilmeSemEstoque() throws Exception {
		// Cenario
		LocacaoService locacaoService = new LocacaoService();
		Usuario usuario = new Usuario();
		usuario.setNome("Rodrigo");

		Filme filme = new Filme();
		filme.setNome("Harry Potter");
		filme.setPrecoLocacao(5.0);
		filme.setEstoque(0);

		// Ação
		Locacao locacao = locacaoService.alugarFilme(usuario, filme);
	}

	//Forma elegante - se ficar na duvida qual usar, utiliza a forma elegante
	@Test
	public void testeLocacaoUsuarioVazio() throws FilmeSemEstoqueException {
		// Cenario
		LocacaoService locacaoService = new LocacaoService();
		Filme f = new Filme("Rodrigo FIlm", 1, 4.0);

		// acao
		try {
			locacaoService.alugarFilme(null, f);
			fail();
		} catch (LocadoraException e) {
			assertThat(e.getMessage(), is("Usuário vazio"));
		}

		//Continua na forma robusta, essa mensagem vai ser exibida, diferente da forma robusta, abaixo
		System.out.println("Continua o codigo - forma elegante");
	}

	//Forma nova
	@Test
	public void testeLocacaoFilmeVazio() throws FilmeSemEstoqueException, LocadoraException {
		// Cenario
		LocacaoService locacaoService = new LocacaoService();
		Usuario usuario = new Usuario("Rodrigo");

		expectedException.expect(LocadoraException.class);
		expectedException.expectMessage("Filme vazio");
		locacaoService.alugarFilme(usuario, null);
		
		//Aqui nao continua
		System.out.println("Continua o codigo - Forma nova");

	}

}
