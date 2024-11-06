package dat.entities;

import jakarta.persistence.*;
import dat.dtos.TripDTO;
import dat.enums.TripCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String startPosition;
    private String name;
    private double price;

    @Enumerated(EnumType.STRING)
    private TripCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    private Guide guide;

    public Trip(TripDTO dto) {
        this.startTime = dto.getStartTime();
        this.endTime = dto.getEndTime();
        this.startPosition = dto.getStartPosition();
        this.name = dto.getName();
        this.price = dto.getPrice();
        this.category = dto.getCategory();
    }

    public Trip(LocalDateTime startTime, LocalDateTime endTime, String startPosition, String name, double price, TripCategory category, Guide guide) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.startPosition = startPosition;
        this.name = name;
        this.price = price;
        this.category = category;
        this.guide = guide;
    }
}
