package pl.cinema.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.cinema.model.*;
import pl.cinema.repository.*;
import pl.cinema.service.ReservationService;
import pl.cinema.service.UserService;

import javax.validation.Valid;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

@Controller
@SessionAttributes("reservation")
public class TicketController {

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private UserTicketRepository userTicketRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private PriceListRepository priceListRepository;
    @Autowired
    private GuestTicketRepository guestTicketRepository;
    @Autowired
    private RepertoireRepository repertoireRepository;

    @RequestMapping(value = "/buyticket/{name}", method = RequestMethod.GET)
    public String getDaysByMovieName(@PathVariable("name") String name,Model model){
        List <Repertoire> repertoires = repertoireRepository.findByMoviename(name);
        List <Date> days = new ArrayList<>();
        for(Repertoire r: repertoires){
            if(!days.contains(r.getId().getDay())) days.add(r.getId().getDay());
        }
        model.addAttribute("days",days);
        model.addAttribute("successMessage","days");
        model.addAttribute("url","/buyticket/"+name);
        return "buyticket";
    }

    @RequestMapping(value = "/buyticket/{name}/{day}", method = RequestMethod.GET)
    public String getHoursByDay(@PathVariable("name") String name,
                                @PathVariable("day") String day,
                                Model model){
        List <Repertoire> repertoires = repertoireRepository.findByMoviename(name);
        List <Date> days = new ArrayList<>();
        for(Repertoire r: repertoires){
            if(!days.contains(r.getId().getDay())) days.add(r.getId().getDay());
        }
        model.addAttribute("days",days);
        int year= Integer.parseInt(day.substring(0,4));
        int month= Integer.parseInt(day.substring(5,7));
        int dday= Integer.parseInt(day.substring(8,10));
        model.addAttribute("url1","/buyticket/"+name);
        model.addAttribute("times",repertoireRepository.findTimesByDay(name,dday,month,year));
        model.addAttribute("successMessage","times");
        model.addAttribute("url","/buyticket/"+name+"/"+day);
        model.addAttribute("name",name);
        return "buyticket";
    }

    @RequestMapping(value = "/buyticket/{name}/{day}/{time}", method = RequestMethod.GET)
    public String getPlace(@PathVariable("name") String name,
                                @PathVariable("day") String day,
                                @PathVariable("time") String time,
                                Model model){
        Time t = Time.valueOf(time);
        List <Repertoire> repertoires = repertoireRepository.findByMoviename(name);
        List <Date> days = new ArrayList<>();
        for(Repertoire r: repertoires){
            if(!days.contains(r.getId().getDay())) days.add(r.getId().getDay());
        }
        model.addAttribute("days",days);
        int year= Integer.parseInt(day.substring(0,4));
        int month= Integer.parseInt(day.substring(5,7));
        int dday= Integer.parseInt(day.substring(8,10));
        model.addAttribute("url1","/buyticket/"+name);
        model.addAttribute("times",repertoireRepository.findTimesByDay(name,dday,month,year));
        model.addAttribute("url","/buyticket/"+name+"/"+day);
        model.addAttribute("name",name);
        int roomId = repertoireRepository.findRoomId(name,dday,month,year,t);
        List <Integer> busyPlaces = reservationService.findPlaces(name,dday,month,year,t,roomId);
        List <Integer> availablePlaces = new ArrayList<>();
        for(int i=1; i <=30; i++){
            if(!busyPlaces.contains(i)) availablePlaces.add(i);
        }
        model.addAttribute("places",availablePlaces);
        model.addAttribute("successMessage","places");
        model.addAttribute("url2","/buyticket/"+name+"/"+day+"/"+time);
        return "buyticket";
    }

