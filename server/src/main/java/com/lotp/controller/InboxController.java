package com.lotp.controller;

import com.lotp.model.ConfirmMatch;
import com.lotp.model.InboxCommand;
import com.lotp.model.Match;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * User: jitse
 * Date: 12/6/13
 * Time: 9:40 AM
 */
@Controller
@RequestMapping("/inbox")
public class InboxController extends BaseController{

    @Autowired
    TTController ttController;

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView inbox () {
        InboxCommand command = new InboxCommand();

        //all the pending matches have player2 as the currently logged in user
        List<Match> pendingMatches = dao.getPendingMatchesByPlayer(myUserContext.getCurrentUser().getId());

        for (Match m: pendingMatches){
            if (m.getP2Score() > m.getP1Score()){
                m.setStatus("W");
            } else {
                m.setStatus("L");
            }
        }

        command.setPendingMatches(pendingMatches);

        return new ModelAndView("inbox", "command", command);
    }


    @RequestMapping(value = "/declineMatch/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView declineMatch (@PathVariable("id") String id){
        ConfirmMatch confirmMatch = new ConfirmMatch();
        confirmMatch.setConfirmed(false);
        confirmMatch.setPendingId(id);

        ttController.confirmMatch(confirmMatch);

        return new ModelAndView("redirect:/inbox");
    }

    @RequestMapping(value = "/confirmMatch/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView confirmMatch (@PathVariable("id") String id){
        ConfirmMatch confirmMatch = new ConfirmMatch();
        confirmMatch.setConfirmed(true);
        confirmMatch.setPendingId(id);

        ttController.confirmMatch(confirmMatch);

        return new ModelAndView("redirect:/inbox");
    }
}
