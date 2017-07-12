package pl.cinema.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import pl.cinema.model.Movie;
import pl.cinema.repository.MovieRepository;

@Controller
public class AdminController {
    @Autowired
    private MovieRepository movieRepo;

    @RequestMapping(value="/admin")
    public String admin(){
        return "admin";
    }

    @RequestMapping(value = "/admin/addmovie", method = RequestMethod.POST)
    public String Postmovie(Model model)
    {
        return "admin";
    }

    @RequestMapping(value = "/admin/addmovie", method = RequestMethod.GET)
    public String addmovie(@RequestParam(value="name", required=false,defaultValue = "$") String name,
                           @RequestParam(value="premiera", required=false) String premiera,
                           @RequestParam(value="czas", required=false) String czas,
                           @RequestParam(value="napisy", required=false) String napisy,
                           @RequestParam(value="jakosc", required=false) String jakosc,
                           @RequestParam(value="rezyser", required=false) String rezyser,
                           @RequestParam(value="gatunek", required=false) String gatunek,
                           @RequestParam(value="produkcja", required=false) String produkcja,
                           @RequestParam(value="ocena", required=false) String ocena,
                           Model model) {
        String findmovieId="";

        if(!name.equals("$")) {
            if(premiera.isEmpty()||czas.isEmpty()||rezyser.isEmpty()||napisy.isEmpty()||produkcja.isEmpty()||ocena.isEmpty())
            {
                model.addAttribute("error","Wszystkie pola maja być uzupelnine");
                return "admin/addmovie";
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
                    movie.setDirector(rezyser);
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
        return "admin/addmovie";
    }
    @RequestMapping(value = "/admin/editMovie", method = RequestMethod.GET)
//    public String edit(@RequestParam("name") String name, Model model) {
    public String edit(@RequestParam(value="name", required=false) String name,
                       @RequestParam(value="premiera", required=false) String premiera,
                       @RequestParam(value="czas", required=false) String czas,
                       @RequestParam(value="napisy", required=false) String napisy,
                       @RequestParam(value="jakosc", required=false) String jakosc,
                       @RequestParam(value="rezyser", required=false) String rezyser,
                       @RequestParam(value="gatunek", required=false) String gatunek,
                       @RequestParam(value="produkcja", required=false) String produkcja,
                       @RequestParam(value="ocena", required=false) String ocena,
                       @RequestParam(value="update", required=false,defaultValue = "First") String updat,
                       Model model) {
        try {
            Movie findmovie = movieRepo.findByName(name);
            model.addAttribute("editMovie", findmovie);
            if(updat.equals("update"))
            {
                model.addAttribute("hello", "User succesfully deleted!");
                findmovie.setName(name);
                findmovie.setPremiere(premiera);
                findmovie.setDuration(czas);
                findmovie.setSubtitles(napisy);
                findmovie.setTd(jakosc);
                findmovie.setDirector(rezyser);
                findmovie.setType(gatunek);
                findmovie.setProduction(produkcja);
                findmovie.setRating(Double.parseDouble(ocena.replaceAll(" ", ".")));
                movieRepo.save(findmovie);
            }
//            movieRepo.delete(findmovie);
        }
        catch (Exception ex) {
            return "Error deleting the user:" + ex.toString();
        }


        return "admin/editMovie";
    }
    @RequestMapping(value = "/admin/deleteMovie", method = RequestMethod.GET)
    public String Delete(@RequestParam("name") String name, Model model) {
        try {
            Movie findmovie = movieRepo.findByName(name);
            movieRepo.delete(findmovie);
        }
        catch (Exception ex) {
            return "Error deleting the user:" + ex.toString();
        }

        model.addAttribute("hello", "User succesfully deleted!");
        return "admin/deleteMovie";
    }
}
