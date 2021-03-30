package br.ce.wcaquino.matchers;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

import br.ce.wcaquino.utils.DataUtils;

public class DiaSemanaMatcher extends TypeSafeMatcher<Date> { //Date � o o tipo do parametro que � passado la no assert

	private Integer diaSemana;
		
	public DiaSemanaMatcher(Integer diaSemana) {
		this.diaSemana = diaSemana;
	}
	
	public void describeTo(Description description) {
		Calendar data = Calendar.getInstance();
		data.set(Calendar.DAY_OF_WEEK, diaSemana);
		String dataExtenso = data.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, new Locale("pt", "BR"));
		description.appendText(dataExtenso);
	}

	@Override
	protected boolean matchesSafely(Date data) {
		return DataUtils.verificarDiaSemana(data, diaSemana);
	}

}