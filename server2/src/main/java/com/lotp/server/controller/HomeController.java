package com.lotp.server.controller;

import com.lotp.server.entity.LeaderboardItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * User: jitse
 * Date: 12/4/13
 * Time: 12:25 PM
 *
 * THIS IS THE LEADERBOARD
 */
@Controller
@RequestMapping("/")
public class HomeController extends BaseController {

    @Autowired
    TTController ttController;

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView home () {

        List<LeaderboardItem> command = ttController.getLeaderBoard();
        return new ModelAndView("home", "command", command);
    }

}