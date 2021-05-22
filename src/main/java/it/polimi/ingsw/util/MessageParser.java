package it.polimi.ingsw.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;

/**
 * The MessageParser class parses messages with the following format:
 * command(< parameters without spaces >);
 */
public class MessageParser
{
    private static class Message
    {
        private final String order;
        private final String[] parameters;

        private Message(String order,Object...parameters)
        {
            this.order = order;

            this.parameters = new String[parameters.length];
            for(int i = 0; i < parameters.length; i++)
                this.parameters[i] = parameters[i].toString();
        }
    }

    private static final Gson parser = new Gson();
    private Message message;

    /**
     * Parses the given String as a message; results are stored in the current MessageParser.
     * If the message has a wrong format it is interpreted as a "NOP", meaning no-operation.
     * @param cmd the String to parse.
     */
    public void parse(String cmd) {

        try {
            this.message = parser.fromJson(cmd,Message.class);
        } catch(Exception e) {
            this.message = new Message(cmd);
        }

    }

    /**
     * Returns the command stored in the current MessageParser.
     * The invocation of this method should follow a .parse() call.
     * @return the command previously stored.
     */
    public String getOrder()
    {
        if(this.message == null) return "NOP";
        else return this.message.order;
    }

    /**
     * Returns the number of parameters stored in the current MessageParser.
     * The invocation of this method should follow a .parse() call.
     * @return the number of parameters previously stored.
     */
    public int getNumberOfParameters()
    {
        if(this.message == null) return 0;
        else return this.message.parameters.length;
    }

    /**
     * Returns the parameter with the specified index as an integer.
     * if the given index exceeds the amount of parameters stored in the current
     * MessageParser returns zero;
     * The invocation of this method should follow a .parse() call.
     * @return the required parameter as an integer.
     */
    public int getIntParameter(int index)
    {
        if(index >= 0 && index < this.message.parameters.length)
            return Integer.parseInt(this.message.parameters[index]);
        else return 0;
    }

    /**
     * Returns the parameter with the specified index as a String.
     * if the given index exceeds the amount of parameters stored in the current
     * MessageParser returns null;
     * The invocation of this method should follow a .parse() call.
     * @return the required parameter as a String.
     */
    public String getStringParameter(int index)
    {
        if(index >= 0 && index < this.message.parameters.length)
            return this.message.parameters[index];
        else return null;
    }

    /**
     * Returns the parameter with the specified index as an object of the specified type.
     * if the given index exceeds the amount of parameters stored in the current
     * MessageParser returns null;
     * @param index the index of the desired parameter.
     * @param type the type of the given parameter.
     * @return the required parameter as an object of the specified class.
     */
    public <T> T getObjectParameter(int index, Class<T> type)
    {
        if(index >= 0 && index < this.message.parameters.length)
            return parser.fromJson(this.message.parameters[index],type);
        else return null;
    }

    /**
     * Returns the parameter with the specified index as an object of the specified type;
     * if the given index exceeds the amount of parameters stored in the current
     * MessageParser returns null;
     * @param index the index of the desired parameter.
     * @param type the type of the given parameter.
     * @param adapter a custom adapter for the class.
     * @return the required parameter as an object of the specified class.
     */
    public <T> T getObjectParameter(int index, Class<T> type,TypeAdapter<T> adapter)
    {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(type,adapter);
        Gson parser = builder.create();

        if(index >= 0 && index < this.message.parameters.length)
            return parser.fromJson(this.message.parameters[index],type);
        else return null;
    }

    /**
     * Composes a message with the given order and parameters.
     * @param order a String representing the command.
     * @param parameters a sequence of parameters.
     * @return a String representing the composed message.
     */
    public static String message(String order,Object...parameters)
    {
        return parser.toJson(new Message(order,parameters));
    }
}

