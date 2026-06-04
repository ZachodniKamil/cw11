// Zestaw laptopowy. Cena zalezy od stacji dokujacej i ilosci RAM.
public class LaptopSet extends Equipment {
    private final int ramGb;
    private final boolean hasDockingStation;

    public LaptopSet(String id, String name, double baseDailyPrice, int ramGb, boolean hasDockingStation) {
        super(id, name, baseDailyPrice);
        this.ramGb = ramGb;
        this.hasDockingStation = hasDockingStation;
    }

    public int getRamGb() {
        return ramGb;
    }

    public boolean hasDockingStation() {
        return hasDockingStation;
    }

    @Override
    public double calculateDailyPrice() {
        double price = getBaseDailyPrice();
        if (hasDockingStation) {
            price += 15;
        }
        if (ramGb >= 32) {
            price += 25;
        }
        return price;
    }

    @Override
    public String getDetails() {
        String dock = hasDockingStation ? "stacja dokujaca" : "bez stacji dokujacej";
        return ramGb + " GB RAM, " + dock;
    }

    @Override
    public String getType() {
        return "LaptopSet";
    }
}
