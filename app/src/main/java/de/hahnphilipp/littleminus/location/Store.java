package de.hahnphilipp.littleminus.location;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Store {
    private String storeKey;
    private String name;
    private String address;
    private String postalCode;
    private String locality;
    private double latitude;
    private double longitude;
}
