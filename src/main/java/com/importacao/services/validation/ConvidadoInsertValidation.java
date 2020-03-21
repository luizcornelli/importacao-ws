package com.importacao.services.validation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.importacao.domain.Convidado;
import com.importacao.dto.ConvidadoNewDTO;
import com.importacao.repositories.ConvidadoRepository;
import com.importacao.resources.exception.FieldMessage;
import com.importacao.services.validation.utils.BR;

public class ConvidadoInsertValidation implements ConstraintValidator<ConvidadoInsert, ConvidadoNewDTO> {

	@Autowired
	private ConvidadoRepository convidadoRepository;

	@Override
	public void initialize(ConvidadoInsert ann) {
	}

	@Override
	public boolean isValid(ConvidadoNewDTO convidadoDto, ConstraintValidatorContext context) {

		List<FieldMessage> list = new ArrayList<>();

		if (!BR.isValidCPF(convidadoDto.getCpf())) {
			list.add(new FieldMessage("cpf", "CPF Inválido"));
		}

		Convidado aux = convidadoRepository.findByEmail(convidadoDto.getEmail());
		if (aux != null) {
			list.add(new FieldMessage("email", "Email já existente!"));
		}

		for (FieldMessage e : list) {

			context.disableDefaultConstraintViolation();

			context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
					.addConstraintViolation();
		}
		return list.isEmpty();
	}
}
