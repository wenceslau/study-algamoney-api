package com.exemplo.algamoney.api.exceptionhandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

@ControllerAdvice
public class AlgamoneyExceptionHandler extends ResponseEntityExceptionHandler {

	@Autowired
	private MessageSource messageSource;

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		// TODO Auto-generated method stub
		String messageUser = messageSource.getMessage("mensagem.invalida", null, LocaleContextHolder.getLocale());
		String messageDeveloper = ex.getCause() != null ? ex.getCause().toString() : ex.toString();
		List<Erro> erros = Arrays.asList(new Erro(messageUser, messageDeveloper));
		return handleExceptionInternal(ex, erros, headers, HttpStatus.BAD_REQUEST, request);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		// TODO Auto-generated method stub
		List<Erro> erros = criarListError(ex.getBindingResult());
		return handleExceptionInternal(ex, erros, headers, HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler({ EmptyResultDataAccessException.class })
	// @ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<Object> handleEmptyResultDataAccessException(EmptyResultDataAccessException ex,
			WebRequest request) {

		String messageUser = messageSource.getMessage("mensagem.recursoNaoEncontrado", null,
				LocaleContextHolder.getLocale());
		String messageDeveloper = ex.toString();
		List<Erro> erros = Arrays.asList(new Erro(messageUser, messageDeveloper));

		return handleExceptionInternal(ex, erros, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
	}
	
	@ExceptionHandler({ DataIntegrityViolationException.class })
	// @ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex,
			WebRequest request) {

		String messageUser = messageSource.getMessage("mensagem.operacaoNaoPermitida", null,
				LocaleContextHolder.getLocale());
		String messageDeveloper = ExceptionUtils.getRootCauseMessage(ex);
		List<Erro> erros = Arrays.asList(new Erro(messageUser, messageDeveloper));

		return handleExceptionInternal(ex, erros, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}

	protected ResponseEntity<Object> handleMySQLIntegrityConstraintViolationException(
			MySQLIntegrityConstraintViolationException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		// TODO Auto-generated method stub
		String messageUser = "Campo Obrigatorio";
		String messageDeveloper = ex.getCause().toString();
		List<Erro> erros = Arrays.asList(new Erro(messageUser, messageDeveloper));
		return handleExceptionInternal(ex, erros, headers, HttpStatus.BAD_REQUEST, request);
	}

	private List<Erro> criarListError(BindingResult bindingResult) {
		List<Erro> erros = new ArrayList<>();

		for (FieldError fiedlError : bindingResult.getFieldErrors()) {

			String messageUser = messageSource.getMessage(fiedlError, LocaleContextHolder.getLocale());
			String messageDeveloper = fiedlError.toString();

			erros.add(new Erro(messageUser, messageDeveloper));

		}

		return erros;
	}

	public static class Erro {

		private String messageUser;
		private String messageDeveloper;

		public Erro(String messageUser, String messageDeveloper) {
			super();
			this.messageUser = messageUser;
			this.messageDeveloper = messageDeveloper;
		}

		public String getMessageUser() {
			return messageUser;
		}

		public String getMessageDeveloper() {
			return messageDeveloper;
		}

	}

}
