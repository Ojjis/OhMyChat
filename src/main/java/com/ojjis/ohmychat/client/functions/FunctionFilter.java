package com.ojjis.ohmychat.client.functions;

/**
 * Created with IntelliJ IDEA.
 * User: johan
 * Date: 12/2/13
 * Time: 4:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class FunctionFilter {

    public static String process(String text){
        if(text.startsWith("/help")){
            return "It looks like you need help. "+
                    "Here's a list of all the avaliable commands:\n"+
                    "/help - will display this help message\n" +
                    "/slap <user> - will slap the selected user\n" +
                    "/whois <user> - will list some info about the user\n";

        }else if(text.startsWith("/exit")){
            return "Use the menu bar to log out and exit please!\n";
        }else if(text.startsWith("/whois")){
            return"This has not yet been implemented!\n";
        }else if(text.startsWith("/slap")){
            return"This has not yet been implemented!\n";
        }else{
            return "Unknown command, write /help for more info...\n";
        }
    }
}
