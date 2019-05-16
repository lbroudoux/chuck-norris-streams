package nextgen.repo;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import nextgen.model.Rental;

public interface RentalRepo extends PagingAndSortingRepository<Rental, Long>{

	Iterable<Rental> findByCustomerId(@Param(value = "id")Long id);
}
