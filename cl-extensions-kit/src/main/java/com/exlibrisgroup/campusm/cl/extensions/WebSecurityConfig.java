package com.exlibrisgroup.campusm.cl.extensions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.campusm.cl.auth.TokenValidationService;
import com.campusm.cl.extensions.auth.CMAuthTokenAuthenticationFilter;
import com.campusm.cl.extensions.auth.LdapAuthenticationFilter;
import com.campusm.cl.extensions.auth.LdapValidationService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	@Qualifier("ExtTokenValidationService")
	private TokenValidationService tokenValidationService;
	
	@Autowired
	private LdapValidationService ldapValidationService;

	@Override
	protected void configure(HttpSecurity security) throws Exception {
		security.httpBasic().disable();
		security.csrf().disable();
		security.addFilter(new CMAuthTokenAuthenticationFilter(authenticationManager(), tokenValidationService));
		security.addFilter(new LdapAuthenticationFilter(authenticationManager(), ldapValidationService));

	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService);
	}

}
