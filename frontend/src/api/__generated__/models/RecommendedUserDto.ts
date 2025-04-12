/* tslint:disable */
/* eslint-disable */
/**
 * 4차 프로젝트 6팀 March API
 * 6팀 March의 백엔드 API 명세서입니다.
 *
 * The version of the OpenAPI document: v1.0.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

import { mapValues } from '../runtime';
/**
 * 
 * @export
 * @interface RecommendedUserDto
 */
export interface RecommendedUserDto {
    /**
     * 
     * @type {number}
     * @memberof RecommendedUserDto
     */
    id?: number;
    /**
     * 
     * @type {string}
     * @memberof RecommendedUserDto
     */
    nickname?: string;
    /**
     * 
     * @type {number}
     * @memberof RecommendedUserDto
     */
    latitude?: number;
    /**
     * 
     * @type {number}
     * @memberof RecommendedUserDto
     */
    longitude?: number;
}

/**
 * Check if a given object implements the RecommendedUserDto interface.
 */
export function instanceOfRecommendedUserDto(value: object): value is RecommendedUserDto {
    return true;
}

export function RecommendedUserDtoFromJSON(json: any): RecommendedUserDto {
    return RecommendedUserDtoFromJSONTyped(json, false);
}

export function RecommendedUserDtoFromJSONTyped(json: any, ignoreDiscriminator: boolean): RecommendedUserDto {
    if (json == null) {
        return json;
    }
    return {
        
        'id': json['id'] == null ? undefined : json['id'],
        'nickname': json['nickname'] == null ? undefined : json['nickname'],
        'latitude': json['latitude'] == null ? undefined : json['latitude'],
        'longitude': json['longitude'] == null ? undefined : json['longitude'],
    };
}

export function RecommendedUserDtoToJSON(json: any): RecommendedUserDto {
    return RecommendedUserDtoToJSONTyped(json, false);
}

export function RecommendedUserDtoToJSONTyped(value?: RecommendedUserDto | null, ignoreDiscriminator: boolean = false): any {
    if (value == null) {
        return value;
    }

    return {
        
        'id': value['id'],
        'nickname': value['nickname'],
        'latitude': value['latitude'],
        'longitude': value['longitude'],
    };
}

