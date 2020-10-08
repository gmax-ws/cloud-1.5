package scalable.solutions.oborder.dto;

public class OrderDTO {

    private String id;

    private String description;

    public OrderDTO() {

    }

    public OrderDTO(String id, String description) {
        this.id = id;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
