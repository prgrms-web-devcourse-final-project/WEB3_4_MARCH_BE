package com.backend.global.redis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RedisGeoService {

    // RedisTemplate을 주입받음. (String 키, String 값 형태)
    private final RedisTemplate<String, String> redisTemplate;

    // Redis에 좌표를 저장할 Key 이름
    // GEOADD, GEORADIUS 등의 명령어는 이 키를 기준으로 동작함
    private final static String GEO_KEY = "members:geo";

    /**
     * 회원의 위치 정보를 Redis GEO에 등록하는 메서드
     * - 회원 가입 시 또는 최초 위치 설정시 사용하도록 설정
     * - 내부적으로 GEOADD 명령어를 사용함.
     *
     * @param memberId Redis에 저장될 사용자 ID
     * @param latitude 위도
     * @param longitude 경도
     */
    public void addLocation(Long memberId, double latitude, double longitude) {

        // Redis GEO는 (경도, 위도) 순서로 Point 객체를 생성함
        // memberId는 키 값으로, 문자열로 변환해서 저장
        redisTemplate.opsForGeo().add(
                GEO_KEY, // GEO 키 이름
                new Point(longitude, latitude), // Redis GEO는 (경도,위도) 순서를 요구
                memberId.toString()); // Redis는 String만 저장하므로 문자열 반환
    }

    /**
     * 특정 위치 기준으로 반경 내 사용자 ID 리스트를 조회
     *
     * - GEORADIUS 명령어 기반
     * - 거리 단위는 km로 설정
     *
     * @param latitude 기준 위도
     * @param longitude 기준 경도
     * @param radiusKm 검색 반경 (km 단위로 설정)
     * @param limit 최대로 조회 할 회원수
     * @return 유저 ID(Long 타입) 리스트
     */
    public List<Long> findNearByUserIds(double latitude, double longitude, double radiusKm, int limit) {

        // 검색할 원형 영역 정의 (중심 좌표와 반경)
        Circle area = new Circle(
                new Point(longitude, latitude),             // Redis GEO는 (경도, 위도) 순서
                new Distance(radiusKm, Metrics.KILOMETERS)  // 거리 단위는 Km
        );

        // 반경 검색 실행 (Redis 명령: GEORADIUS or GEOSEARCH)
        // 제한 인원(limit)도 설정
        GeoResults<RedisGeoCommands.GeoLocation<String>> results =
                redisTemplate.opsForGeo().radius(
                        GEO_KEY,
                        area,
                        RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().limit(Integer.MAX_VALUE)
                );

        if (results == null) return List.of();

        return results.getContent().stream()
                .map(result -> Long.parseLong(result.getContent().getName()))
                .toList();
    }


}
