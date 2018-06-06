package com.exlibrisgroup.campusm.cl.extensions.controller.examples;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.campusm.cl.extensions.auth.User;
import com.campusm.cl.service.configuration.ConfigurationManager;
import com.exlibrisgroup.campusm.cl.extensions.model.example.Grade;
import com.exlibrisgroup.campusm.cl.extensions.model.example.Grades;
import com.exlibrisgroup.campusm.cl.extensions.model.example.TestEmployee;
import com.gw.campusm.db.XsdBeanProcessor;

@RestController 
@EnableAutoConfiguration
public class ExampleController {

	private final static Logger logger = Logger.getLogger(ExampleController.class);
	
	@Autowired
	private ConfigurationManager configurationManager;			
	
	@PreAuthorize("isAuthenticated()") 	
	@RequestMapping(value = "getGrades", method = RequestMethod.GET) 
	public ResponseEntity<Grades> getGrades(@RequestParam String username, @RequestParam String password) throws Exception {	
		logger.info("start get gardes");
	  
		String csvFile = getCSVFile(username);
		
		Grades grades = new Grades();
		
		if (!checkIfFileExsits(csvFile)) {
			
			grades.setErrorMessage("File not found");
			return new ResponseEntity<Grades>(grades, HttpStatus.OK);
		}
		FillGrades(grades, csvFile);
		       		
		return new ResponseEntity<Grades>(grades, HttpStatus.OK);
	}
	
	@PreAuthorize("isAuthenticated()") 	
	@RequestMapping(value = "getCMAtuhEmployee", method = RequestMethod.GET)
	public ResponseEntity<Grades> getCMAtuhEmployee(@RequestParam String username, @RequestParam String password) throws Exception {	
		logger.info("start getCMAtuhEmployee");
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Object pricipal = auth.getPrincipal();

		if (pricipal instanceof User) {
		        logger.info("Token info: " + ((User) pricipal).getUserInfo().toString());
		}
		
		String csvFile = getCSVFile(username);
		
		Grades grades = new Grades();
		
		if (!checkIfFileExsits(csvFile)) {
			
			grades.setErrorMessage("File not found");
			return new ResponseEntity<Grades>(grades, HttpStatus.OK);
		}
		FillGrades(grades, csvFile);
		       		
		return new ResponseEntity<Grades>(grades, HttpStatus.OK);
	}
	
	@PreAuthorize("isAuthenticated()") 	
	@RequestMapping(value = "postGrades", method = RequestMethod.POST) 
	public ResponseEntity<Grades> postGrades(@RequestParam String username, @RequestParam String password,@RequestBody String body) throws Exception {	
		logger.info("Start post gardes");
		
		body = URLDecoder.decode(body,"UTF-8");
						
		Grades grades = new Grades();
		
		JSONObject jSONObject = null;	
		try {
			JSONParser parser = new JSONParser();
			jSONObject= (JSONObject) parser.parse(body);
			
		} catch (Exception e) {
			String message = "Can't parse body:  "+ body;
			logger.error(message,e);
			grades.setErrorMessage(message);
			return new ResponseEntity<Grades>(grades, HttpStatus.OK);
		}

		String csvFile = getCSVFile((String)jSONObject.get("fileName"));
		
		if (!checkIfFileExsits(csvFile)) {
			
			grades.setErrorMessage("File not found");
			return new ResponseEntity<Grades>(grades, HttpStatus.OK);
		}
		FillGrades(grades, csvFile);
		       		
		return new ResponseEntity<Grades>(grades, HttpStatus.OK);
	}
	
	@RequestMapping(value = "postGradesBody", method = RequestMethod.POST) 
	public ResponseEntity<Grades> postGradesBody(@RequestBody String body) throws Exception {			
		
		logger.info("Start postGradesBody: " + body);
		
		body = URLDecoder.decode(body,"UTF-8");
				
		Grades grades = new Grades();
				
		JSONObject jSONObject = null;	
		try {
			JSONParser parser = new JSONParser();
			jSONObject= (JSONObject) parser.parse(body);
			
		} catch (Exception e) {
			String message = "Can't parse body:  "+ body;
			logger.error(message,e);
			grades.setErrorMessage(message);
			return new ResponseEntity<Grades>(grades, HttpStatus.OK);
		}

		String csvFile = getCSVFile((String)jSONObject.get("fileName"));
		
		if (!checkIfFileExsits(csvFile)) {
			
			grades.setErrorMessage("File not found");
			return new ResponseEntity<Grades>(grades, HttpStatus.OK);
		}
		FillGrades(grades, csvFile);
		       		
		return new ResponseEntity<Grades>(grades, HttpStatus.OK);
	}
	
	@PreAuthorize("isAuthenticated()") 	
	@RequestMapping(value = "getEmployee", method = RequestMethod.GET)
	public ResponseEntity<TestEmployee> getEmployeeById(@RequestParam String username, @RequestParam String password,@RequestParam String id) throws Exception {	
		
		TestEmployee employee = new TestEmployee();
		DataSource datasource = null;
		QueryRunner run = null;
		try {
			datasource= configurationManager.getDatasourceByName("test");
			if (datasource!=null) {
				run = new QueryRunner(datasource);
				ResultSetHandler<List<TestEmployee>> blh = new BeanListHandler<TestEmployee>(TestEmployee.class, new XsdBeanProcessor());
				List<TestEmployee> employess = (List<TestEmployee>)run.query("select first_name firstName,last_name lastName,office_name officeName from employee_test where id=?", blh,id);
				if (CollectionUtils.isNotEmpty(employess)) {
					employee = employess.get(0);
				}
			}
		}catch (Exception e) { 
			logger.error("Failed to get employee: " + id,e);
			employee.setErrorMessage("Failed to get employee");
		}
		finally {
			if (datasource != null && run!=null) {
				DbUtils.closeQuietly(run.getDataSource().getConnection());
			}
		}		
		
		return new ResponseEntity<TestEmployee>(employee, HttpStatus.OK);		
	}
	
	private String getCSVFile(String username) {
		JSONObject configuration = configurationManager.getConfiguration("grades");
		String path = (String) configuration.get("path");
		return path+File.separator+username + ".csv";		
	}
	
	private void FillGrades (Grades grades,String csvFile) throws Exception {
		String line = "";
        List <Grade> listOfGrades = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
			while ((line = br.readLine()) != null) {
	          
	            String[] gradeLine = line.split("-");
	            if (ArrayUtils.isNotEmpty(gradeLine) && gradeLine.length>1) {
	            	Grade grade = new Grade();
	            	grade.setClassName(gradeLine[0]);
	            	grade.setGrade(gradeLine[1]);
	            	listOfGrades.add(grade);
	            }           
	        }
			grades.setGrades(listOfGrades);
		}
	}
	
	private boolean checkIfFileExsits (String filename) {
			
    	File f = new File(filename);
    	if(f.exists() && !f.isDirectory()) { 
    	    return true;
    	}
    	return false;
	}
		
}
