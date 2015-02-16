package com.lotp.controller;

import com.lotp.model.PasswordResetCommand;
import com.lotp.model.Player;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * User: jitse
 * Date: 1/19/14
 * Time: 11:27 AM
 */
@Controller
@RequestMapping("/password/reset")
public class PasswordResetController extends BaseController{

    @RequestMapping(value = "/{hash}", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView main (@PathVariable("hash") String hash) {

        //see if this is a valid hash
        Player p = dao.getPlayerFromHash(hash);
        if (p == null) {
            return new ModelAndView("redirect:/");
        }

        PasswordResetCommand command = new PasswordResetCommand();
        command.setPasswordHash(hash);
        command.setEmail(p.getEmail());

        return new ModelAndView("passwordReset", "command", command);
    }

    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody
    ModelAndView submit (@ModelAttribute("command")PasswordResetCommand command, BindingResult result) {
        if (!StringUtils.hasText(command.getNewPassword())) {
            result.rejectValue("password", "password.required");
            return new ModelAndView("passwordReset", "command", command);
        }

        dao.updatePlayerPassword(command.getNewPassword(), command.getPasswordHash());
        dao.deletePasswordReset(command.getPasswordHash());
        return new ModelAndView("redirect:/signin");
    }
}
