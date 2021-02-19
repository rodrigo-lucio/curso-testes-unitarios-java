package br.ce.wcaquino.servicos;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoServiceTest {

	@Test
	public void teste() {
		//Cenario
	
		LocacaoService locacaoService = new LocacaoService();
		System.out.println("DSADAS");
		Usuario usuario = new Usuario();
		usuario.setNome("Rodrigo");
		
		Filme filme = new Filme();
		filme.setNome("Harry Potter");
		filme.setPrecoLocacao(5.0);
		
		//Ação
		Locacao service = locacaoService.alugarFilme(usuario, filme);
		
		//Verificação
		Assert.assertTrue(DataUtils.isMesmaData(service.getDataLocacao(), new Date()));
		Assert.assertTrue(DataUtils.isMesmaData(service.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)));
		Assert.assertTrue(service.getValor() == 5);
		
	}
}
