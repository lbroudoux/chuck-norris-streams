package nextgen.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import nextgen.model.Customer;
import nextgen.model.Movie;
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
        model.addAttribute("movies",movieRepo.findAll());
        return "index";
    }
    
    @GetMapping("/rent/{id}")
    public String rent(HttpSession session,Model model,@PathVariable("id") Long id) {
    	Rental rental = new Rental();
    	Customer customer = (Customer) session.getAttribute("customer");
    	
    	rental.setStartDate(new Date());
    	rental.setRentalDuration(7);
    	Movie movie = movieRepo.findById(id).get();
    	rental.setCustomer(customer);
    	rental.setMovie(movie);
    	rentalRepo.save(rental);
    	model.addAttribute("rentals", rentalRepo.findByCustomerId(customer.getId()));
		return "rented";
    	
    }
    
    @GetMapping("/rent")
    public String rentHistory(HttpSession session,Model model) {
    	
    	Customer customer = (Customer) session.getAttribute("customer");
    	
    	
    	model.addAttribute("rentals", rentalRepo.findByCustomerId(customer.getId()));
    	
    	
		return "rented";
    	
    }
    
    @GetMapping("/login")
    public String login(HttpSession session,Model model) {
    	if (session.getAttribute("customer") == null) {
    		System.out.println("not logged in");
    		model.addAttribute("customer",new Customer());
    	}
    	else
    	{
    		System.out.println("logged in as " + session.getAttribute("customer"));
    	}
        return "login";
    }
    
    @PostMapping("/login")
    public String loginSubmit(HttpSession session,@ModelAttribute(value="customer")Customer customerInput) {
        
    	Customer c = customerRepo.findByFirstName(customerInput.getFirstName());
    	if (c != null) {
    		session.setAttribute("customer", c);
    	}
    	
    	return "login";
    }
    
    @GetMapping("/logout")
    private String logout(HttpSession session) {
    	
    	session.invalidate();
    	
    	return "logout";
    }
    
}
