package com.lotp.server.controller;

import com.lotp.server.entity.Match;
import com.lotp.server.model.MatchCommand;
import com.lotp.server.model.MatchSuccessCommand;
import com.lotp.server.entity.Player;
import com.lotp.server.postageapp.MailSender;
import com.lotp.server.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * User: jitse
 * Date: 12/4/13
 * Time: 3:26 PM
 */
@Controller
@RequestMapping("/match")
public class MatchController extends BaseController {

    @Autowired
    MailSender mailSender;

    @Autowired
    TTController ttController;

    @Autowired
    PlayerRepository playerRepository;

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody
    ModelAndView main () {
        MatchCommand command = new MatchCommand();

        Iterable<Player> players = playerRepository.findAll();


        List<String> finalList = new ArrayList<String>();

        String myEmail = myUserContext.getCurrentUser().getEmail();

        for (Player p : players) {
            String email = p.getEmail();
            String name = p.getName();

            if (myEmail.equalsIgnoreCase(email)) {
                continue;
            }

            String combo  = "\"" + name + " (" + email + ")\"";
            finalList.add(combo);
        }

        command.setEmailList(finalList);
        return new ModelAndView("match", "command", command);
    }

    @RequestMapping(value="/save", method = RequestMethod.POST)
    public @ResponseBody
    ModelAndView submit (@ModelAttribute("command")MatchCommand command, BindingResult result) {

        if (!StringUtils.hasText(command.getEmail())) {
            result.rejectValue("email", "opponent.required");
            return new ModelAndView("match", "command", command);
        }

        if (!command.getEmail().contains("(") || !command.getEmail().contains(")")) {
            result.rejectValue("email", "select.from.email.list");
            return new ModelAndView("match", "command", command);
        }

        String email = command.getEmail().substring(command.getEmail().indexOf("(")+1, command.getEmail().indexOf(")"));

        Player opponent = playerRepository.findByEmail(email);
        if (opponent == null) {
            result.rejectValue("email", "invalid.player");
            return new ModelAndView("match", "command", command);
        }
        int gamesLost = Integer.parseInt(command.getGamesLost());
        int gamesWon = Integer.parseInt(command.getGamesWon());
        int totalGames =  gamesLost + gamesWon;

        if (totalGames > 5) {
            result.rejectValue("email", "too.many.games.played");
            return new ModelAndView("match", "command", command);
        } else if (totalGames < 3) {
            result.rejectValue("email", "too.few.games.played");
            return new ModelAndView("match", "command", command);
        } else if (gamesLost < 3 && gamesWon < 3) {
            result.rejectValue("email", "no.winner");
            return new ModelAndView("match", "command", command);
        }

        Match match = new Match();
        match.setPlayerOne(myUserContext.getCurrentUser());
        match.setP1Score(Integer.parseInt(command.getGamesWon()));
        match.setPlayerTwo(opponent);
        match.setP2Score(Integer.parseInt(command.getGamesLost()));


        //hack to subtract 8 hours due to time zone differences with AWS
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, -8);
        match.setDate(cal.getTime());
        ttController.saveMatch(match);


        MatchSuccessCommand rval = new MatchSuccessCommand();
        rval.setName(opponent.getName());
        return new ModelAndView("success", "command", rval);
    }
}
