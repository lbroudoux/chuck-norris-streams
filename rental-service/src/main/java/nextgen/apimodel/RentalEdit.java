package nextgen.apimodel;

import java.util.Date;

import javax.persistence.ManyToOne;

import nextgen.model.Customer;
import nextgen.model.Movie;

public class RentalEdit {

	private Date startDate;
	private Integer rentalDuration;
	
	private Long movieId;
	
	private Long customerId;

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Integer getRentalDuration() {
		return rentalDuration;
	}

	public void setRentalDuration(Integer rentalDuration) {
		this.rentalDuration = rentalDuration;
	}

	public Long getMovieId() {
		return movieId;
	}

	public void setMovieId(Long movieId) {
		this.movieId = movieId;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}
	
	
}
