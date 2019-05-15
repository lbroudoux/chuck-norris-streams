package nextgen.processor;


import java.util.Optional;

import org.apache.camel.Body;
import org.apache.camel.language.Simple;
import org.springframework.stereotype.Component;

import nextgen.apimodel.RentalEdit;
import nextgen.model.Customer;
import nextgen.model.Movie;
import nextgen.model.Rental;

@Component
public class RentalCRUD extends AbstractCRUD{
	
	public Iterable<Rental> findAll() {
		return rentalRepo.findAll();
	}
	
	public Optional<Rental> findById( @Simple("headers.id") Long id) {
		return rentalRepo.findById(id);
	}
	
	public Rental save(@Body RentalEdit rentalEdit) {
		
		Long movieId =  rentalEdit.getMovieId(); 
		Long customerId = rentalEdit.getCustomerId(); 
		Movie movie = movieRepo.findById(movieId).get();
		Customer customer = customerRepo.findById(customerId).get();
		
		Rental rental = new Rental();
		rental.setRentalDuration(rentalEdit.getRentalDuration());
		rental.setStartDate(rentalEdit.getStartDate());
		rental.setCustomer(customer);
		rental.setMovie(movie);
		
		rentalRepo.save(rental);
		
		return rental;
	}
}
