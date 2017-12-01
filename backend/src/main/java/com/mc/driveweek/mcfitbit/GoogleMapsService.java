package com.mc.driveweek.mcfitbit;

import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;
import org.springframework.stereotype.Service;

@Service
class GoogleMapsService {
  private String apiKey;

  GoogleMapsService(String apiKey) {
    this.apiKey = apiKey;
  }

  DirectionsResult getDirections(String origin, String destination) throws Exception {
    GeoApiContext context = new GeoApiContext.Builder()
        .apiKey(apiKey)
        .build();

    DirectionsApiRequest directions = DirectionsApi
        .getDirections(context, origin, destination)
        .mode(TravelMode.WALKING);

    return directions.await();
  }
}
