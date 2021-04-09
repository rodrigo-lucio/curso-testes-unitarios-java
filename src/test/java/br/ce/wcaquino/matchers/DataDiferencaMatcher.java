package br.ce.wcaquino.matchers;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

import br.ce.wcaquino.utils.DataUtils;

public class DataDiferencaMatcher extends TypeSafeMatcher<Date> {

	private Integer diferencaDias;

	public DataDiferencaMatcher(Integer diferencaDias) {
		this.diferencaDias = diferencaDias;
	}

	public void describeTo(Description description) {
		Calendar data = Calendar.getInstance();
		data.set(Calendar.DAY_OF_WEEK, diferencaDias);
		String dataExtenso = data.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, new Locale("pt", "BR"));
		description.appendText(dataExtenso);
	}

	@Override
	protected boolean matchesSafely(Date data) {
		Date dataDiferenca = DataUtils.obterDataComDiferencaDias(diferencaDias);
		return DataUtils.isMesmaData(data, dataDiferenca);
	}

}
