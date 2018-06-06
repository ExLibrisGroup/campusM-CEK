package com.exlibrisgroup.campusm.cl.extensions.controller.examples;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.campusm.cl.exception.NotAuthorizedException;
import com.exlibrisgroup.campusm.cl.extensions.model.error.ExampleErrorDetails;


@ControllerAdvice
@RestController
public class ExampleResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		ExampleErrorDetails errorDetails = new ExampleErrorDetails(new Date(), "Validation Failed",ex.getBindingResult().getFieldError().getDefaultMessage());
		return new ResponseEntity<Object>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(NotAuthorizedException.class)
	public final ResponseEntity<Object> handleNotAuthorizedException(NotAuthorizedException ex, WebRequest request) {
		ExampleErrorDetails errorDetails = new ExampleErrorDetails(new Date(), ex.getMessage(),request.getDescription(false));
		return new ResponseEntity<Object>(errorDetails, HttpStatus.UNAUTHORIZED);	
	}
	
	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, 
			  HttpStatus status, WebRequest request) {
		Iterator<String> iter = request.getParameterNames();
		
		
		String error = ex.getParameterName() + " parameter is missing of type " + ex.getParameterType();			     
		return new ResponseEntity<Object>(error, HttpStatus.BAD_REQUEST);
	}
}
