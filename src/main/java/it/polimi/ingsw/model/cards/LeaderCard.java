package it.polimi.ingsw.model.cards;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import it.polimi.ingsw.controller.PlayerBoard;
import it.polimi.ingsw.model.resources.ResourcePack;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Immutable class to represent Leader Cards.
 * @author Alessandro Mercurio
 */
public class LeaderCard extends Card {
    final private ResourcePack reqResources;
    final private ColorPack reqDevCards;
    final private Power power;

    /**
     * Constructs a LeaderCard with the given parameters.
     * @param reqResources the requirement in terms of Resources.
     * @param reqDevCards  the requirement in terms of DevelopmentCards.
     * @param power        the Power granted by the LeaderCard.
     */
    public LeaderCard(int points, ResourcePack reqResources, ColorPack reqDevCards, Power power) {
        super(points);
        this.reqResources = reqResources.getCopy();
        this.reqDevCards = reqDevCards.getCopy();
        this.power = power;
    }

    /**
     * Returns a List of all the available LeaderCards.
     * @return a list of all the LeaderCards.
     */
    public static List<LeaderCard> getLeaderCardDeck(String filePath) {
        InputStream data = LeaderCard.class.getClassLoader().getResourceAsStream(filePath);

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Power.class,new LeaderCard.PowerReader());
        builder.enableComplexMapKeySerialization();
        Gson parser = builder.create();

        Type leaderCardType = new TypeToken<List<LeaderCard>>() {}.getType();

        try {
            InputStreamReader leaderCard = new InputStreamReader(data);
            JsonReader reader = new JsonReader(leaderCard);
            return parser.fromJson(reader,leaderCardType);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * Activates the Leader on the given PlayerBoard;
     * its power is made available to the corresponding player for the rest of the game.
     * @param board the Player's PlayerBoard.
     */
    public void activate(PlayerBoard board) {
        this.power.activate(board);
    }

    /**
     * Returns the pack of Resources needed to be in Player's Storage to play the Leader.
     * @return a ResourcePack representing the required amount of each Resource.
     */
    public ResourcePack getReqResources() {
        return this.reqResources.getCopy();
    }

    /**
     * Returns the Development Cards needed to be on the Player's board to play the Leader.
     * @return a ColorPack representing the amount, color and level of the required Cards.
     */
    public ColorPack getReqDevCards() {
        return this.reqDevCards.getCopy();
    }

    /**
     * Returns the power associated with the current LeaderCard.
     * @return the power given by the current Leader.
     */
    public Power getPower() {
        // Since Power objects are immutale no copy is needed.
        return this.power;
    }

    public static class PowerReader implements JsonSerializer<Power>, JsonDeserializer<Power> {

        @Override
        public JsonElement serialize(Power power, Type typeOfPower, JsonSerializationContext context) {
            JsonObject result = new JsonObject();
            result.add("type", new JsonPrimitive(power.getClass().getSimpleName()));
            result.add("description", context.serialize(power, power.getClass()));

            return result;
        }

        @Override
        public Power deserialize(JsonElement json, Type typeOfPower, JsonDeserializationContext context) throws JsonParseException {
            JsonObject power = json.getAsJsonObject();
            String type = power.get("type").getAsString();
            JsonElement powerDescriptor = power.get("description");

            try {
                return context.deserialize(powerDescriptor, Class.forName("it.polimi.ingsw.model.cards." + type));
            } catch (ClassNotFoundException e) {
                throw new JsonParseException("Unknown Leader's power type: " + type, e);
            }
        }
    }

    @Override
    public String toString() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Power.class, new LeaderCard.PowerReader());
        builder.enableComplexMapKeySerialization();
        Gson parser = builder.create();

        return parser.toJson(this);
    }

    public static LeaderCard fromString(String leaderCard) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Power.class, new LeaderCard.PowerReader());
        builder.enableComplexMapKeySerialization();
        Gson parser = builder.create();

        return parser.fromJson(leaderCard,LeaderCard.class);
    }
}