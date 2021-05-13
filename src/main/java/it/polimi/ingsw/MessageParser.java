package it.polimi.ingsw;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The MessageParser class parses messages with the following format:
 * command(< parameters without spaces >);
 */
public class MessageParser
{
    private static final Pattern pattern = Pattern.compile("(?<command>\\w+)(?:\\((?<parameter>(?:(?:\\{.*}|.+),)*(?:\\{.*}|.+))?\\))?");
    private String order = "NOP";
    private String[] parameters;

    /**
     * Parses the given String as a message; results are stored in the current MessageParser.
     * If the message has a wrong format it is interpreted as a "NOP", meaning no-operation.
     * @param cmd the String to parse.
     */
    public void parse(String cmd) {
        Matcher matcher = pattern.matcher(cmd);
        String[] fragments;
        ArrayList<String> shards = new ArrayList<>();

        if(matcher.matches()) {
            this.order = matcher.group("command");
            if(matcher.group("parameter") != null) {

                fragments = matcher.group("parameter").split(",");

                StringBuilder shard = new StringBuilder();
                boolean collect = false;
                for(String param : fragments) {
                    if(collect) {
                        shard.append(",").append(param);
                        if(param.charAt(param.length() - 1) == '}') {
                            shards.add(shard.toString());
                            collect = false;
                        }
                    } else {
                        if(param.charAt(0) == '{' && param.charAt(param.length() - 1) != '}') {
                            shard = new StringBuilder(param);
                            collect = true;
                        }
                        else shards.add(param);
                    }
                }

                this.parameters = shards.toArray(new String[0]);
            }
        }
        else {
            this.order = "NOP";
        }
    }

    /**
     * Returns the command stored in the current MessageParser.
     * The invocation of this method should follow a .parse() call.
     * @return the command previously stored.
     */
    public String getOrder()
    {
        return this.order;
    }

    /**
     * Returns the number of parameters stored in the current MessageParser.
     * The invocation of this method should follow a .parse() call.
     * @return the number of parameters previously stored.
     */
    public int getNumberOfParameters()
    {
        if(this.parameters == null) return 0;
        else return this.parameters.length;
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
        if(index >= 0 && index < this.parameters.length)
            return Integer.parseInt(this.parameters[index]);
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
        if(index >= 0 && index < this.parameters.length)
            return this.parameters[index];
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
        Gson parser = new Gson();
        if(index >= 0 && index < this.parameters.length)
            return parser.fromJson(this.parameters[index],type);
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

        if(index >= 0 && index < this.parameters.length)
            return parser.fromJson(this.parameters[index],type);
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
        StringBuilder messageBuilder = new StringBuilder(order);
        if(parameters.length != 0)
        {
            messageBuilder.append('(');
            for(Object obj : parameters)
                messageBuilder.append(obj).append(',');
            messageBuilder.setCharAt(messageBuilder.length() - 1,')');
        }
        return messageBuilder.toString();
    }
}

