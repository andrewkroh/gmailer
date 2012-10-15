package com.krohinc.gmailer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
 
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
 
/**
 * Gmail wrapper for sending messages from the command line.
 * 
 * @version 1.0
 * @author akroh
 */
public class Gmail {
    
    private static class Arguments
    {
        @Parameter(names={"--username", "-u"}, description="Gmail username", required=true)
        private String username;
        
        @Parameter(names={"--password", "-p"}, description="Gmail password", required=true)
        private String password;
        
        @Parameter(names={"--subject", "-s"}, description="Subject of message", required=true)
        private String subject;
        
        @Parameter(names={"--to", "-t"}, description="To addresses", variableArity = true)
        public List<String> to = new ArrayList<String>();

        @Parameter(names={"--message", "-m"}, description="Message text", required=false)
        private String message;
        
        @Parameter(names = { "--messageFile", "-mf" }, 
                   description = "File to be used as message text", 
                   required = false)
        private String messageFile;
    }
    
    private static String readFile(String path) throws IOException 
    {
        FileInputStream stream = new FileInputStream(new File(path));
        try
        {
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0,
                    fc.size());
            /* Instead of using default, pass in a decoder. */
            return Charset.defaultCharset().decode(bb).toString();
        }
        finally
        {
            stream.close();
        }
    }
    
    private static void printUsage(JCommander jcommander)
    {
        jcommander.setProgramName("Gmailer");
        jcommander.usage();
        System.exit(0);
    }
 
    public static void main(String[] args) throws MessagingException, IOException
    {
        final Arguments arguments = new Arguments();
        JCommander jcommander = new JCommander(arguments);
        
        try
        {
            jcommander.parse(args);
        } 
        catch (ParameterException e)
        {
            printUsage(jcommander);
        }
        
        if (arguments.message == null && arguments.messageFile == null)
        {
            printUsage(jcommander);
        }
        
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
 
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(arguments.username,
                                arguments.password);
                    }
                });
 
        List<Address> toAddresses = new ArrayList<Address>(arguments.to.size());
        for (String address : arguments.to)
        {
            toAddresses.add(new InternetAddress(address, true));
        }
        
        Message message = new MimeMessage(session);
        message.setRecipients(Message.RecipientType.TO, 
                                  toAddresses.toArray(new Address[]{}));
        message.setSubject(arguments.subject);
        
        if (arguments.messageFile != null)
        {
            message.setText(readFile(arguments.messageFile));
        }
        else
        {
            message.setText(arguments.message);
        }

        Transport.send(message);
 
        System.out.println("Message sent.");
    }
}

