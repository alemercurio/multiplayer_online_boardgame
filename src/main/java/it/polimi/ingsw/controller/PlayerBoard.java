package it.polimi.ingsw.controller;

import it.polimi.ingsw.network.Player;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.vatican.FaithTrack;
import it.polimi.ingsw.model.resources.*;
import it.polimi.ingsw.network.Talkie;
import it.polimi.ingsw.util.MessageParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerBoard
{
    private final MarketBoard market;
    private Talkie player;
    public final Storage storage;
    public final LeaderStack leaders;
    public final DevelopmentCardStack devCards;
    public final Factory factory;
    public final FaithTrack faithTrack;

    private final ResourcePack marketDiscounts;
    private final List<Resource> whiteExchange;

    private static final Production baseProduction = new Production(
            new ResourcePack().add(Resource.VOID,2),
            new ResourcePack().add(Resource.VOID,1));

    public PlayerBoard(Talkie player, MarketBoard market, FaithTrack faithTrack)
    {
        this.market = market;
        this.player = player;
        this.storage = new Storage();
        this.leaders = new LeaderStack();
        this.devCards = new DevelopmentCardStack();

        this.factory = new Factory();
        this.addProduction(baseProduction);

        this.faithTrack = faithTrack;
        this.player.send(MessageParser.message("update","faith:track",this.faithTrack));
        this.player.send(MessageParser.message("update","faith:config",this.faithTrack.getConfig()));

        this.marketDiscounts = new ResourcePack();
        this.whiteExchange = new ArrayList<>();
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Adds the given LeaderCards to the current PlayerBoard.
     * @param leaders the list of LeaderCards to add.
     */
    public void giveLeaders(List<LeaderCard> leaders) { this.leaders.addLeaders(leaders); }

    /**
     * Activates productions in the current PlayerBoard's Factory;
     */
    public int activateProduction() throws NonConsumablePackException {
        ResourcePack required = this.factory.productionRequirements();
        ResourcePack available = this.storage.getAllResource();
        if(available.isConsumable(required)) {

            // Consumes resources required to activate productions.
            this.storage.autoConsume(required);
            this.player.send(MessageParser.message("update","WHConfig",this.storage.warehouse.getConfig()));

            // Collects the products.
            ResourcePack products = this.factory.productionChain();
            int white = products.flush(Resource.VOID);

            this.faithTrack.advance(products.flush(Resource.FAITHPOINT));

            this.storage.stockStrongbox(products);
            this.player.send(MessageParser.message("update","strongbox",this.storage.strongbox));

            return white;
        }
        else throw new NonConsumablePackException();
    }

    public int activateProduction(ResourcePack whiteSelection) throws NonConsumablePackException {
        ResourcePack required = this.factory.productionRequirements();
        ResourcePack available = this.storage.getAllResource();

        if(whiteSelection.size() == required.get(Resource.VOID)) {
            required.add(whiteSelection);
            required.flush(Resource.VOID);
            required.flush(Resource.FAITHPOINT);
        }
        else throw new NonConsumablePackException();

        if(available.isConsumable(required)) {

            // Consumes resources required to activate productions.
            this.storage.autoConsume(required);
            this.player.send(MessageParser.message("update","WHConfig",this.storage.warehouse.getConfig()));

            // Collects the products.
            ResourcePack products = this.factory.productionChain();
            int white = products.flush(Resource.VOID);

            this.faithTrack.advance(products.flush(Resource.FAITHPOINT));

            this.storage.stockStrongbox(products);
            this.player.send(MessageParser.message("update","strongbox",this.storage.strongbox));

            return white;
        }
        else throw new NonConsumablePackException();
    }

    /**
     * Tests if it is possible to buy the DevelopmentCard with the given level and colour from the Market;
     * @param level the level of the desired DevelopmentCard.
     * @param color the colour of the desired DevelopmentCard.
     * @return true if it is possible to buy the DevelopmentCard, false otherwise.
     */
    public boolean canBuyDevCard(int level,Color color) {
        try {
            DevelopmentCard toBuy = this.market.getDevelopmentCard(level,color);
            ResourcePack price = toBuy.getCost();
            price.discount(this.marketDiscounts);
            return this.devCards.canBeStored(toBuy) && this.storage.getAllResource().isConsumable(price);
        } catch (NoSuchDevelopmentCardException e) {
            return false;
        }
    }

    public boolean canBeStored(int level,int position) {
        return this.devCards.canBeStored(level,position);
    }

    public void buyDevCard(int level,Color color,int position) throws NonConsumablePackException, NoSuchDevelopmentCardException, NonPositionableCardException {

        if(!this.canBeStored(level,position)) throw new NonPositionableCardException();

        ResourcePack price = this.market.getCost(level,color);
        price.discount(this.marketDiscounts);
        if(!this.storage.isConsumable(price)) throw new NonConsumablePackException();

        DevelopmentCard devCard = this.market.buyDevelopmentCard(level,color);

        this.storage.autoConsume(price);

        this.player.send(MessageParser.message("update","WHConfig",this.storage.warehouse.getConfig()));
        this.player.send(MessageParser.message("update","strongbox",this.storage.strongbox));

        this.storeDevelopmentCard(devCard,position);
    }

    /**
     * Stores the given DevelopmentCard in the current PlayerBoard's DevelopmentCardStack.
     * the production granted by the card is added to the current Factory.
     * @param devCard the DevelopmentCard to store.
     * @param position the index of the stack where the card is going to be positioned.
     * @throws NonPositionableCardException if the given card cannot be positioned.
     */
    public void storeDevelopmentCard(DevelopmentCard devCard, int position) throws NonPositionableCardException
    {
        DevelopmentCard covered = this.devCards.getDevCard(position);

        // Throws an exception if the card is not positionable
        this.devCards.storeDevCard(devCard,position);

        // Remove the production offered by the covered DevelopmentCard.
        if(covered != null) this.factory.discardProductionPower(covered.getProduction());

        // Add the Production offered by the new DevelopmentCard.
        this.factory.addProductionPower(devCard.getProduction());

        this.player.send(MessageParser.message("update","devCards",this.devCards));
        this.player.send(MessageParser.message("update","fact",this.factory));
    }

    public int storeResources(ResourcePack loot)
    {
        ResourcePack toStore = loot.getCopy();

        this.faithTrack.advance(toStore.flush(Resource.FAITHPOINT));

        int white = toStore.flush(Resource.VOID);
        this.storage.warehouse.add(toStore);
        this.player.send(MessageParser.message("update","WHConfig",this.storage.warehouse.getConfig()));

        return white;
    }

    public List<Resource> getWhiteExchange() {
        return Collections.unmodifiableList(this.whiteExchange);
    }

    public boolean hasWhitePower()
    {
        return (this.whiteExchange != null) && (!this.whiteExchange.isEmpty());
    }

    public boolean convertResources(int amount,ResourcePack desired)
    {
        int ableToConvert = 0;
        for(Resource resource : this.whiteExchange)
            ableToConvert += desired.get(resource);
        if(ableToConvert == amount)
        {
            this.storage.warehouse.add(desired);
            this.player.send(MessageParser.message("update","WHConfig",this.storage.warehouse.getConfig()));
            return true;
        }
        else return false;
    }

    public int pendingResources() {
        return this.storage.warehouse.getPendingResources().size();
    }

    public int done() {
        int wasted = this.storage.warehouse.done();
        this.faithTrack.wastedResources(wasted);
        return wasted;
    }

    public boolean playLeaderCard(int leader) {
        LeaderCard toPlay = this.leaders.getInactiveLeader(leader);
        if(toPlay == null) return false;

        // Test if the requirements are satisfied.
        if(!this.storage.getAllResource().isConsumable(toPlay.getReqResources()))
            return false;
        if(!this.devCards.getColorPack().testRequirements(toPlay.getReqDevCards()))
            return false;

        // Plays the LeaderCard
        this.leaders.activate(leader,this);
        return true;
    }

    public boolean discardLeader(int leader) {
        if(this.leaders.getInactiveLeader(leader) == null) return false;
        else {
            this.leaders.discard(leader);
            this.faithTrack.advance(1);
            return true;
        }
    }

    public int countPoints() {
        return this.devCards.getPoints()
        + this.faithTrack.getTotalPoints()
        + this.leaders.getPoints()
        + this.storage.getAllResource().size()/5;
    }

    public DevelopmentCard getDevelopmentCard(int level, Color color) throws NoSuchDevelopmentCardException {
            return this.market.getDevelopmentCard(level, color);
    }

    public DevelopmentCard buyDevelopmentCard(int level, Color color) throws NoSuchDevelopmentCardException {
        return this.market.buyDevelopmentCard(level, color);
    }

    // Methods for power activation

    public void addDiscount(ResourcePack rp) {
        this.marketDiscounts.add(rp);
        this.player.send(MessageParser.message("update","discount",this.marketDiscounts));
    }

    public void addProduction(Production p) {
        this.factory.addProductionPower(p);
        this.player.send(MessageParser.message("update","fact",this.factory));
    }

    public void addWhite(Resource res) {
        if(!this.whiteExchange.contains(res)) this.whiteExchange.add(res);
        this.player.send(MessageParser.message("update","white",this.whiteExchange));
    }

    public void addLeaderStock(StockPower stock) {
        this.storage.warehouse.addStockPower(stock);
        this.player.send(MessageParser.message("update","WH",stock));
    }

    // NEW

    public void updateAll() {
        this.player.send(MessageParser.message("update","fact",this.factory));
        this.player.send(MessageParser.message("update","white",this.whiteExchange));
        this.player.send(MessageParser.message("update","discount",this.marketDiscounts));
        for(StockPower stock : this.storage.warehouse.getStockPower())
            this.player.send(MessageParser.message("update","WH",stock));
        this.player.send(MessageParser.message("update","WHConfig",this.storage.warehouse.getConfig()));
        this.player.send(MessageParser.message("update","strongbox",this.storage.strongbox));
        this.player.send(MessageParser.message("update","leaders",this.leaders));
        this.player.send(MessageParser.message("update","devCards",this.devCards));
        this.player.send(MessageParser.message("update","faith:track",this.faithTrack));
        this.player.send(MessageParser.message("update","faith:config",this.faithTrack.getConfig()));
    }
}
