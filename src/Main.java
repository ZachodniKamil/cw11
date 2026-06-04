import java.util.List;
import java.util.Locale;
import java.util.Scanner;

// Uruchamia program, tworzy dane startowe i obsluguje menu konsolowe.
public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static ReservationService service;

    public static void main(String[] args) {
        // Wymuszamy kropke jako separator dziesietny przy wypisywaniu cen.
        Locale.setDefault(Locale.ROOT);

        service = new ReservationService(new LoyaltyDiscountPolicy());
        createSampleData();

        System.out.println("=== MediaLab - system rezerwacji sprzetu ===");

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    showStudents();
                    break;
                case "2":
                    showEquipment();
                    break;
                case "3":
                    createReservation();
                    break;
                case "4":
                    returnEquipment();
                    break;
                case "5":
                    showActiveReservations();
                    break;
                case "6":
                    showReport();
                    break;
                case "0":
                    running = false;
                    System.out.println("Koniec programu. Do zobaczenia!");
                    break;
                default:
                    System.out.println("Nieznana opcja, sprobuj jeszcze raz.");
            }
        }
    }

    // Dane startowe - studenci i sprzet z tresci zadania.
    private static void createSampleData() {
        service.addStudent(new Student("S001", "Anna Kowalska", "12c", 120));
        service.addStudent(new Student("S002", "Marek Nowak", "12c", 40));
        service.addStudent(new Student("S003", "Julia Zielinska", "13a", 0));

        service.addEquipment(new LaptopSet("E001", "Lenovo ThinkPad Lab", 80, 32, true));
        service.addEquipment(new LaptopSet("E002", "Dell XPS Demo", 100, 16, false));
        service.addEquipment(new CameraKit("E003", "Sony Content Kit", 90, 3, true));
        service.addEquipment(new CameraKit("E004", "Canon Interview Kit", 70, 1, true));
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("1. Wyswietl studentow");
        System.out.println("2. Wyswietl sprzet");
        System.out.println("3. Utworz rezerwacje");
        System.out.println("4. Zwroc sprzet");
        System.out.println("5. Pokaz aktywne rezerwacje");
        System.out.println("6. Pokaz raport");
        System.out.println("0. Zakoncz");
        System.out.print("Wybor: ");
    }

    private static void showStudents() {
        System.out.println("\n--- Lista studentow ---");
        for (Student s : service.getStudents()) {
            System.out.println(s);
        }
    }

    private static void showEquipment() {
        System.out.println("\n--- Lista sprzetu ---");
        for (Equipment e : service.getEquipmentList()) {
            // getDisplayText() jest wywolywane polimorficznie - kazdy typ liczy cene po swojemu.
            System.out.println(e.getDisplayText());
        }
    }

    private static void createReservation() {
        System.out.print("Podaj id studenta: ");
        String studentId = scanner.nextLine().trim();
        System.out.print("Podaj id sprzetu: ");
        String equipmentId = scanner.nextLine().trim();
        System.out.print("Podaj liczbe dni: ");
        String daysText = scanner.nextLine().trim();

        int days;
        try {
            days = Integer.parseInt(daysText);
        } catch (NumberFormatException ex) {
            System.out.println("Blad: liczba dni musi byc liczba.");
            return;
        }

        try {
            Reservation reservation = service.createReservation(studentId, equipmentId, days);
            double cost = reservation.calculateTotalCost(new LoyaltyDiscountPolicy());
            System.out.println("\nUtworzono rezerwacje " + reservation.getId() + ".");
            System.out.println("Sprzet: " + reservation.getEquipment().getName());
            System.out.printf("Koszt: %.2f PLN%n", cost);
            System.out.println("Status: " + reservation.getStatus());
        } catch (IllegalArgumentException ex) {
            System.out.println("Blad: " + ex.getMessage());
        }
    }

    private static void returnEquipment() {
        System.out.print("Podaj id rezerwacji: ");
        String reservationId = scanner.nextLine().trim();

        try {
            int points = service.returnEquipment(reservationId);
            System.out.println("\nZwrocono sprzet. Student otrzymal " + points + " punkty lojalnosciowe.");
        } catch (IllegalArgumentException ex) {
            System.out.println("Blad: " + ex.getMessage());
        }
    }

    private static void showActiveReservations() {
        System.out.println("\n--- Aktywne rezerwacje ---");
        List<Reservation> active = service.getReservationsByStatus(ReservationStatus.ACTIVE);
        if (active.isEmpty()) {
            System.out.println("Brak aktywnych rezerwacji.");
        } else {
            for (Reservation r : active) {
                System.out.println(r.getDisplayText());
            }
        }
    }

    private static void showReport() {
        System.out.println("\n=== RAPORT ===");

        List<Reservation> active = service.getReservationsByStatus(ReservationStatus.ACTIVE);
        System.out.println("\nAktywne rezerwacje (" + active.size() + "):");
        if (active.isEmpty()) {
            System.out.println("  brak");
        } else {
            for (Reservation r : active) {
                System.out.println("  " + r.getDisplayText());
            }
        }

        List<Reservation> returned = service.getReservationsByStatus(ReservationStatus.RETURNED);
        System.out.println("\nZakonczone rezerwacje (" + returned.size() + "):");
        if (returned.isEmpty()) {
            System.out.println("  brak");
        } else {
            for (Reservation r : returned) {
                System.out.println("  " + r.getDisplayText());
            }
        }

        System.out.printf("%nLaczny przychod z zakonczonych rezerwacji: %.2f PLN%n", service.getTotalRevenue());

        Student top = service.getTopStudent();
        if (top != null) {
            System.out.println("Student z najwieksza liczba punktow: "
                    + top.getFullName() + " (" + top.getLoyaltyPoints() + " pkt)");
        }
    }
}
