package dat.daos;

import dat.dtos.TripDTO;

import java.util.Set;

public abstract class ITripGuideDAO {
    public abstract void addGuideToTrip(int tripId, int guideId);

    public abstract Set<TripDTO> getTripsByGuide(int guideId);

    public void populateDatabase() {

    }
}
