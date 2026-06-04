// Interfejs opisujacy sposob naliczania znizki dla rezerwacji.
public interface DiscountPolicy {
    double applyDiscount(Student student, double price);
}
