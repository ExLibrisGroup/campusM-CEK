package com.exlibrisgroup.campusm.cl.extensions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan({"com.gw.campusm","com.campusm","com.exlibrisgroup"})
public class ExtensionsApplication extends SpringBootServletInitializer{
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(ExtensionsApplication.class);
	}
	
	public static void main(String[] args) {
		SpringApplication.run(ExtensionsApplication.class, args);
	}
}
