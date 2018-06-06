package com.exlibrisgroup.campusm.cl.extensions.model.example;

import java.util.List;

public class Grades {

	private List<Grade> grades;	
	private String errorMessage;
	
	public List<Grade> getGrades() {
		return grades;
	}
	public void setGrades(List<Grade> grades) {
		this.grades = grades;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
