package cl.magal.asistencia.entities.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import cl.magal.asistencia.entities.enums.Bank;

@Converter(autoApply=true)
public class BankConverter implements AttributeConverter<Bank, Integer>{

	@Override
	public Integer convertToDatabaseColumn(Bank arg0) {
		return arg0.getCorrelative();
	}

	@Override
	public Bank convertToEntityAttribute(Integer arg0) {
		return Bank.getBank(arg0);
	}

}