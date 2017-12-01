package com.mc.driveweek.mcfitbit;

import com.google.maps.model.DirectionsResult;
import com.google.maps.model.LatLng;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
class GoogleStaticMapsService {
  private static final String baseUrl = "https://maps.googleapis.com/maps/api/staticmap";
  private String apiKey;

  GoogleStaticMapsService(String apiKey) {
    this.apiKey = apiKey;
  }

  InputStream getImageStream(String origin, String destination, DirectionsResult directions) throws IOException {
    String encodedPolyLine = directions.routes[0].overviewPolyline.getEncodedPath();
    LatLng center = midPoint(directions.routes[0].bounds.northeast, directions.routes[0].bounds.southwest);

    UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
        .fromUriString(baseUrl)
        .queryParam("center", center)
        .queryParam("size", "348x250")
        .queryParam("maptype", "roadmap")
        .queryParam("markers", "icon:https://goo.gl/utvgJv%7C" + origin)
        .queryParam("markers", "icon:https://goo.gl/Nac8jS%7C" + destination)
        .queryParam("path", "weight:6|color:0xFFFFFF|enc:" + encodedPolyLine)
        .queryParam("format", "jpg-baseline")
        .queryParam("style", "feature:all%7Celement:labels%7Cvisibility:off")
        .queryParam("style", "feature:all%7Celement:labels.text.stroke%7Cinvert_lightness:true")
        .queryParam("style", "feature:all%7Celement:labels.text.fill%7Ccolor:0xffffff")
        .queryParam("style", "feature:landscape%7Celement:geometry%7Ccolor:0x2c5a71")
        .queryParam("style", "feature:transit%7Celement:all%7Cvisibility:off")
        .queryParam("style", "feature:water%7Celement:geometry%7Ccolor:0x193341")
        .queryParam("style", "feature:road%7Celement:geometry%7Ccolor:0x06242c")
        .queryParam("style", "feature:poi%7Celement:geometry%7Ccolor:0x2E7F51")
        .queryParam("style", "feature:road%7Celement:labels.text%7Cvisibility:on")
        .queryParam("key", apiKey);

    String uri = uriComponentsBuilder.build().toUriString();
    return new URL(uri).openConnection().getInputStream();
  }

  private static LatLng midPoint(LatLng northeast, LatLng southwest){
    double latitudeCenter = (southwest.lat + northeast.lat) / 2D;
    double longitudeCenter;
    if (southwest.lng <= northeast.lng) {
      longitudeCenter = (northeast.lng + southwest.lng) / 2D;
    }
    else {
      longitudeCenter = (northeast.lng + 360D + southwest.lng) / 2D;
    }
    return new LatLng(latitudeCenter, longitudeCenter);
  }
}
