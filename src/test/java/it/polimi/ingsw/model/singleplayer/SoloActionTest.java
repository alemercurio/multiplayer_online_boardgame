package it.polimi.ingsw.model.singleplayer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class SoloActionTest {

    SoloAction soloCrossShuffle = new SoloCross(1,true);
    SoloAction soloCrossNonShuffle = new SoloCross(1,false);
    SoloAction soloDiscard = new SoloDiscard(new HashMap<>());

    String scShuffle = "{\"type\":\"SoloCross\",\"description\":{\"faithPoints\":1,\"shuffle\":true}}";
    String scNonShuffle = "{\"type\":\"SoloCross\",\"description\":{\"faithPoints\":1,\"shuffle\":false}}";
    String sd = "{\"type\":\"SoloDiscard\",\"description\":{\"toDiscard\":{},\"shuffle\":false}}";

    Gson parser;

    @Before
    public void setUp() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(SoloAction.class,new SoloAction.SoloReader());
        parser = builder.create();
    }

    @Test
    public void testSerialization() {
        assertEquals(parser.toJson(soloCrossShuffle,SoloAction.class),scShuffle);
        assertEquals(parser.toJson(soloCrossNonShuffle,SoloAction.class),scNonShuffle);
        assertEquals(parser.toJson(soloDiscard,SoloAction.class),sd);
    }

    @Test
    public void testDeserialization() {
        assertEquals(parser.fromJson(scShuffle,SoloAction.class),soloCrossShuffle);
        assertEquals(parser.fromJson(scNonShuffle,SoloAction.class),soloCrossNonShuffle);
        assertEquals(parser.fromJson(sd,SoloAction.class),soloDiscard);
    }

    @Test
    public void testFromString() {
        String soloAction = "{\"type\":\"SoloCross\",\"description\":{\"faithPoints\":1,\"shuffle\":true}}";
        SoloAction actualAction = SoloAction.fromString(soloAction);
        assertEquals(new SoloCross(1, true), actualAction);
    }
}