package pl.cinema.service;

import pl.cinema.model.Reservation;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

public interface ReservationService {
    void saveReservation(Reservation reservation);
    Reservation findReservation(String moviename, String day, String time, int room, int place);
    List<Integer> findPlaces(String moviename, int day, int month, int year, Time time, int room);
}
