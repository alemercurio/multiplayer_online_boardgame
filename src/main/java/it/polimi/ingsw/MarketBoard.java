package it.polimi.ingsw;

import java.util.LinkedList;

public class MarketBoard {
    private final ResourceMarket resourceMarket;
    private final CardMarket cardMarket;


    /**
     * Represent the resource section of the MarketBoard.
     */
    public static class ResourceMarket
    {
        private final Resource[][] marketTray;
        private Resource remaining;

        /**
         * Constructs a ResourceMarket containing the given resources in random order.
         * if the resources provided are not enough to fill the market
         * empty spaces are filled with VOID; if the pack has more resources than necessary
         * the excess is ignored.
         * @param resources the resources to initialize the market with.
         */
        public ResourceMarket(ResourcePack resources)
        {
            this.marketTray = new Resource[3][4];
            for(int r = 0;r < 3;r++)
                for(int c = 0;c < 4;c++)
                    this.marketTray[r][c] = resources.getRandom();
            this.remaining = resources.getRandom();
        }

        /**
         * Construct a standard ResourceMarket.
         */
        public ResourceMarket()
        {
            // Standard resources for the marketTray:
            //      2 coins,
            //      2 stones,
            //      2 servants,
            //      2 shields,
            //      1 faithpoint,
            //      4 voids

            this(new ResourcePack(2,2,2,2,1,4));
        }

        /**
         * Gather resources from the given column; market shift is automatically handled.
         * @param column the column whose resources are desired.
         * @return a pack of the resources previously present in the given column.
         */
        public ResourcePack getColumn(int column)
        {
            ResourcePack resources = new ResourcePack();

            // Adjust the index of column
            if(column >= 4) column = 3;
            else if(column < 0) column = 0;

            // Gathers resources and shifts them
            Resource tmp_resource;
            for(int row = 2;row >= 0; row--)
            {
                tmp_resource = this.marketTray[row][column];
                resources.add(tmp_resource,1);
                this.marketTray[row][column] = this.remaining;
                this.remaining = tmp_resource;
            }

            return resources;
        }

        /**
         * Gather resources from the given row; market shift is automatically handled.
         * @param row the row whose resources are desired.
         * @return a pack of the resources previously present in the given row.
         */
        public ResourcePack getRow(int row)
        {
            ResourcePack resources = new ResourcePack();

            // Adjust the index of column
            if(row >= 3) row = 2;
            else if(row < 0) row = 0;

            // Gathers resources and shifts them
            Resource tmp_resource;
            for(int column = 3;column >= 0; column--)
            {
                tmp_resource = this.marketTray[row][column];
                resources.add(tmp_resource,1);
                this.marketTray[row][column] = this.remaining;
                this.remaining = tmp_resource;
            }

            return resources;
        }

        @Override
        public String toString()
        {
            String resMarket = "{";
            for(int r = 0;r < 3;r++)
            {
                resMarket = resMarket + "\n\t";
                for (int c = 0; c < 4; c++)
                {
                    resMarket = resMarket + marketTray[r][c].getAlias();
                }
            }
            resMarket = resMarket + "\n}";
            return resMarket;
        }
    }

    public static class CardMarket
    {
        private final LinkedList<DevelopmentCard>[][] decksMap;

        public CardMarket()
        {
            this.decksMap = new LinkedList[3][4];
            for(int row = 0;row < 3;row++)
                for(int column = 0;column < 4; column++)
                {
                    this.decksMap[row][column] = new LinkedList<DevelopmentCard>();
                }

            for(DevelopmentCard devCard : DevelopmentCard.getDevelopmentCardDeck())
                this.decksMap[devCard.getLevel()][devCard.getColor().ordinal()].add(devCard);
        }

        public DevelopmentCard getDevelopmentCard(int level, Color color)
        {
            // TODO: Potrebbe generare un'eccezione (anzichè tornare null)
            return this.decksMap[level][color.ordinal()].pollFirst();
        }

        public boolean discard(Color color, int amount)
        {
            int level = 0;
            for(int i = 0; i < amount; i++)
            {
                if(this.decksMap[level][color.ordinal()].pollFirst() == null)
                {
                    // It is necessary to remove from a higher level
                    level++;
                    if(level == 3) return false; // The column is empty

                    // Since the card has not been discarded
                    // It is necessary to redo this iteration
                    i--;
                }
            }
            return true;
        }

        @Override
        public String toString()
        {
            String res = "{";
            for(int row = 2; row >= 0; row--)
            {
                res = res + "\tLEVEL:" + (row + 1) + "\n";
                for(int column = 0; column < 4; column++)
                {
                    if(!this.decksMap[row][column].isEmpty()) res = res + "\t" + this.decksMap[row][column].get(0);
                }
                res = res + "\n";
            }
            res = res + "}";
            return res;
        }
    }

    public MarketBoard()
    {
        this.resourceMarket = new ResourceMarket();
        this.cardMarket = new CardMarket();
    }

    public ResourcePack takeRow(int row)
    {
        return this.resourceMarket.getRow(row);
    }

    public ResourcePack takeColumn(int column)
    {
        return this.resourceMarket.getColumn(column);
    }

    public DevelopmentCard getDevelopmentCard(int level, Color color)
    {
        return this.cardMarket.getDevelopmentCard(level,color);
    }

    public boolean discard(Color color, int amount)
    {
        return this.cardMarket.discard(color, amount);
    }

    public String getResourceMarketView()
    {
        return this.resourceMarket.toString();
    }

    public String getCardMarketView()
    {
        return this.cardMarket.toString();
    }

}
