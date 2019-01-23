package fr.picedr.bot.utils;


import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MsgUtils {

    public static int MAX_MSG_SIZE=2000;

    public static String FT_CSS="css";
    public static String FT_FIX="fix";


    /**
     * Send message to a user
     * @param user : User object to send the message to
     * @param message : message content
     * @return the message sent to the user
     */
    public static Message tell(User user, String message){
        return tell(user,message,0);
    }

    /**
     * Send message to a user
     * @param user : User object to send the message to
     * @param message : message content
     * @param typing : delay of the answer
     * @return the message sent to the user
     */
    public static Message tell(User user, String message,int typing){
        PrivateChannel priv =  user.openPrivateChannel().complete();
        priv.sendTyping().complete();
        if (typing>0) {
            try{
                Thread.sleep(typing * 1000);
            }catch (InterruptedException ie){
                ie.printStackTrace();
            }
        }
        return priv.sendMessage(message).complete();
    }

    /**
     * Send a simple message on a public channel with no typing delay
     * @param channel : the channel to send the message on
     * @param message : content of the message to send (one line)
     * @return Message sent
     */
    public static Message tell(TextChannel channel, String message){
        return tell(channel,message,0);
    }


    /**
     * Send a simple message on a public channel
     * @param channel : the channel to send the message on
     * @param message : content of the message to send (one line)
     * @param typing : set a delay in seconds where bot is typing (0 : not typing)
     * @return Message sent
     */
    public static Message tell(TextChannel channel, String message, int typing){
        channel.sendTyping().complete();
        if (typing>0) {
            try{
                Thread.sleep(typing * 1000);
            }catch (InterruptedException ie){
                ie.printStackTrace();
            }
        }
        return channel.sendMessage(message).complete();
    }

    /**
     * Send a message on a public channel with into a frame
     * @param channel : the channel to send the message on
     * @param message : content of the message (one line)
     * @param type : can be java, markdown, css
     * @param typing : set a delay in seconds where bot is typing (0 : not typing)
     * @return Message sent
     */
    public static Message tellFramed(TextChannel channel,String message, String type){
        return tellFramed(channel,message,type,0);
    }

    /**
     * Send a message on a public channel with into a frame
     * @param channel : the channel to send the message on
     * @param message : content of the message (one line)
     * @param type : can be java, markdown, css
     * @param typing : set a delay in seconds where bot is typing (0 : not typing)
     * @return Message sent
     */
    public static Message tellFramed(TextChannel channel,String message, String type, int typing){
        message = "```"+type+"\n"+message+" ```";
        channel.sendTyping().complete();
        if (typing>0) {
            try{
                Thread.sleep(typing * 1000);
            }catch (InterruptedException ie){

            }


        }
        return channel.sendMessage(message).complete();
    }

    /**
     * Send a multiLine message by block of max message size.
     * @param channel : channel to send the message on
     * @param message : content of the message (multiple line)
     * @return List of the messages sent
     */
    public static List<Message> tellBlock(TextChannel channel, List<String> message){
        return tellBlock(channel,message,0);
    }



    /**
     * Send a multiLine message by block of max message size.
     * @param channel : channel to send the message on
     * @param message : content of the message (multiple line)
     * @param typing : set a delay in seconds where bot is typing (0 : not typing)
     * @return List of the messages sent
     */
    public static List<Message> tellBlock(TextChannel channel, List<String> message, int typing){
        List<Message> result = new ArrayList<Message>();
        String tell="";
        for (String line : message){
            if (tell.length()+line.length()>=MAX_MSG_SIZE){
                result.add(tell(channel, tell,typing));
                typing = 0;
                tell=line;
            }else{
                tell = tell+"\n"+line;
            }
        }
        result.add(tell(channel, tell, typing));
        return result;
    }


    /**
     * Send a multiLine message by block of max message size.
     * @param user : channel to send the message on
     * @param message : content of the message (multiple line)
     * @return List of the messages sent
     */
    public static List<Message> tellBlock(User user, List<String> message){
        List<Message> result = new ArrayList<>();
        String tell="";
        for (String line : message){
            if (tell.length()+line.length()>=MAX_MSG_SIZE){
                result.add(tell(user, tell));
                tell=line;
            }else{
                tell = tell+"\n"+line;
            }
        }
        result.add(tell(user, tell));
        return result;
    }

    /**
     * Send a multi line framed message with no delay
     * @param channel :  channel to send the message on
     * @param message : message content
     * @param type: can be java, markdown, css
     * @return List of the messages sent
     */
    public static List<Message> tellBlockFramed(TextChannel channel, List<String> message, String type){
        return tellBlockFramed(channel,message,type,0);
    }

    /**
     * Send a multi line framed message
     * @param channel :  channel to send the message on
     * @param message : message content
     * @param type: can be java, markdown, css
     * @param typing : set a delay in seconds where bot is typing (0 : not typing)
     * @return List of the messages sent
     */
    public static List<Message> tellBlockFramed(TextChannel channel, List<String> message, String type, int typing){
        List<Message> result = new ArrayList<Message>();
        String tell="```"+type+"\n";
        for (String line : message){
            if (tell.length()+line.length()>=MAX_MSG_SIZE){
                result.add(tell(channel, tell+" ```",typing));
                typing = 0;
                tell="```"+type+"\n"+line;
            }else{
                tell = tell+"\n"+line;
            }
        }
        result.add(tell(channel, tell+" ```",typing));
        return result;
    }

    /**
     * Send a file on a public channel
     * @param channel : channel to send the file on
     * @param file : file to send
     * @param msg : message content send with the file
     */
    public static Message sendFile(TextChannel channel, File file, String msg){
        Message result = null;
        try {
            Message message = new MessageBuilder().append(msg).build();
            result = channel.sendFile(file, message).complete();
        } catch (Exception e) {

        }
        return result;
    }

    /**
     * Delete a message
     * @param message : message to delete
     */
    public static void delete(Message message){
        MessageChannel channel = message.getChannel();
        channel.deleteMessageById(message.getId()).complete();
    }
	
}