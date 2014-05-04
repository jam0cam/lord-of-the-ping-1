package com.lotp.server.controller;

import com.lotp.server.model.PasswordResetCommand;
import com.lotp.server.entity.Player;
import com.lotp.server.repository.PasswordResetRepository;
import com.lotp.server.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.security.NoSuchAlgorithmException;

/**
 * User: jitse
 * Date: 1/19/14
 * Time: 11:27 AM
 */
@Controller
@RequestMapping("/password/reset")
public class PasswordResetController extends BaseController{

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    PasswordResetRepository passwordResetRepository;

    @RequestMapping(value = "/{hash}", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView main (@PathVariable("hash") String hash) {

        //see if this is a valid hash
        Player p = playerRepository.findByPasswordResetResetHash(hash);
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

        Player player = playerRepository.findByPasswordResetResetHash(command.getPasswordHash());

        if( null != player ){
            try {
                player.setPassword(Util.md5(command.getNewPassword()));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            passwordResetRepository.delete(player.getPasswordReset().getId());

            player.setPasswordReset(null);

            playerRepository.save(player);
        }

        return new ModelAndView("redirect:/signin");
    }
}
