package com.axonactive.devdayapp.rest;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.axonactive.devdayapp.dto.LoginUser;
import com.axonactive.devdayapp.logger.Log;
import com.axonactive.devdayapp.service.LoginService;

@RestController
@RequestMapping("/user")
public class LoginResource {
	
	private static @Log Logger log;
	
	@Autowired
	private LoginService loginService;

	@PostMapping("/registration")
	public void registration(@RequestBody LoginUser loginUser) {
		log.info("POST request to register user. Username: ", loginUser.getUsername());
		
		try {
			loginService.registration(loginUser);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot register user. Exception: " + e.getMessage());
		}
	}

	@GetMapping("/login")
	public LoginUser login(@RequestParam String username, @RequestParam String password) {
		log.info("POST request to login. Username: ", username);
		
		try {
			return loginService.login(username, password);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User failed to login. Exception: " + e.getMessage());
		}
	}
}