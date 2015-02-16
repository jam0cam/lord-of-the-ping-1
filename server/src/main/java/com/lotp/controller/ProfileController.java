package com.lotp.controller;

import com.lotp.model.ProfileCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * User: jitse
 * Date: 12/4/13
 * Time: 1:59 PM
 */
@Controller
@RequestMapping("/profile")
public class ProfileController extends BaseController{


    @Autowired
    TTController ttController;

    @RequestMapping(value = "/myProfile", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView main () {
        ProfileCommand command = ttController.getProfile(myUserContext.getCurrentUser().getId());
        command.setOwnProfile(true);
        return new ModelAndView("profile", "command", command);
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView profileById (@PathVariable("id") String id) {
        ProfileCommand command = ttController.getProfile(id);
        command.setOwnProfile(false);
        return new ModelAndView("profile", "command", command);
    }
}
