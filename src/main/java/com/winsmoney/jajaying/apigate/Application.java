package com.winsmoney.jajaying.apigate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static com.winsmoney.jajaying.apigate.JwtUtil.HEADER_STRING;
import static com.winsmoney.jajaying.apigate.JwtUtil.TOKEN_PREFIX;
import static com.winsmoney.jajaying.apigate.JwtUtil.USER_ID;

@SpringBootApplication
@RestController
public class Application {

	@GetMapping("/api/protected")
	public @ResponseBody Object hellWorld(@RequestHeader(value =USER_ID) String userId) {
		return "Hello World! This is a protected api, your use id is "+userId;
	}

	@PostMapping("/login")
	public void login(HttpServletResponse response,
					  @RequestBody final AccountCredentials credentials) throws IOException {
		//here we just have one hardcoded username=admin and password=admin
		//TODO add your own user validation code here
		if(validCredentials(credentials)) {
			String jwt = JwtUtil.generateToken(credentials.username);
			response.addHeader(HEADER_STRING, TOKEN_PREFIX + " " + jwt);
		}else
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Wrong credentials");
	}


	@Bean
	public FilterRegistrationBean jwtFilter() {
		final FilterRegistrationBean registrationBean = new FilterRegistrationBean();
		JwtAuthenticationFilter filter = new JwtAuthenticationFilter(
				"/api/**");
		registrationBean.setFilter(filter);
		return registrationBean;
	}

	private boolean validCredentials(AccountCredentials credentials) {
		if("admin".equals(credentials.username)
				&& "admin".equals(credentials.password)){
			return true;//psudo company id;
		}
		return false;
	}


	public static class AccountCredentials {
		public String username;
		public String password;
	}
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
