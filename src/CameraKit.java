// Zestaw kamerowy. Cena zalezy od liczby obiektywow i statywu.
public class CameraKit extends Equipment {
    private final int lensCount;
    private final boolean hasTripod;

    public CameraKit(String id, String name, double baseDailyPrice, int lensCount, boolean hasTripod) {
        super(id, name, baseDailyPrice);
        this.lensCount = lensCount;
        this.hasTripod = hasTripod;
    }

    public int getLensCount() {
        return lensCount;
    }

    public boolean hasTripod() {
        return hasTripod;
    }

    @Override
    public double calculateDailyPrice() {
        double price = getBaseDailyPrice();
        price += lensCount * 10;
        if (hasTripod) {
            price += 15;
        }
        return price;
    }

    @Override
    public String getDetails() {
        String tripod = hasTripod ? "statyw" : "bez statywu";
        return lensCount + " obiektyw(y), " + tripod;
    }

    @Override
    public String getType() {
        return "CameraKit";
    }
}