    @RequestMapping(value="/buyticket/{name}/{day}/{time}/{place}", method = RequestMethod.GET)
    public String makeReservation(@PathVariable("name") String name,
                           @PathVariable("day") String day,
                           @PathVariable("time") String time,
                           @PathVariable("place") int place,
                           @RequestParam(value = "login", required = true) String login,
                           Model model){
        model.addAttribute("successMessage","buy");
        Time t = Time.valueOf(time);
        List <Repertoire> repertoires = repertoireRepository.findByMoviename(name);
        List <Date> days = new ArrayList<>();
        for(Repertoire r: repertoires){
            if(!days.contains(r.getId().getDay())) days.add(r.getId().getDay());
        }
        model.addAttribute("days",days);
        int year= Integer.parseInt(day.substring(0,4));
        int month= Integer.parseInt(day.substring(5,7));
        int dday= Integer.parseInt(day.substring(8,10));
        model.addAttribute("url1","/buyticket/"+name);
        model.addAttribute("times",repertoireRepository.findTimesByDay(name,dday,month,year));
        model.addAttribute("url","/buyticket/"+name+"/"+day);
        model.addAttribute("name",name);
        int roomId = repertoireRepository.findRoomId(name,dday,month,year,t);
        List <Integer> busyPlaces = reservationService.findPlaces(name,dday,month,year,t,roomId);
        List <Integer> availablePlaces = new ArrayList<>();
        for(int i=1; i <=30; i++){
            if(!busyPlaces.contains(i)) availablePlaces.add(i);
        }
        model.addAttribute("places",availablePlaces);
        model.addAttribute("url2","/buyticket/"+name+"/"+day+"/"+time);
        return "buyticket";
    }

    @RequestMapping(value="/buyticket/{name}/{day}/{time}/{place}", method = RequestMethod.POST)
    public String buyTicket(@PathVariable("name") String name,
                                  @PathVariable("day") String day,
                                  @PathVariable("time") String time,
                                  @PathVariable("place") int place,
                                  @RequestParam(value = "login", required = true) String login,
                                  Model model, final RedirectAttributes redirectAttributes){
        if (login==null) {
            return "buyticket";
        }
        else{
            Reservation reservation = new Reservation();
            Time t = Time.valueOf(time);
            int year = Integer.parseInt(day.substring(0,4));
            int month = Integer.parseInt(day.substring(5,7));
            int dday = Integer.parseInt(day.substring(8,10));
            reservation.setRoom(repertoireRepository.findRoomId(name,dday,month,year,t));
            reservation.setMoviename(name);
            reservation.setPlace(place);
            Date d = Date.valueOf(day);
            reservation.setDay(d);
            reservation.setTime(t);
            if(login.equals("yes")){
                PriceList priceList = priceListRepository.findByMoviename(reservation.getMoviename());
                double price = priceList.getPrice();
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                User user = userService.findUserByEmail(auth.getName());
                reservationService.saveReservation(reservation);
                UserTicket userTicket = new UserTicket();
                userTicket.setPrice(price);
                userTicket.setTicketid(reservation.getId());
                userTicket.setUserid(user.getId());
                userTicketRepository.save(userTicket);
                int premium = (int) price/8;
                user.setPremium(premium);
                userService.updatePremium(user.getId());
                model.addAttribute("successMessage", "success");
                model.addAttribute("successMessage1", "Reservation has been registered successfully.Number of ticket "+ reservation.getId()+". You received "+premium+" premium points");
            }
            else{
                redirectAttributes.addFlashAttribute("reservation", reservation);
                return "redirect:/buyticketdata";
            }
        }
        return "buyticket";
    }

    @RequestMapping(value = "/buyticketdata",method = RequestMethod.GET)
    public String getGuestData(@ModelAttribute("reservation") final Reservation reservation, Model model){
        GuestTicket guestTicket = new GuestTicket();
        model.addAttribute("reservation", reservation);
        model.addAttribute("guestTicket",guestTicket);
        model.addAttribute("successMessage","null");
        return "buyticketdata";
    }

    @RequestMapping(value = "/buyticketdata",method = RequestMethod.POST)
    public ModelAndView saveGuestData(@Valid GuestTicket guestTicket, BindingResult bindingResult, @ModelAttribute("reservation") final Reservation reservation){
        ModelAndView modelAndView = new ModelAndView();
        if (bindingResult.hasErrors()) {
            modelAndView.addObject("successMessage", "null");
            modelAndView.setViewName("buyticketdata");
        }
        else{
            reservationService.saveReservation(reservation);
            PriceList priceList = priceListRepository.findByMoviename(reservation.getMoviename());
            double price = priceList.getPrice();
            guestTicket.setTicketid(reservation.getId());
            guestTicket.setPrice(price);
            guestTicketRepository.save(guestTicket);
            modelAndView.addObject("successMessage", "Reservation has been registered successfully.Number of ticket "+ reservation.getId()+".");
        }
        return modelAndView;
    }
}
