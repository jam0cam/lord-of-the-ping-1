package com.lotp.server.controller;

import com.lotp.server.command.LoginCommand;
import com.lotp.server.entity.Player;
import com.lotp.server.security.MyUserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * User: jitse
 * Date: 6/16/13
 * Time: 7:06 PM
 */
@Controller
@RequestMapping("/signin")
public class LoginController {

    @Autowired
    MyUserContext myUserContext;

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView login () {
        Player command = myUserContext.getCurrentUser();
        if (command != null) {
            //user is already logged in, redirect to accounts page
            return new ModelAndView("redirect:/");
        }

        return new ModelAndView("login", "command", new LoginCommand());
    }

    @RequestMapping(value = "failedLogin", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView failedLogin () {
        LoginCommand command = new LoginCommand();
        command.setError("Invalid username or password");
        return new ModelAndView("login", "command", command);
    }

    @RequestMapping(value = "logoutSuccess", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView logoutSuccess () {
//        LoginCommand command = new LoginCommand();
//        command.setMessage("You have been logged out.");
        return new ModelAndView("redirect:/");
    }

}
