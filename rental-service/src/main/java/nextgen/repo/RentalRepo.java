package nextgen.repo;

import org.springframework.data.repository.PagingAndSortingRepository;

import nextgen.model.Rental;

public interface RentalRepo extends PagingAndSortingRepository<Rental, Long>{

}
