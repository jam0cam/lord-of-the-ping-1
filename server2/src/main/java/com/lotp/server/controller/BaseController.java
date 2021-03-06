package com.lotp.server.controller;

import com.lotp.server.exception.EmailExistsException;
import com.lotp.server.exception.InvalidMatchException;
import com.lotp.server.exception.InvalidUserNameOrPasswordException;
import com.lotp.server.model.HttpResponse;
import com.lotp.server.security.MyUserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;

/**
 * User: jitse
 * Date: 12/4/13
 * Time: 2:19 PM
 */
public abstract class BaseController {

    @Autowired
    protected SimpleDateFormat dateFormatter;

    @Autowired
    protected MyUserContext myUserContext;

    @ExceptionHandler({ InvalidUserNameOrPasswordException.class })
    @ResponseBody
    public HttpResponse loginException(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        HttpResponse errorResponse = new HttpResponse();
        errorResponse.setMessage("Invalid Email or Password");
        errorResponse.setCode(HttpServletResponse.SC_UNAUTHORIZED);
        return errorResponse;
    }

    @ExceptionHandler({ EmailExistsException.class })
    @ResponseBody
    public HttpResponse emailExistsException(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_CONFLICT);
        HttpResponse errorResponse = new HttpResponse();
        errorResponse.setMessage("Email already taken");
        errorResponse.setCode(HttpServletResponse.SC_CONFLICT);
        return errorResponse;
    }

    @ExceptionHandler({ InvalidMatchException.class })
    @ResponseBody
    public HttpResponse invalidMatchException(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        HttpResponse errorResponse = new HttpResponse();
        errorResponse.setMessage("Match information is not valid");
        errorResponse.setCode(HttpServletResponse.SC_BAD_REQUEST);
        return errorResponse;
    }
}
