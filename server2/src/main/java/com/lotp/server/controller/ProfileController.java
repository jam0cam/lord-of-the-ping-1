package com.lotp.server.controller;

import com.lotp.server.model.ProfileCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private Logger logger = LoggerFactory.getLogger(ProfileController.class);

    @Autowired
    TTController ttController;

    @RequestMapping(value = "/myProfile", method = RequestMethod.GET)
    public ModelAndView main () {
        Long playerId = myUserContext.getCurrentUser().getId();
        logger.debug("Profile load for player id: " + playerId);

        ProfileCommand command = ttController.getProfile(playerId);
        command.setOwnProfile(true);
        return new ModelAndView("profile", "command", command);
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ModelAndView profileById (@PathVariable("id") long id) {
        ProfileCommand command = ttController.getProfile(id);
        command.setOwnProfile(false);
        return new ModelAndView("profile", "command", command);
    }
}
