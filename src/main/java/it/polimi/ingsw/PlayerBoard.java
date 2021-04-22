package it.polimi.ingsw;

import it.polimi.ingsw.cards.*;
import it.polimi.ingsw.faith.FaithTrack;
import it.polimi.ingsw.supply.*;

import java.util.ArrayList;
import java.util.List;

public class PlayerBoard
{
    private final MarketBoard market;
    private final Storage storage;
    private final LeaderStack leaders;
    private final DevelopmentCardStack devCards;
    private final Factory factory;
    private final FaithTrack faithTrack;

    private final ResourcePack marketDiscounts;
    private final List<Resource> whiteExchange;

    public PlayerBoard(MarketBoard market,FaithTrack faithTrack)
    {
        this.market = market;
        this.storage = new Storage();
        this.leaders = new LeaderStack();
        this.devCards = new DevelopmentCardStack();
        this.factory = new Factory();
        this.faithTrack = faithTrack;

        this.marketDiscounts = new ResourcePack();
        this.whiteExchange = new ArrayList<Resource>();
    }

    public void giveResources(ResourcePack rp)
    {
        // TODO: da fare!
    }

    /**
     * Adds the given LeaderCards to the current PlayerBoard.
     * @param leaders the list of LeaderCards to add.
     */
    public void giveLeaders(List<LeaderCard> leaders)
    {
        this.leaders.addLeaders(leaders);
    }

    public void activateProduction() throws NonConsumablePackException
    {
        ResourcePack required = this.factory.productionRequirements();
        ResourcePack available = this.storage.getAllResource();
        if(available.isConsumable(required))
        {
            // TODO: only autoConsume is supported at the moment
            this.storage.autoConsume(required);
            this.storage.stockStrongbox(this.factory.productionChain());
        }
        // TODO: potrebbe sollevare un'eccezione qualora non fosse possibile
    }

    public void buyDevelopmentCard(int level, Color color, int position)
            throws NoSuchDevelopmentCardException, NonPositionableCardException, NonConsumablePackException
    {
        DevelopmentCard top = this.devCards.getDevCard(position);
        int currentLevel = (top == null) ? 0 : top.getLevel();

        if(level != currentLevel + 1) throw new NonPositionableCardException();
        else if(this.storage.getAllResource().isConsumable(this.market.getCost(level, color)))
        {
            DevelopmentCard devCard = this.market.getDevelopmentCard(level, color);
            // TODO: only autoConsume is supported at the moment
            this.storage.autoConsume(devCard.getCost());

            if(top != null) // Remove the old Production
                this.factory.discardProductionPower(top.getProduction());

            // Add the bought DevelopmentCard to the PlayerBoard
            this.devCards.storeDevCard(devCard,position);
            this.factory.addProductionPower(devCard.getProduction());
        }
        else throw new NonPositionableCardException();
    }

    // TODO: SEI ARRIVATO FINO A QUI!
    public void gatherResources(int position)
    {

    }

    public void playLeaderCard(int leader)
    {
        this.leaders.activate(leader,this);
    }

    public void discardLeader(int leader)
    {
        this.leaders.discard(leader);
        this.faithTrack.advance();
    }

    public int countPoints()
    {
        return 0;
    }

    // Methods for power activation

    public void addDiscount(ResourcePack rp)
    {
        this.marketDiscounts.add(rp);
        System.out.println("<PB> Add discount >> " + rp);
    }

    public void addProduction(Production p)
    {
        // Production objects are immutable so it is not necessary to make a copy
        this.factory.addProductionPower(p);
        System.out.println("<PB> Add Production >> " + p);
    }

    public void addWhite(Resource res)
    {
        this.whiteExchange.add(res);
        System.out.println("<PB> Add Resource >> " + res);
    }

    public void addLeaderStock(StockPower stock)
    {
        // StockPower objects are immutable so it is not necessary to make a copy
        this.storage.addStockPower(stock);
        System.out.println("<PB> Add Leader Stock >> " + stock);
    }
}
