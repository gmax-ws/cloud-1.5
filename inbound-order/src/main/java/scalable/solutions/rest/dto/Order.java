package scalable.solutions.rest.dto;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "orders")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String description;

	public Order() {

	}

	public Order(long id) {
		this.id = id;
	}

	public Order(String description) {
		this.description = description;
	}

	// Getter and setter methods

	public long getId() {
		return id;
	}

	public void setId(long value) {
		this.id = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

} // class Order
