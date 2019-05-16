package nextgen.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import nextgen.model.Rental;
import nextgen.repo.CustomerRepo;
import nextgen.repo.MovieRepo;
import nextgen.repo.RentalRepo;

@Controller
public class AppController {
	
	@Autowired
	CustomerRepo customerRepo;
	
	@Autowired
	MovieRepo movieRepo; 
	
	@Autowired
	RentalRepo rentalRepo;
	
	
    @GetMapping("/")
    public String main(Model model) {
        model.addAttribute("rentals", rentalRepo.findAll());
        model.addAttribute("movies",movieRepo.findAll());
        model.addAttribute("customers",customerRepo.findAll());
        return "index";
    }
    
    @GetMapping("/rent/{id}")
    public String rent(Model model,@PathVariable("id") Long id) {
    	
		return "rented";
    	
    }
}
