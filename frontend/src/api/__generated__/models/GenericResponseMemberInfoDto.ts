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
import type { MemberInfoDto } from './MemberInfoDto';
import {
    MemberInfoDtoFromJSON,
    MemberInfoDtoFromJSONTyped,
    MemberInfoDtoToJSON,
    MemberInfoDtoToJSONTyped,
} from './MemberInfoDto';

/**
 * 
 * @export
 * @interface GenericResponseMemberInfoDto
 */
export interface GenericResponseMemberInfoDto {
    /**
     * 
     * @type {Date}
     * @memberof GenericResponseMemberInfoDto
     */
    timestamp?: Date;
    /**
     * 
     * @type {number}
     * @memberof GenericResponseMemberInfoDto
     */
    code?: number;
    /**
     * 
     * @type {MemberInfoDto}
     * @memberof GenericResponseMemberInfoDto
     */
    data?: MemberInfoDto;
    /**
     * 
     * @type {string}
     * @memberof GenericResponseMemberInfoDto
     */
    message?: string;
    /**
     * 
     * @type {boolean}
     * @memberof GenericResponseMemberInfoDto
     */
    isSuccess?: boolean;
}

/**
 * Check if a given object implements the GenericResponseMemberInfoDto interface.
 */
export function instanceOfGenericResponseMemberInfoDto(value: object): value is GenericResponseMemberInfoDto {
    return true;
}

export function GenericResponseMemberInfoDtoFromJSON(json: any): GenericResponseMemberInfoDto {
    return GenericResponseMemberInfoDtoFromJSONTyped(json, false);
}

export function GenericResponseMemberInfoDtoFromJSONTyped(json: any, ignoreDiscriminator: boolean): GenericResponseMemberInfoDto {
    if (json == null) {
        return json;
    }
    return {
        
        'timestamp': json['timestamp'] == null ? undefined : (new Date(json['timestamp'])),
        'code': json['code'] == null ? undefined : json['code'],
        'data': json['data'] == null ? undefined : MemberInfoDtoFromJSON(json['data']),
        'message': json['message'] == null ? undefined : json['message'],
        'isSuccess': json['isSuccess'] == null ? undefined : json['isSuccess'],
    };
}

export function GenericResponseMemberInfoDtoToJSON(json: any): GenericResponseMemberInfoDto {
    return GenericResponseMemberInfoDtoToJSONTyped(json, false);
}

export function GenericResponseMemberInfoDtoToJSONTyped(value?: GenericResponseMemberInfoDto | null, ignoreDiscriminator: boolean = false): any {
    if (value == null) {
        return value;
    }

    return {
        
        'timestamp': value['timestamp'] == null ? undefined : ((value['timestamp']).toISOString()),
        'code': value['code'],
        'data': MemberInfoDtoToJSON(value['data']),
        'message': value['message'],
        'isSuccess': value['isSuccess'],
    };
}

