package nextgen.processor;

import org.springframework.beans.factory.annotation.Autowired;

import nextgen.repo.CustomerRepo;
import nextgen.repo.MovieRepo;
import nextgen.repo.RentalRepo;

public abstract class AbstractCRUD {
	@Autowired
	CustomerRepo customerRepo;
	
	@Autowired
	MovieRepo movieRepo; 
	
	@Autowired
	RentalRepo rentalRepo;
}
