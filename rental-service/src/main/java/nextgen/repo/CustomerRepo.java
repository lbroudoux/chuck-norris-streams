package nextgen.repo;

import org.springframework.data.repository.PagingAndSortingRepository;

import nextgen.model.Customer;

public interface CustomerRepo extends PagingAndSortingRepository<Customer, Long>{

}
