package com.expertworks.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.expertworks.service.UserDetailsServiceImpl;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

//	@Autowired
//	private DataSource dataSource;

	/*
	 * { "exp": 1603830869, "user_name": "prakash", "authorities": [ "ADMIN" ],
	 * "jti": "882885f0-f494-4138-9d66-d1adc286c580", "client_id": "client",
	 * "scope": [ "write" ] }
	 */

	@Bean
	public PasswordEncoder passwordEncoder() {
		return NoOpPasswordEncoder.getInstance();
	}

//	  @Bean public PasswordEncoder passwordEncoder() { return new
//	  BCryptPasswordEncoder(); }
//	 

	@Autowired
	private UserDetailsService userDetailsService;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// auth.userDetailsService(userDetailsService).passwordEncoder(new
		// BCryptPasswordEncoder());
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}

//    @Override
//    protected void configure(AuthenticationManagerBuilder builder) throws Exception {
//        builder.jdbcAuthentication()
//               .passwordEncoder(new BCryptPasswordEncoder())
//               .dataSource(dataSource).usersByUsernameQuery("select email,password,enabled, name "
//            	        + "from users "
//            	        + "where email = ?")
//            	      .authoritiesByUsernameQuery("select email,authority "
//            	        + "from authorities "
//            	        + "where email = ?");;
//    }
//    

//    CREATE TABLE bael_users (
//    		  name VARCHAR(50) NOT NULL,
//    		  email VARCHAR(50) NOT NULL,
//    		  password VARCHAR(100) NOT NULL,
//    		  enabled TINYINT NOT NULL DEFAULT 1,
//    		  PRIMARY KEY (email)
//    		);
//    		  
//    		CREATE TABLE bael_authorities (
//    		  email VARCHAR(50) NOT NULL,
//    		  authority VARCHAR(50) NOT NULL,
//    		  FOREIGN KEY (email) REFERENCES bael_users(email)
//    		);
//    	CREATE UNIQUE INDEX ix_auth_email on bael_authorities (email,authority);

//    create table users(
//    		username varchar(50) not null primary key,
//    		password varchar(100) not null,
//    		enabled boolean not null
//    	);
//    	create table authorities (
//    		username varchar(50) not null,
//    		authority varchar(50) not null,
//    		constraint fk_authorities_users foreign key(username) references users(username)
//    	);
//    	create unique index ix_auth_username on authorities (username,authority);

	/*
	 * @Bean public UserDetailsService userDetailsService() {
	 * InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
	 * 
	 * UserDetails u = User.withUsername("john") .password("12345")
	 * .authorities("ADMIN") .build();
	 * 
	 * manager.createUser(u);
	 * 
	 * return manager; }
	 */

	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	@CrossOrigin
	protected void configure(HttpSecurity http) throws Exception {
		http.cors().and().csrf().disable().anonymous().disable().authorizeRequests()
				.antMatchers(HttpMethod.OPTIONS, "**").permitAll();
	}

}
