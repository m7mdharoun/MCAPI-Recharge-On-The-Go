package com.mc.driveweek.mcfitbit;

import com.mastercard.api.core.model.RequestMap;
import com.mastercard.api.places.MerchantPointOfInterest;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

@Service
class PlacesService {

  private static final double EARTH_RADIUS = 6378.1370D;


  Map<String, Object> getPlaces(Double lat, Double lng, Integer offset, Integer size, String merchantName, String industry) throws Exception {

    Map<String, String> place = new HashMap<>();
    place.put("latitude", lat.toString());
    place.put("longitude", lng.toString());
    place.put("countryCode", "USA");
    place.put("industry", industry);
    place.put("primaryChannelOfDistribution", "B");
    place.put("nfcFlag", "true");

    if (!StringUtils.isEmpty(merchantName)) {
      place.put("merchantName", merchantName);
    }

    RequestMap map = new RequestMap();
    map.put("place", place);
    map.put("radiusSearch", "true");
    map.put("unit", "km");
    map.put("pageOffset", offset);
    map.put("pageLength", size);
    map.put("distance", "1");

    MerchantPointOfInterest pointOfInterest = MerchantPointOfInterest.create(map);
    JSONArray places = (JSONArray) pointOfInterest.get("MerchantPOIResponse.places.place");

    Object placesList =  places.stream()
        .map(p -> buildSimplePlaceMap((JSONObject) p, lat, lng))
        .collect(Collectors.toList());

    Map<String, Object> responseMap = new HashMap<>();
    responseMap.put("data", placesList);
    return responseMap;
  }

  private static long distance(double startLat, double startLong, double endLat, double endLong) {
    double dLat  = Math.toRadians(endLat - startLat);
    double dLong = Math.toRadians(endLong - startLong);

    startLat = Math.toRadians(startLat);
    endLat   = Math.toRadians(endLat);

    double a = haversin(dLat) + Math.cos(startLat) * Math.cos(endLat) * haversin(dLong);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    return Math.round(EARTH_RADIUS * c * 1000);
  }

  private static double haversin(double val) {
    return Math.pow(Math.sin(val / 2), 2);  }


  private Map<String, Object> buildSimplePlaceMap(JSONObject object, Double originLat, Double originLng) {
    long distance = distance(originLat, originLng, o2d(object.get("latitude")), o2d(object.get("longitude")));

    Map<String, Object> map = new HashMap<>();
    map.put("latlng",   object.get("latitude") + "," + object.get("longitude"));
    map.put("name", WordUtils.capitalize(object.get("merchantName").toString().toLowerCase()));
    map.put("addr", object.get("cleansedStreetAddr").toString());
    map.put("dist", distance);
    return map;

  }

  private static final double o2d(Object o) {
    return Double.valueOf(o.toString());
  }
}
