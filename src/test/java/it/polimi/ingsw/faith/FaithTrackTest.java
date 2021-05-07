package it.polimi.ingsw.faith;

import it.polimi.ingsw.MultiGame;
import org.junit.*;

import static org.junit.Assert.*;

public class FaithTrackTest {
    String filePath = "src/main/resources/JSON/Vatican.json";
    Vatican vatican;
    FaithTrack our,other,another;

    @Before
    public void setUpFaithTracks() {
        vatican = new Vatican(new MultiGame(2),filePath);
        our = vatican.getFaithTrack();
        other = vatican.getFaithTrack();
        another = vatican.getFaithTrack();
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

        assertEquals(our.getTotalPoints(),79);
        assertEquals(other.getTotalPoints(),0);
    }
}