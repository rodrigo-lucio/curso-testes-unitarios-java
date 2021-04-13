package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.adicionarDias;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.ce.wcaquino.dao.LocacaoDao;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exception.FilmeSemEstoqueException;
import br.ce.wcaquino.exception.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoService {

	private LocacaoDao dao;
	private SPCService spcService;
	private EmailService emailService;

	public Locacao alugarFilme(Usuario usuario, List<Filme> filmes) throws FilmeSemEstoqueException, LocadoraException {

		if (usuario == null) {
			throw new LocadoraException("Usuário vazio");
		}

		Locacao locacao = new Locacao();

		if (filmes == null || filmes.isEmpty()) {
			throw new LocadoraException("Sem filmes");
		}

		double valorLocacao = 0;

		for (int i = 0; i < filmes.size(); i++) {
			if (filmes.get(i).getEstoque() == 0) {
				throw new FilmeSemEstoqueException();
			}

			Double precoLocacaoFilme = filmes.get(i).getPrecoLocacao();

			switch (i) {
			case 2:
				precoLocacaoFilme = (precoLocacaoFilme * 0.75);
				break;
			case 3:
				precoLocacaoFilme = (precoLocacaoFilme * 0.5);
				break;
			case 4:
				precoLocacaoFilme = (precoLocacaoFilme * 0.25);
				break;
			case 5:
				precoLocacaoFilme = 0d;
				break;
			}
			valorLocacao += precoLocacaoFilme;
		}
		boolean negativado;
		try {
			negativado = spcService.possuiNegativacao(usuario);
		} catch (Exception e) {
			throw new LocadoraException("Problemas com o SPC, tente novamente");
		}

		if (negativado) {
			throw new LocadoraException("Usuario negativado no SPC");
		}

		locacao.setFilmes(filmes);
		locacao.setValor(valorLocacao);
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(new Date());

		// Entrega no dia seguinte
		Date dataEntrega = new Date();
		dataEntrega = adicionarDias(dataEntrega, 1);

		// Nao aceita devolver no domingo
		if (DataUtils.verificarDiaSemana(dataEntrega, Calendar.SUNDAY)) {
			dataEntrega = adicionarDias(dataEntrega, 1);
		}

		locacao.setDataRetorno(dataEntrega);

		// Salvando a locacao...
		dao.salvar(locacao);

		return locacao;
	}

	public void notificarAtrasos() {
		List<Locacao> locacoes = dao.obterLocacoesPendentes();
		for (Locacao locacao : locacoes) {
			if (locacao.getDataRetorno().before(new Date())) {
				emailService.notificarAtraso(locacao.getUsuario());
			}
		}
	}

}