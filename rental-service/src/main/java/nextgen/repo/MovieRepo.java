package nextgen.repo;

import org.springframework.data.repository.PagingAndSortingRepository;

import nextgen.model.Customer;
import nextgen.model.Movie;
import nextgen.model.Rental;

public interface MovieRepo extends PagingAndSortingRepository<Movie, Long>{

}
