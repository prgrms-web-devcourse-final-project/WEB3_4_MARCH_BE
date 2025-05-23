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
 * @interface LoginResponseDto
 */
export interface LoginResponseDto {
    /**
     * 
     * @type {string}
     * @memberof LoginResponseDto
     */
    accessToken?: string;
    /**
     * 
     * @type {string}
     * @memberof LoginResponseDto
     */
    kakaoId?: string;
    /**
     * 
     * @type {string}
     * @memberof LoginResponseDto
     */
    memberId?: string;
    /**
     * 
     * @type {string}
     * @memberof LoginResponseDto
     */
    refreshToken?: string;
    /**
     * 
     * @type {boolean}
     * @memberof LoginResponseDto
     */
    isRegistered?: boolean;
}

/**
 * Check if a given object implements the LoginResponseDto interface.
 */
export function instanceOfLoginResponseDto(value: object): value is LoginResponseDto {
    return true;
}

export function LoginResponseDtoFromJSON(json: any): LoginResponseDto {
    return LoginResponseDtoFromJSONTyped(json, false);
}

export function LoginResponseDtoFromJSONTyped(json: any, ignoreDiscriminator: boolean): LoginResponseDto {
    if (json == null) {
        return json;
    }
    return {
        
        'accessToken': json['accessToken'] == null ? undefined : json['accessToken'],
        'kakaoId': json['kakaoId'] == null ? undefined : json['kakaoId'],
        'memberId': json['memberId'] == null ? undefined : json['memberId'],
        'refreshToken': json['refreshToken'] == null ? undefined : json['refreshToken'],
        'isRegistered': json['isRegistered'] == null ? undefined : json['isRegistered'],
    };
}

export function LoginResponseDtoToJSON(json: any): LoginResponseDto {
    return LoginResponseDtoToJSONTyped(json, false);
}

export function LoginResponseDtoToJSONTyped(value?: LoginResponseDto | null, ignoreDiscriminator: boolean = false): any {
    if (value == null) {
        return value;
    }

    return {
        
        'accessToken': value['accessToken'],
        'kakaoId': value['kakaoId'],
        'memberId': value['memberId'],
        'refreshToken': value['refreshToken'],
        'isRegistered': value['isRegistered'],
    };
}

