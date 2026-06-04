// Laczy studenta, sprzet, liczbe dni i status. Przechowuje cale obiekty, nie tylko id.
public class Reservation implements Displayable {
    private final String id;
    private final Student student;
    private final Equipment equipment;
    private final int days;
    private ReservationStatus status;

    public Reservation(String id, Student student, Equipment equipment, int days) {
        this.id = id;
        this.student = student;
        this.equipment = equipment;
        this.days = days;
        this.status = ReservationStatus.ACTIVE;
    }

    public String getId() {
        return id;
    }

    public Student getStudent() {
        return student;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public int getDays() {
        return days;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    // Calkowity koszt: cena dzienna * liczba dni, a potem znizka wg polityki.
    public double calculateTotalCost(DiscountPolicy discountPolicy) {
        double priceBeforeDiscount = equipment.calculateDailyPrice() * days;
        return discountPolicy.applyDiscount(student, priceBeforeDiscount);
    }

    @Override
    public String getDisplayText() {
        return String.format("%s | student: %s | sprzet: %s | dni: %d | status: %s",
                id, student.getFullName(), equipment.getName(), days, status);
    }
}
