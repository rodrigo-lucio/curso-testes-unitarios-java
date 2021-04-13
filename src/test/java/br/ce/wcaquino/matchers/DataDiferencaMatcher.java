package br.ce.wcaquino.matchers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import br.ce.wcaquino.utils.DataUtils;

public class DataDiferencaMatcher extends TypeSafeMatcher<Date> {

	private Integer diferencaDias;

	public DataDiferencaMatcher(Integer diferencaDias) {
		this.diferencaDias = diferencaDias;
	}

	public void describeTo(Description description) {
		Date dataEsperada = DataUtils.obterDataComDiferencaDias(diferencaDias);
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY");
		description.appendText(dateFormat.format(dataEsperada));
		System.out.println(description);
	}

	@Override
	protected boolean matchesSafely(Date data) {
		Date dataDiferenca = DataUtils.obterDataComDiferencaDias(diferencaDias);
		return DataUtils.isMesmaData(data, dataDiferenca);
	}

}
