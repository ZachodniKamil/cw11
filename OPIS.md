# MediaLab - system rezerwacji sprzetu

Konsolowy program w Javie do obslugi wypozyczalni sprzetu w pracowni MediaLab.
Pozwala przejrzec studentow i sprzet, utworzyc rezerwacje, zwrocic sprzet
i wyswietlic raport z przychodow.

## Jak uruchomic

```
javac -d out src/*.java
java -cp out Main
```

## Opis klas

- **Main** - punkt wejscia programu. Tworzy dane startowe (studenci, sprzet),
  wyswietla menu i odczytuje wybor uzytkownika z konsoli. Sama logika biznesowa
  jest w `ReservationService`, w `Main` jest tylko obsluga menu i komunikaty.
- **Student** - dane studenta: id, imie i nazwisko, grupa, punkty lojalnosciowe.
  Potrafi dodac sobie punkty po zwrocie sprzetu.
- **Equipment** - abstrakcyjna klasa bazowa dla sprzetu. Trzyma id, nazwe, cene
  bazowa i informacje o dostepnosci. Definiuje abstrakcyjna metode
  `calculateDailyPrice()`, ktora kazdy typ sprzetu liczy po swojemu.
- **LaptopSet** - zestaw laptopowy (dziedziczy po Equipment). Cena zalezy od
  stacji dokujacej (+15) i ilosci RAM (+25 jesli >= 32 GB).
- **CameraKit** - zestaw kamerowy (dziedziczy po Equipment). Cena zalezy od
  liczby obiektywow (+10 za sztuke) i statywu (+15).
- **Reservation** - laczy studenta, sprzet, liczbe dni i status. Przechowuje
  cale obiekty Student i Equipment, nie tylko ich id. Liczy calkowity koszt.
- **ReservationService** - glowna logika biznesowa: tworzenie rezerwacji ze
  sprawdzeniem regul, zwrot sprzetu, wyszukiwanie dostepnego sprzetu i raporty.
  Trzyma kolekcje (ArrayList) studentow, sprzetu i rezerwacji.
- **LoyaltyDiscountPolicy** - osobna klasa naliczajaca znizke za lojalnosc.

## Opis interfejsow

- **Displayable** (`String getDisplayText()`) - dla obiektow, ktore potrafia
  przygotowac czytelny opis do wyswietlenia w konsoli.
  Implementuja go: **Equipment** (a przez dziedziczenie LaptopSet i CameraKit)
  oraz **Reservation**.
- **DiscountPolicy** (`double applyDiscount(Student, double)`) - opisuje sposob
  naliczania znizki. Implementuje go klasa **LoyaltyDiscountPolicy**.
  Dzieki temu `Reservation.calculateTotalCost(...)` nie wie nic o szczegolach
  znizki - dostaje tylko obiekt polityki, co ulatwia podmiane regul w przyszlosci.

## Enum

- **ReservationStatus** - stan rezerwacji: ACTIVE, RETURNED, CANCELLED.

## Gdzie dziala polimorfizm

Lista sprzetu jest typu `List<Equipment>`, ale trzyma rozne obiekty:
LaptopSet i CameraKit. Gdy w `Main.showEquipment()` wywolujemy w petli
`e.getDisplayText()` (a w srodku `calculateDailyPrice()`), Java sama wybiera
wersje metody pasujaca do faktycznego typu obiektu - laptop liczy cene inaczej
niz kamera, mimo ze w kodzie operujemy na wspolnym typie Equipment.
To samo dzieje sie przy liczeniu kosztu rezerwacji.

## Reguly biznesowe (w skrocie)

- Rezerwacja powstaje tylko gdy student i sprzet istnieja, sprzet jest dostepny,
  a liczba dni miesci sie w zakresie 1-14.
- Po utworzeniu sprzet staje sie niedostepny, rezerwacja dostaje status ACTIVE.
- Cena laptopa: baza (+15 stacja dokujaca) (+25 RAM >= 32 GB).
- Cena kamery: baza + 10 za kazdy obiektyw (+15 statyw).
- Student z >= 100 punktami dostaje 10% znizki na cala rezerwacje.
- Przy zwrocie status zmienia sie na RETURNED, sprzet wraca do dostepnych,
  a student dostaje 1 punkt za kazde pelne 10 PLN kosztu.

## Przykladowy zapis konsoli (utworzenie i zwrot rezerwacji)

```
Wybor: 3
Podaj id studenta: S001
Podaj id sprzetu: E003
Podaj liczbe dni: 3

Utworzono rezerwacje R001.
Sprzet: Sony Content Kit
Koszt: 364.50 PLN
Status: ACTIVE

Wybor: 3
Podaj id studenta: S002
Podaj id sprzetu: E003
Podaj liczbe dni: 2

Blad: sprzet E003 nie jest dostepny.

Wybor: 4
Podaj id rezerwacji: R001

Zwrocono sprzet. Student otrzymal 36 punkty lojalnosciowe.

Wybor: 6

=== RAPORT ===

Aktywne rezerwacje (0):
  brak

Zakonczone rezerwacje (1):
  R001 | student: Anna Kowalska | sprzet: Sony Content Kit | dni: 3 | status: RETURNED

Laczny przychod z zakonczonych rezerwacji: 364.50 PLN
Student z najwieksza liczba punktow: Anna Kowalska (156 pkt)
```

Uwaga do kosztu: dla zestawu Sony (baza 90 + 3 obiektywy * 10 + statyw 15 = 135/dzien),
3 dni = 405 PLN, a po 10% znizce lojalnosciowej = 364.50 PLN. Zgadza sie z regulami z tresci.
