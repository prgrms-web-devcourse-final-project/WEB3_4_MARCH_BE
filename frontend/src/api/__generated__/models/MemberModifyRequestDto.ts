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
 * @interface MemberModifyRequestDto
 */
export interface MemberModifyRequestDto {
    /**
     * 
     * @type {string}
     * @memberof MemberModifyRequestDto
     */
    nickname: string;
    /**
     * 
     * @type {number}
     * @memberof MemberModifyRequestDto
     */
    age: number;
    /**
     * 
     * @type {number}
     * @memberof MemberModifyRequestDto
     */
    height: number;
    /**
     * 
     * @type {string}
     * @memberof MemberModifyRequestDto
     */
    gender: string;
    /**
     * 
     * @type {number}
     * @memberof MemberModifyRequestDto
     */
    latitude?: number;
    /**
     * 
     * @type {number}
     * @memberof MemberModifyRequestDto
     */
    longitude?: number;
    /**
     * 
     * @type {string}
     * @memberof MemberModifyRequestDto
     */
    introduction?: string;
}

/**
 * Check if a given object implements the MemberModifyRequestDto interface.
 */
export function instanceOfMemberModifyRequestDto(value: object): value is MemberModifyRequestDto {
    if (!('nickname' in value) || value['nickname'] === undefined) return false;
    if (!('age' in value) || value['age'] === undefined) return false;
    if (!('height' in value) || value['height'] === undefined) return false;
    if (!('gender' in value) || value['gender'] === undefined) return false;
    return true;
}

export function MemberModifyRequestDtoFromJSON(json: any): MemberModifyRequestDto {
    return MemberModifyRequestDtoFromJSONTyped(json, false);
}

export function MemberModifyRequestDtoFromJSONTyped(json: any, ignoreDiscriminator: boolean): MemberModifyRequestDto {
    if (json == null) {
        return json;
    }
    return {
        
        'nickname': json['nickname'],
        'age': json['age'],
        'height': json['height'],
        'gender': json['gender'],
        'latitude': json['latitude'] == null ? undefined : json['latitude'],
        'longitude': json['longitude'] == null ? undefined : json['longitude'],
        'introduction': json['introduction'] == null ? undefined : json['introduction'],
    };
}

export function MemberModifyRequestDtoToJSON(json: any): MemberModifyRequestDto {
    return MemberModifyRequestDtoToJSONTyped(json, false);
}

export function MemberModifyRequestDtoToJSONTyped(value?: MemberModifyRequestDto | null, ignoreDiscriminator: boolean = false): any {
    if (value == null) {
        return value;
    }

    return {
        
        'nickname': value['nickname'],
        'age': value['age'],
        'height': value['height'],
        'gender': value['gender'],
        'latitude': value['latitude'],
        'longitude': value['longitude'],
        'introduction': value['introduction'],
    };
}

