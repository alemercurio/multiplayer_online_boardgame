package it.polimi.ingsw.model.cards;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import it.polimi.ingsw.model.resources.Production;
import it.polimi.ingsw.model.resources.ResourcePack;
import javafx.scene.image.Image;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Immutable class to represent Development Cards.
 * @author Alessandro Mercurio
 */
public class DevelopmentCard extends Card {
    final private ResourcePack cost;
    final private Color color;
    final private int level;
    final private Production production;

    /**
     * Constructs a DevelopmentCard with the given parameters.
     *
     * @param cost       the ResourcePack representing the cost of the card.
     * @param color      the Color of the card.
     * @param level      the level of the card.
     * @param production the Production associated with the card.
     */
    public DevelopmentCard(int points, ResourcePack cost, Color color, int level, Production production) {
        super(points);
        this.cost = cost.getCopy();
        this.color = color;
        this.level = level;
        this.production = production;
    }

    /**
     * Returns a List of all the available DevelopmentCards.
     * @param filePath the path of the JSON where the DevelopmentCards are stored.
     * @return a list of all the DevelopmentCards.
     */
    public static List<DevelopmentCard> getDevelopmentCardDeck(String filePath) {
        InputStream data = DevelopmentCard.class.getClassLoader().getResourceAsStream(filePath);
        Gson parser = new Gson();
        Type devCardType = new TypeToken<List<DevelopmentCard>>() {}.getType();

        try {
            InputStreamReader devCard = new InputStreamReader(data);
            JsonReader reader = new JsonReader(devCard);
            return parser.fromJson(reader,devCardType);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * Getter for the cost, in terms of Resources, to buy the Card.
     * @return the ResourcePack representing the cost.
     */
    public ResourcePack getCost() {
        return this.cost.getCopy();
    }

    /**
     * Getter for the Production characterizing the Development Card.
     * @return the Production that the Card makes available for the player.
     */
    public Production getProduction() {
        // Because Production objects are immutable, they can be shared.
        return this.production;
    }

    /**
     * Getter for the color of the Card.
     * @return the Color of the Card.
     */
    public Color getColor() {
        return this.color;
    }

    /**
     * Getter for the level of the Card.
     * @return the level of the Card.
     */
    public int getLevel() {
        return this.level;
    }

    /**
     * Getter for the matching png file for the Card.
     * @return the path to the file as String.
     */
    public String getPathForCard() {
        String url = String.format("/PNG/cardfront/DevCardFront%s%d.png", this.getColor().getAlias(), this.getPoints());
        return url;
    }

    @Override
    public String toString() {
        Gson parser = new Gson();
        return parser.toJson(this);
    }

    public static DevelopmentCard fromString(String devCard) {
        Gson parser = new Gson();
        return parser.fromJson(devCard,DevelopmentCard.class);
    }
}
