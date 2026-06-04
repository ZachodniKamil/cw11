import java.util.ArrayList;
import java.util.List;

// Glowna logika biznesowa: tworzenie rezerwacji, zwroty, wyszukiwanie sprzetu i raporty.
public class ReservationService {
    private final List<Student> students = new ArrayList<>();
    private final List<Equipment> equipmentList = new ArrayList<>();
    private final List<Reservation> reservations = new ArrayList<>();
    private final DiscountPolicy discountPolicy;
    private int nextReservationNumber = 1;

    public ReservationService(DiscountPolicy discountPolicy) {
        this.discountPolicy = discountPolicy;
    }

    public void addStudent(Student student) {
        students.add(student);
    }

    public void addEquipment(Equipment equipment) {
        equipmentList.add(equipment);
    }

    public List<Student> getStudents() {
        return students;
    }

    public List<Equipment> getEquipmentList() {
        return equipmentList;
    }

    // Szuka studenta po id, zwraca null jak nie ma.
    private Student findStudent(String studentId) {
        for (Student s : students) {
            if (s.getId().equalsIgnoreCase(studentId)) {
                return s;
            }
        }
        return null;
    }

    // Szuka sprzetu po id, zwraca null jak nie ma.
    private Equipment findEquipment(String equipmentId) {
        for (Equipment e : equipmentList) {
            if (e.getId().equalsIgnoreCase(equipmentId)) {
                return e;
            }
        }
        return null;
    }

    private Reservation findReservation(String reservationId) {
        for (Reservation r : reservations) {
            if (r.getId().equalsIgnoreCase(reservationId)) {
                return r;
            }
        }
        return null;
    }

    // Tworzy rezerwacje po sprawdzeniu wszystkich regul. Rzuca wyjatek z komunikatem jak cos nie gra.
    public Reservation createReservation(String studentId, String equipmentId, int days) {
        Student student = findStudent(studentId);
        if (student == null) {
            throw new IllegalArgumentException("nie ma studenta o id " + studentId + ".");
        }

        Equipment equipment = findEquipment(equipmentId);
        if (equipment == null) {
            throw new IllegalArgumentException("nie ma sprzetu o id " + equipmentId + ".");
        }

        if (!equipment.isAvailable()) {
            throw new IllegalArgumentException("sprzet " + equipmentId + " nie jest dostepny.");
        }

        if (days < 1 || days > 14) {
            throw new IllegalArgumentException("liczba dni musi byc z zakresu 1-14.");
        }

        String id = String.format("R%03d", nextReservationNumber);
        nextReservationNumber++;

        Reservation reservation = new Reservation(id, student, equipment, days);
        equipment.setAvailable(false);
        reservations.add(reservation);
        return reservation;
    }

    // Zwrot sprzetu. Zwraca liczbe przyznanych punktow, rzuca wyjatek przy bledzie.
    public int returnEquipment(String reservationId) {
        Reservation reservation = findReservation(reservationId);
        if (reservation == null) {
            throw new IllegalArgumentException("nie ma rezerwacji o id " + reservationId + ".");
        }

        if (reservation.getStatus() != ReservationStatus.ACTIVE) {
            throw new IllegalArgumentException("rezerwacja " + reservationId + " nie jest aktywna.");
        }

        double cost = reservation.calculateTotalCost(discountPolicy);
        reservation.setStatus(ReservationStatus.RETURNED);
        reservation.getEquipment().setAvailable(true);

        // 1 punkt za kazde pelne 10 PLN kosztu.
        int points = (int) (cost / 10);
        reservation.getStudent().addLoyaltyPoints(points);
        return points;
    }

    // Wszystkie aktualnie dostepne sprzety.
    public List<Equipment> findAvailableEquipment() {
        List<Equipment> result = new ArrayList<>();
        for (Equipment e : equipmentList) {
            if (e.isAvailable()) {
                result.add(e);
            }
        }
        return result;
    }

    public List<Reservation> getReservationsByStatus(ReservationStatus status) {
        List<Reservation> result = new ArrayList<>();
        for (Reservation r : reservations) {
            if (r.getStatus() == status) {
                result.add(r);
            }
        }
        return result;
    }

    // Laczny przychod z zakonczonych (zwroconych) rezerwacji.
    public double getTotalRevenue() {
        double sum = 0;
        for (Reservation r : reservations) {
            if (r.getStatus() == ReservationStatus.RETURNED) {
                sum += r.calculateTotalCost(discountPolicy);
            }
        }
        return sum;
    }

    // Student z najwieksza liczba punktow lojalnosciowych.
    public Student getTopStudent() {
        Student top = null;
        for (Student s : students) {
            if (top == null || s.getLoyaltyPoints() > top.getLoyaltyPoints()) {
                top = s;
            }
        }
        return top;
    }
}
