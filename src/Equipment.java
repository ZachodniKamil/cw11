// Abstrakcyjna klasa bazowa dla kazdego rodzaju sprzetu.
// Kazdy konkretny sprzet liczy cene dzienna na swoj sposob (polimorfizm).
public abstract class Equipment implements Displayable {
    private final String id;
    private final String name;
    private final double baseDailyPrice;
    private boolean available;

    public Equipment(String id, String name, double baseDailyPrice) {
        this.id = id;
        this.name = name;
        this.baseDailyPrice = baseDailyPrice;
        this.available = true;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getBaseDailyPrice() {
        return baseDailyPrice;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    // Cena za jeden dzien - kazdy typ sprzetu nadpisuje to po swojemu.
    public abstract double calculateDailyPrice();

    // Krotki opis dodatkowych danych sprzetu (RAM, statyw itd.).
    public abstract String getDetails();

    // Nazwa typu sprzetu do wyswietlenia.
    public abstract String getType();

    @Override
    public String getDisplayText() {
        String status = available ? "dostepny" : "wypozyczony";
        return String.format("%s | %s | %s | %.2f PLN/dzien | %s | %s",
                id, name, getType(), calculateDailyPrice(), getDetails(), status);
    }
}
