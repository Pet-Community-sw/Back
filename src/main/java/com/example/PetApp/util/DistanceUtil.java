package com.example.PetApp.util;

import java.util.List;

public class DistanceUtil {
    public static Double calculateTotalDistance(List<String> list) {
        double totalDistance=0.0;
        for (int i = 1; i < list.size(); i++) {
            String path1 = list.get(i - 1);
            String path2 = list.get(i);

            String[] arr1 = path1.split(",");
            String[] arr2 = path2.split(",");

            double longitude1 = Double.parseDouble(arr1[0]);
            double latitude1 = Double.parseDouble(arr1[1]);
            double longitude2 = Double.parseDouble(arr2[0]);
            double latitude2 = Double.parseDouble(arr2[1]);
            double distanceInMeters = HaversineUtil.calculateDistanceInMeters(latitude1, longitude1, latitude2, longitude2);
            totalDistance += distanceInMeters;
        }
        return totalDistance;
    }
}
