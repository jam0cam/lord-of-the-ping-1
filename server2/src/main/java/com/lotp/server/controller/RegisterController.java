package com.lotp.server.controller;

import com.lotp.server.entity.Player;
import com.lotp.server.repository.PlayerRepository;
import com.lotp.server.security.MyUserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * User: jitse
 * Date: 12/4/13
 * Time: 12:57 PM
 */

@Controller
@RequestMapping("/register")
public class RegisterController
{
    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    MyUserContext myUserContext;

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView main () {
        return new ModelAndView("register", "command", new Player());
    }

    @RequestMapping(method= RequestMethod.POST)
    public ModelAndView onSubmit(@ModelAttribute("command")Player command, BindingResult result) {

        if (!StringUtils.hasText(command.getName())) {
            result.rejectValue("email", "name.required");
            return new ModelAndView("register", "command", command);
        } else if (!StringUtils.hasText(command.getEmail())) {
            result.rejectValue("email", "email.required");
            return new ModelAndView("register", "command", command);
        } else if (!StringUtils.hasText(command.getPassword())) {
            result.rejectValue("email", "password.required");
            return new ModelAndView("register", "command", command);
        }

        if (!result.hasErrors()){
            Util.trimRegister(command);

            playerRepository.save(command);
            myUserContext.authenticateAndSetUser(command);
            return new ModelAndView("redirect:/");
        } else {
            return new ModelAndView("register", "command", command);
        }
    }

}

