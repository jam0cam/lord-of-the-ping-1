package com.lotp.server.postageapp;

import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: jitse
 * Date: 7/13/13
 * Time: 1:23 PM
 */
public class MailSender {

    private String from;
    private String apiKey;


    public Response sendMail(String recipient, String subject, String body) {
        Header header = new Header();
        header.setFrom(from);
        header.setSubject(subject);

        List<String> recipients = new ArrayList<String>();
        recipients.add(recipient);

        Argument argument = new Argument();
        argument.setHeader(header);
        argument.setRecipients(recipients);
        argument.setContent(body);

        Message message = new Message();
        message.setArgument(argument);
        message.setApiKey(apiKey);

        ObjectMapper om = new ObjectMapper();
        om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        try {
            System.out.println(om.writeValueAsString(message));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return sendMail(message);
    }

    private  Response sendMail(Message message) {
        RestTemplate template = new RestTemplate();
        Response rval = null;
        try {
            rval  =  template.postForObject("https://api.postageapp.com/v.1.0/send_message.json", message, Response.class);
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }

        return rval;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
