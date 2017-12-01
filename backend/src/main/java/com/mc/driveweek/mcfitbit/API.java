package com.mc.driveweek.mcfitbit;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class API {

  @Autowired
  PlacesService placesService;

  @Autowired
  GoogleMapsService googleMapsService;

  @Autowired
  GoogleStaticMapsService googleStaticMapsService;

  @RequestMapping(path = "/places")
  public ResponseEntity<Object> getPlaces(
      @RequestParam(value = "lat") Double latitude,
      @RequestParam(value = "lng") Double longitude,
      @RequestParam(value = "size", defaultValue = "6") Integer size,
      @RequestParam(value = "offset", defaultValue = "0") Integer offset,
      @RequestParam(value = "merchantName", required = false) String merchantName,
      @RequestParam(value = "industry") String industry
  ) throws Exception {

    return ResponseEntity.ok(placesService.getPlaces(latitude, longitude, offset, size, merchantName, industry));
  }

  @RequestMapping(path = "/map")
  public void getMap(@RequestParam String origin,
      @RequestParam(required = false) String destination,
      @RequestParam(required = false) String industry,
      HttpServletResponse response) throws Exception {
    InputStream imageStream;

    if (StringUtils.isEmpty(destination)) {
      Map<String, String> map = getDestination(origin, industry);
      destination = map.get("latlng");

      // Response headers for extra meta-data
      response.addHeader("x-merchant-name", map.get("name"));
      response.addHeader("x-merchant-dist", map.get("dist"));
    }

    imageStream = googleStaticMapsService.getImageStream(origin, destination, googleMapsService.getDirections(origin, destination));

    // Prevent chunking, seems the fitbit doesn't support it
    byte[] imageBytes = IOUtils.toByteArray(imageStream);
    response.setContentType("image/jpeg");
    response.setContentLength(imageBytes.length);

    IOUtils.copy(googleStaticMapsService.getImageStream(origin, destination,
        googleMapsService.getDirections(origin, destination)),
        response.getOutputStream());
  }

  private Map<String, String> getDestination(String origin, String industry) throws Exception {
    String[] coordinates = origin.split(",");
    Map<String, Object> placesResp = placesService.getPlaces(
        Double.valueOf(coordinates[0]), Double.valueOf(coordinates[1]),
        0, 2, null, industry);

    List<Map<String, Object>> data = (List<Map<String, Object>>) placesResp.get("data");
    Map<String, Object> place = data.get(0);
    HashMap<String, String> map = new HashMap<>();
    map.put("latlng", (String) place.get("latlng"));
    map.put("name", (String) place.get("name"));
    map.put("dist",  place.get("dist").toString());
    return  map;
  }
}
