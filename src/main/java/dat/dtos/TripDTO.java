// TripDTO.java
package dat.dtos;

import dat.entities.Trip;
import dat.enums.TripCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class TripDTO {
    private Long id;
    private LocalDateTime starttime;
    private LocalDateTime endtime;
    private String startposition;
    private String name;
    private double price;
    private TripCategory category;
    private GuideDTO guide;

    public TripDTO(Long id, LocalDateTime starttime, LocalDateTime endtime, String startposition, String name, double price, TripCategory category, GuideDTO guide) {
        this.id = id;
        this.starttime = starttime;
        this.endtime = endtime;
        this.startposition = startposition;
        this.name = name;
        this.price = price;
        this.category = category;
        this.guide = guide;
    }

    public TripDTO(Trip trip) {
        this.id = trip.getId();
        this.starttime = trip.getStarttime();
        this.endtime = trip.getEndtime();
        this.startposition = trip.getStartposition();
        this.name = trip.getName();
        this.price = trip.getPrice();
        this.category = trip.getCategory();
        this.guide = new GuideDTO(trip.getGuide());
    }

    public LocalDateTime getStartTime() {
        return starttime;
    }

    public LocalDateTime getEndTime() {
        return endtime;
    }

    public String getStartPosition() {
        return startposition;
    }
}
