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
 * @interface MemberRegisterRequestDto
 */
export interface MemberRegisterRequestDto {
    /**
     * 
     * @type {number}
     * @memberof MemberRegisterRequestDto
     */
    kakaoId: number;
    /**
     * 
     * @type {string}
     * @memberof MemberRegisterRequestDto
     */
    email: string;
    /**
     * 
     * @type {string}
     * @memberof MemberRegisterRequestDto
     */
    nickname: string;
    /**
     * 
     * @type {string}
     * @memberof MemberRegisterRequestDto
     */
    gender: string;
    /**
     * 
     * @type {number}
     * @memberof MemberRegisterRequestDto
     */
    age: number;
    /**
     * 
     * @type {number}
     * @memberof MemberRegisterRequestDto
     */
    height: number;
    /**
     * 
     * @type {number}
     * @memberof MemberRegisterRequestDto
     */
    latitude?: number;
    /**
     * 
     * @type {number}
     * @memberof MemberRegisterRequestDto
     */
    longitude?: number;
}

/**
 * Check if a given object implements the MemberRegisterRequestDto interface.
 */
export function instanceOfMemberRegisterRequestDto(value: object): value is MemberRegisterRequestDto {
    if (!('kakaoId' in value) || value['kakaoId'] === undefined) return false;
    if (!('email' in value) || value['email'] === undefined) return false;
    if (!('nickname' in value) || value['nickname'] === undefined) return false;
    if (!('gender' in value) || value['gender'] === undefined) return false;
    if (!('age' in value) || value['age'] === undefined) return false;
    if (!('height' in value) || value['height'] === undefined) return false;
    return true;
}

export function MemberRegisterRequestDtoFromJSON(json: any): MemberRegisterRequestDto {
    return MemberRegisterRequestDtoFromJSONTyped(json, false);
}

export function MemberRegisterRequestDtoFromJSONTyped(json: any, ignoreDiscriminator: boolean): MemberRegisterRequestDto {
    if (json == null) {
        return json;
    }
    return {
        
        'kakaoId': json['kakaoId'],
        'email': json['email'],
        'nickname': json['nickname'],
        'gender': json['gender'],
        'age': json['age'],
        'height': json['height'],
        'latitude': json['latitude'] == null ? undefined : json['latitude'],
        'longitude': json['longitude'] == null ? undefined : json['longitude'],
    };
}

export function MemberRegisterRequestDtoToJSON(json: any): MemberRegisterRequestDto {
    return MemberRegisterRequestDtoToJSONTyped(json, false);
}

export function MemberRegisterRequestDtoToJSONTyped(value?: MemberRegisterRequestDto | null, ignoreDiscriminator: boolean = false): any {
    if (value == null) {
        return value;
    }

    return {
        
        'kakaoId': value['kakaoId'],
        'email': value['email'],
        'nickname': value['nickname'],
        'gender': value['gender'],
        'age': value['age'],
        'height': value['height'],
        'latitude': value['latitude'],
        'longitude': value['longitude'],
    };
}

