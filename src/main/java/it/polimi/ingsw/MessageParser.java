package it.polimi.ingsw;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The MessageParser class parses messages with the following format:
 * command(< parameters without spaces >);
 */
public class MessageParser
{
    private static final Pattern pattern = Pattern.compile("(?<command>\\w+)(?:\\((?<parameter>(?:\\w+,)*\\w+)\\))?");
    private String order = "NOP";
    private String[] parameters;

    /**
     * Parses the given String as a message; results are stored in the current MessageParser.
     * If the message has a wrong format it is interpreted as a "NOP", meaning no-operation.
     * @param cmd the String to parse.
     */
    public void parse(String cmd)
    {
        Matcher matcher = pattern.matcher(cmd);

        if(matcher.matches())
        {
            this.order = matcher.group("command");
            if(matcher.group("parameter") != null)
                this.parameters = matcher.group("parameter").split(",");
        }
        else
        {
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
}

