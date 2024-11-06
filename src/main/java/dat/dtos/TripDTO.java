package dat.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import dat.entities.Trip;
import dat.enums.TripCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TripDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("startTime")
    private LocalDateTime startTime;

    @JsonProperty("endTime")
    private LocalDateTime endTime;

    @JsonProperty("startPosition")
    private String startPosition;

    @JsonProperty("name")
    private String name;

    @JsonProperty("price")
    private double price;

    @JsonProperty("category")
    private TripCategory category;

    @JsonProperty("guide")
    private GuideDTO guide;

    public TripDTO(Long id, LocalDateTime startTime, LocalDateTime endTime, String startPosition, String name, double price, TripCategory category, GuideDTO guide) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startPosition = startPosition;
        this.name = name;
        this.price = price;
        this.category = category;
        this.guide = guide;
    }

    public TripDTO(Trip trip) {
        this.id = trip.getId();
        this.startTime = trip.getStartTime();
        this.endTime = trip.getEndTime();
        this.startPosition = trip.getStartPosition();
        this.name = trip.getName();
        this.price = trip.getPrice();
        this.category = trip.getCategory();
        this.guide = trip.getGuide() != null ? new GuideDTO(trip.getGuide()) : null; // Null-check added here
    }
}
