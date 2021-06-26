package it.polimi.ingsw.model.singleplayer;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.model.cards.LeaderCard;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for the different types of Solo Action tokens.
 * @author Patrick Niantcho
 */
public abstract class SoloAction {
    private final boolean shuffle;

    /**
     * Constructs a SoloAction;
     * @param shuffle the boolean representing whether or not the action deck will be shuffled.
     */
    public SoloAction(boolean shuffle) {
        this.shuffle = shuffle;
    }

    /**
     * Returns a list of SoloActions stored in the specified file.
     * If the file does not exist or is corrupted returns an empty list.
     * @param filePath a String indicating the path of the file that stores SoloAction's data.
     * @return a list of SoloActions.
     */
    public static List<SoloAction> getSoloActionDeck(String filePath) {
        InputStream soloActionData = SoloAction.class.getClassLoader().getResourceAsStream(filePath);

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(SoloAction.class, new SoloAction.SoloReader());
        Gson parser = builder.create();
        Type listOfSoloAction = new TypeToken<List<SoloAction>>() {}.getType();

        List<SoloAction> solo;
        try {
            InputStreamReader fr = new InputStreamReader(soloActionData);
            solo = parser.fromJson(fr,listOfSoloAction);
            fr.close();
            return solo;
        } catch (Exception e) {
            solo = new ArrayList<>();
        }
        return solo;
    }

    /**
     * Reveals the SoloAction and applies its effect.
     * @param lorenzo the unique instance of LorenzoIlMagnifico in a singleplayer mode game.
     */
    public abstract void apply(LorenzoIlMagnifico lorenzo);

    /**
     * Returns whether the SoloAction causes the shuffling of the SoloActionDeck or not.
     * @return the boolean representing the need to shuffle or not.
     */
    public boolean toShuffle() {
        return shuffle;
    }

    public static class SoloReader implements JsonSerializer<SoloAction>, JsonDeserializer<SoloAction> {
        @Override
        public JsonElement serialize(SoloAction soloAction, Type typeOfSoloAction, JsonSerializationContext context) {
            JsonObject result = new JsonObject();
            result.add("type", new JsonPrimitive(soloAction.getClass().getSimpleName()));
            result.add("description", context.serialize(soloAction));
            return result;
        }

        @Override
        public SoloAction deserialize(JsonElement json, Type typeOfSoloAction, JsonDeserializationContext context) throws JsonParseException {
            JsonObject soloAction = json.getAsJsonObject();
            String type = soloAction.get("type").getAsString();
            JsonElement soloActionDescriptor = soloAction.get("description");
            try {
                return context.deserialize(soloActionDescriptor, Class.forName("it.polimi.ingsw.model.singleplayer." + type));
            } catch (ClassNotFoundException e) {
                throw new JsonParseException("Unknown singleplayer action's type: " + type, e);
            }
        }
    }

    @Override
    public String toString() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(SoloAction.class, new SoloAction.SoloReader());
        Gson parser = builder.create();
        return parser.toJson(this,SoloAction.class);
    }

    public static SoloAction fromString(String soloAction) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(SoloAction.class, new SoloAction.SoloReader());
        Gson parser = builder.create();
        return parser.fromJson(soloAction,SoloAction.class);
    }
}
