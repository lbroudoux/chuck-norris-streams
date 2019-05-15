package nextgen.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import nextgen.apimodel.RentalEdit;
import nextgen.model.Customer;
import nextgen.model.Movie;
import nextgen.model.Rental;
import nextgen.repo.CustomerRepo;
import nextgen.repo.MovieRepo;
import nextgen.repo.RentalRepo;


@Component
public class RentalPost extends AbstractCRUD implements Processor{

	@Autowired
	CustomerRepo customerRepo;
	
	@Autowired
	MovieRepo movieRepo; 
	
	@Autowired
	RentalRepo rentalRepo;
	
	@Override
	public void process(Exchange exchange) throws Exception {
		
		RentalEdit rentalApi = exchange.getIn().getBody(RentalEdit.class);
		Long movieId =  rentalApi.getMovieId(); 
		Long customerId = rentalApi.getCustomerId(); 
		Movie movie = movieRepo.findById(movieId).get();
		Customer customer = customerRepo.findById(customerId).get();
		
		Rental rental = new Rental();
		rental.setRentalDuration(rentalApi.getRentalDuration());
		rental.setStartDate(rentalApi.getStartDate());
		rental.setCustomer(customer);
		rental.setMovie(movie);
		
		rentalRepo.save(rental);
		
		exchange.getIn().setBody(rental);
		
	}

}
