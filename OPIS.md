# MediaLab - system rezerwacji sprzetu

Program konsolowy w Javie do wypozyczania sprzetu w pracowni MediaLab.
Po uruchomieniu pokazuje menu, mozna przejrzec studentow i sprzet, zrobic
rezerwacje, zwrocic sprzet i wyswietlic raport.

## Uruchomienie

javac -d out src/*.java
java -cp out Main

## Za co odpowiadaja klasy

Main - uruchamia program. Tworzy dane startowe (studentow i sprzet), wyswietla
menu i czyta to co wpisze uzytkownik. Starałem sie zeby w Main byla tylko
obsluga menu, a cala logika siedzi w ReservationService.

Student - przechowuje dane studenta (id, imie i nazwisko, grupa, punkty).
Ma metode do dodawania punktow po zwrocie sprzetu.

Equipment - klasa abstrakcyjna, wspolna baza dla calego sprzetu. Trzyma id,
nazwe, cene bazowa i czy sprzet jest dostepny. Ma metode abstrakcyjna
calculateDailyPrice(), bo kazdy typ sprzetu liczy cene inaczej.

LaptopSet - zestaw laptopowy, dziedziczy po Equipment. Do ceny bazowej dolicza
15 za stacje dokujaca i 25 jak ma co najmniej 32 GB RAM.

CameraKit - zestaw kamerowy, dziedziczy po Equipment. Dolicza 10 za kazdy
obiektyw i 15 za statyw.

Reservation - laczy studenta, sprzet, liczbe dni i status. Trzyma cale obiekty
Student i Equipment, a nie tylko ich id. Liczy calkowity koszt rezerwacji.

ReservationService - tu jest cala logika: tworzenie rezerwacji ze sprawdzaniem
regul, zwrot sprzetu, szukanie dostepnego sprzetu i raporty. Trzyma listy
(ArrayList) studentow, sprzetu i rezerwacji.

LoyaltyDiscountPolicy - osobna klasa, ktora liczy znizke za lojalnosc.

## Interfejsy

Displayable - ma jedna metode getDisplayText(), ktora zwraca gotowy tekst do
wypisania w konsoli. Implementuja go Equipment (czyli tez LaptopSet i CameraKit
przez dziedziczenie) oraz Reservation.

DiscountPolicy - metoda applyDiscount(student, cena). Implementuje go
LoyaltyDiscountPolicy. Dzieki temu Reservation przy liczeniu kosztu nie musi
wiedziec jak dziala znizka, tylko dostaje gotowa polityke.

## Enum

ReservationStatus - ACTIVE, RETURNED, CANCELLED.

## Gdzie widac polimorfizm

Lista sprzetu jest typu List<Equipment>, ale w srodku siedza obiekty LaptopSet
i CameraKit. Jak w Main lece petla po liscie i wolam getDisplayText() (a tam
calculateDailyPrice()), to Java sama wybiera wlasciwa wersje metody zaleznie od
tego jaki to naprawde obiekt. Laptop liczy cene inaczej niz kamera, mimo ze w
kodzie traktuje je tak samo jako Equipment.

## Reguly

- rezerwacje da sie zrobic tylko jak student i sprzet istnieja, sprzet jest
  wolny, a liczba dni jest miedzy 1 a 14
- po zrobieniu rezerwacji sprzet robi sie niedostepny i dostaje status ACTIVE
- znizke 10% dostaje student ktory ma co najmniej 100 punktow
- przy zwrocie status zmienia sie na RETURNED, sprzet znow jest wolny, a student
  dostaje 1 punkt za kazde pelne 10 PLN kosztu

## Przyklad dzialania (rezerwacja i zwrot)

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

Maly komentarz do kosztu: Sony to 90 bazy + 3 obiektywy po 10 + 15 za statyw,
czyli 135 za dzien. Razy 3 dni = 405, minus 10% znizki = 364.50 PLN.
