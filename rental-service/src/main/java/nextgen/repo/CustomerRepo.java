package nextgen.repo;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import nextgen.model.Customer;

public interface CustomerRepo extends PagingAndSortingRepository<Customer, Long>{
	
	Customer findByFirstName(@Param(value = "firstName")String firstName);
}
