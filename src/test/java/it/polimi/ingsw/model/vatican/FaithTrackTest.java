package it.polimi.ingsw.model.vatican;

import it.polimi.ingsw.controller.MultiGame;
import org.junit.*;

import static org.junit.Assert.*;

public class FaithTrackTest {
    String filePath = "JSON/Vatican.json";
    Vatican vatican;
    FaithTrack our,other,another;

    @Before
    public void setUpFaithTracks() {
        vatican = new Vatican(new MultiGame(2),filePath);
        our = vatican.getFaithTrack(0);
        other = vatican.getFaithTrack(1);
        another = vatican.getFaithTrack(2);
    }

    @Test
    public void wastedResourcesTest(){
        our.wastedResources(1);
        assertEquals(our.getFaithMarker(),0);
        assertEquals(other.getFaithMarker(),1);
        assertEquals(another.getFaithMarker(),1);
    }

    @Test
    public void popeFavourTest(){
       our.advance(5);
       other.advance(8);

       assertEquals(our.countFavors(),2);
       assertEquals(other.countFavors(),2);
       assertEquals(another.countFavors(),0);
    }

    @Test
    public void countPointsTest(){
        our.advance(30);

        assertEquals(79,our.getTotalPoints());
        assertEquals(0,other.getTotalPoints());
    }
}