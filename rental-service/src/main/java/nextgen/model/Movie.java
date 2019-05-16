package nextgen.model;

import java.time.Year;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class Movie extends AbstractBaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String title;
	private Integer year;
	private String mainActor;
	
	@OneToMany(mappedBy="movie")
	@JsonBackReference
	private Set<Rental> rentals;
	
	
	public Set<Rental> getRentals() {
		return rentals;
	}
	public void setRentals(Set<Rental> rentals) {
		this.rentals = rentals;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public String getMainActor() {
		return mainActor;
	}
	public void setMainActor(String mainActor) {
		this.mainActor = mainActor;
	}
	
	

}
