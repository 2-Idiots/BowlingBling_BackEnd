package com.capstone.bowlingbling.domain.place.domain;

import com.capstone.bowlingbling.global.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "place")
public class Place extends BaseEntity {

    @Column(name = "address_name", nullable = false)
    private String addressName;  // 일반 주소명

    @Column(name = "road_address_name", nullable = false)
    private String roadAddressName;  // 도로명 주소명

    @Column(name = "building_name")
    private String buildingName;  // 건물 이름

    @Column(name = "zone_no")
    private String zoneNo;  // 우편번호

    @Column(name = "latitude")
    private double latitude;  // 위도 (소수점 6자리까지 저장)

    @Column(name = "longitude")
    private double longitude;  // 경도 (소수점 6자리까지 저장)
}
