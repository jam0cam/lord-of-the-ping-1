package com.lotp.server.controller;

import com.lotp.server.command.MatchConfirmation;
import com.lotp.server.entity.PendingMatch;
import com.lotp.server.model.InboxCommand;
import com.lotp.server.entity.Match;
import com.lotp.server.repository.PendingMatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
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

    @Autowired
    PendingMatchRepository pendingMatchRepository;

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView inbox () {
        InboxCommand command = new InboxCommand();

        //all the pending matches have player2 as the currently logged in user
        long userId = myUserContext.getCurrentUser().getId();
        Iterable<PendingMatch> pendingMatches = pendingMatchRepository.findAllByPlayerTwoId(userId);

        List<Match> matches = new ArrayList<>();
        for (Match m: pendingMatches){
            if (m.getP2Score() > m.getP1Score()){
                m.setStatus("W");
            } else {
                m.setStatus("L");
            }

            matches.add(m);
        }

        command.setPendingMatches(matches);

        return new ModelAndView("inbox", "command", command);
    }


    @RequestMapping(value = "/declineMatch/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView declineMatch (@PathVariable("id") long id){
        MatchConfirmation matchConfirmation = new MatchConfirmation();
        matchConfirmation.setConfirmed(false);
        matchConfirmation.setPendingId(id);

        ttController.confirmMatch(matchConfirmation);

        return new ModelAndView("redirect:/inbox");
    }

    @RequestMapping(value = "/confirmMatch/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView confirmMatch (@PathVariable("id") long id){
        MatchConfirmation matchConfirmation = new MatchConfirmation();
        matchConfirmation.setConfirmed(true);
        matchConfirmation.setPendingId(id);

        ttController.confirmMatch(matchConfirmation);

        return new ModelAndView("redirect:/inbox");
    }
}
