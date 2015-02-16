package com.lotp.controller;

import com.lotp.dao.Dao;
import com.lotp.model.Person;
import com.lotp.postageapp.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 */
@Controller
@RequestMapping("/Sample")
public class SampleController {

    @Autowired
    Dao dao;

    @Autowired
    MailSender mailSender;

    @RequestMapping(value = "/id/{id}", method = RequestMethod.GET)
    public @ResponseBody Person getById(@PathVariable("id") String id) {
        return dao.getByResultId(id);
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public @ResponseBody String home () {
        mailSender.sendMail("jitse@zappos.com", "test email", "test body");

        return "email sent";
    }

}
