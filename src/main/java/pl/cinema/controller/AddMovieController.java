package pl.cinema.controller;

import pl.cinema.repository.MovieRepository;
import pl.cinema.model.Movie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AddMovieController {
    @Autowired
    MovieRepository movieRepo;
    @RequestMapping(value = "/addmovie", method = RequestMethod.GET)
    public String greeting(@RequestParam(value="name", required=false,defaultValue = "$") String name,
                           @RequestParam(value="premiera", required=false) String premiera,
                           @RequestParam(value="czas", required=false) String czas,
                           @RequestParam(value="napisy", required=false, defaultValue="brak") String napisy,
                           @RequestParam(value="jakosc", required=false, defaultValue="2D") String jakosc,

                           @RequestParam(value="gatunek", required=false) String gatunek,
                           @RequestParam(value="produkcja", required=false) String produkcja,
                           @RequestParam(value="ocena", required=false) String ocena,
                           Model model) {
        String findmovieId="";

        if(!name.equals("$")) {
            if(premiera.isEmpty()||czas.isEmpty()||gatunek.isEmpty()||produkcja.isEmpty()||ocena.isEmpty())
            {
                model.addAttribute("error","Wszystkie pola maja być uzupelnine");
                return "addmovie";
            }
            try {
                try {
                    Movie findmovie = movieRepo.findByName(name);
                    findmovieId = String.valueOf(findmovie.getId());
                } catch (Exception ex) {
                    System.out.println("Blad wyszukiwania");
                }
                if(findmovieId.isEmpty())
                {
                    model.addAttribute("error","Dodany");
                    Movie movie = new Movie();
                    movie.setName(name);
                    movie.setPremiere(premiera);
                    movie.setDuration(czas);
                    movie.setSubtitles(napisy);
                    movie.setTd(jakosc);
                    movie.setType(gatunek);
                    movie.setProduction(produkcja);
                    movie.setRating(Double.parseDouble(ocena.replaceAll(" ",".")));
                    movieRepo.save(movie);
                }
                else
                {
                    model.addAttribute("error","Taki film istnieje");
                }
            } catch (Exception ex) {
                System.out.println("Nie Dodalem");
            }
        }
//        model.addAttribute("error","Taki film istnieje");
        return "addmovie";
    }
}