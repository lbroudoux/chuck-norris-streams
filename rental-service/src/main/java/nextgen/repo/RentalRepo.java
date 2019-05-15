package nextgen.repo;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import nextgen.model.Rental;

@RepositoryRestResource
public interface RentalRepo extends PagingAndSortingRepository<Rental, Long>{

}
