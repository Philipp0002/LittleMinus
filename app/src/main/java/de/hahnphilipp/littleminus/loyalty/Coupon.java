package de.hahnphilipp.littleminus.loyalty;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Coupon {
    private String id;
    private String promotionId;
    private String image;
    private String type;

    private String discountTitle;
    private String discountDescription;
    private String discountScope;
    private String title;
    private boolean isActivated;

    private String section;

    private ZonedDateTime validFrom;
    private ZonedDateTime validUntil;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Coupon coupon = (Coupon) o;
        return Objects.equals(id, coupon.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}