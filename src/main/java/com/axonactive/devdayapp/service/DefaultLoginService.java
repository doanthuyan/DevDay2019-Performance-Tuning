package com.axonactive.devdayapp.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.axonactive.devdayapp.Constants;
import com.axonactive.devdayapp.dto.LoginUser;
import com.axonactive.devdayapp.dto.RegisterUser;
import com.axonactive.devdayapp.dto.UserDto;
import com.axonactive.devdayapp.exception.InvalidUsernamePasswordException;
import com.axonactive.devdayapp.exception.UserAlreadyExistedException;
import com.axonactive.devdayapp.exception.UserNotFoundException;

@Service
public class DefaultLoginService implements LoginService {
    private static final Logger log = LogManager.getLogger(DefaultLoginService.class);
    
	@Autowired
	private UserService userService;

	@Override
	public void register(RegisterUser registerUser) {
        long startTime = System.currentTimeMillis();
		if (userService.findByUsername(registerUser.getUsername()) != null)
		{
            log.error(Constants.ERROR_LOG_MSG, getClass().getName(),
                "registration", 
                String.format("User already existed: userName=%s", registerUser.getUsername()));
			throw new UserAlreadyExistedException("Error when creating new user. User already existed.");
		}
		Boolean save = userService.save(RegisterUser.toUserDTO(registerUser));
		if (!save)	{
            log.error(Constants.ERROR_LOG_MSG, getClass().getName(),
                "registration", 
                String.format("Cannot save user to database: userName=%s", registerUser.getUsername()));
			throw new InternalError("Error when creating new user. Cannot save user to database.");
		}
        log.info(Constants.INFO_LOG_MSG, getClass().getName(),
                "registration", 
                System.currentTimeMillis() - startTime,
                String.format("userName=%s, password=%s", registerUser.getUsername(), registerUser.getPassword()));
	}

	@Override
	public LoginUser login(String username, String password) {
		long startTime = System.currentTimeMillis();
		UserDto userDto = userService.findByUsername(username);
		if (userDto == null)
		{
            log.error(Constants.ERROR_LOG_MSG, getClass().getName(),
                "login", 
                String.format("User cannot be found: userName=%s", username));
			throw new UserNotFoundException("User cannot be found. Username: " + username);
		}
		if (!isValidPassword(password, userDto.getPassword())) {
            log.error(Constants.ERROR_LOG_MSG, getClass().getName(),
                "login", 
                String.format("User cannot be found: userName=%s", username));
			throw new InvalidUsernamePasswordException("User cannot be found. Username: " + username);
		}

		LoginUser loginUser = LoginUser.toLoginUser(userDto);
        log.info(Constants.INFO_LOG_MSG, getClass().getName(),
                "login", 
                System.currentTimeMillis() - startTime,
                String.format("userName=%s", username));
		return loginUser;
	}

	private boolean isValidPassword(String inputPassword, String dbPassword) {
		return DigestUtils.md5DigestAsHex(inputPassword.getBytes()).equals(dbPassword);
	}

}
