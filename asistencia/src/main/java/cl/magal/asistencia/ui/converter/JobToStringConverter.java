package cl.magal.asistencia.ui.converter;

import java.util.Locale;

import cl.magal.asistencia.entities.enums.Job;

import com.vaadin.data.util.converter.Converter;

public class JobToStringConverter implements Converter<String, Job> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5820634506686817080L;

	@Override
	public Job convertToModel(String value,
			Class<? extends Job> targetType, Locale locale)
					throws com.vaadin.data.util.converter.Converter.ConversionException {
		return Job.getJob(value);
	}

	@Override
	public String convertToPresentation(Job value,
			Class<? extends String> targetType, Locale locale)
					throws com.vaadin.data.util.converter.Converter.ConversionException {
		return value.toString();
	}

	@Override
	public Class<Job> getModelType() {
		return Job.class;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}

}
