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
 * @interface GenericResponseListMemberInfoDto
 */
export interface GenericResponseListMemberInfoDto {
    /**
     * 
     * @type {Date}
     * @memberof GenericResponseListMemberInfoDto
     */
    timestamp?: Date;
    /**
     * 
     * @type {number}
     * @memberof GenericResponseListMemberInfoDto
     */
    code?: number;
    /**
     * 
     * @type {Array<MemberInfoDto>}
     * @memberof GenericResponseListMemberInfoDto
     */
    data?: Array<MemberInfoDto>;
    /**
     * 
     * @type {string}
     * @memberof GenericResponseListMemberInfoDto
     */
    message?: string;
    /**
     * 
     * @type {boolean}
     * @memberof GenericResponseListMemberInfoDto
     */
    isSuccess?: boolean;
}

/**
 * Check if a given object implements the GenericResponseListMemberInfoDto interface.
 */
export function instanceOfGenericResponseListMemberInfoDto(value: object): value is GenericResponseListMemberInfoDto {
    return true;
}

export function GenericResponseListMemberInfoDtoFromJSON(json: any): GenericResponseListMemberInfoDto {
    return GenericResponseListMemberInfoDtoFromJSONTyped(json, false);
}

export function GenericResponseListMemberInfoDtoFromJSONTyped(json: any, ignoreDiscriminator: boolean): GenericResponseListMemberInfoDto {
    if (json == null) {
        return json;
    }
    return {
        
        'timestamp': json['timestamp'] == null ? undefined : (new Date(json['timestamp'])),
        'code': json['code'] == null ? undefined : json['code'],
        'data': json['data'] == null ? undefined : ((json['data'] as Array<any>).map(MemberInfoDtoFromJSON)),
        'message': json['message'] == null ? undefined : json['message'],
        'isSuccess': json['isSuccess'] == null ? undefined : json['isSuccess'],
    };
}

export function GenericResponseListMemberInfoDtoToJSON(json: any): GenericResponseListMemberInfoDto {
    return GenericResponseListMemberInfoDtoToJSONTyped(json, false);
}

export function GenericResponseListMemberInfoDtoToJSONTyped(value?: GenericResponseListMemberInfoDto | null, ignoreDiscriminator: boolean = false): any {
    if (value == null) {
        return value;
    }

    return {
        
        'timestamp': value['timestamp'] == null ? undefined : ((value['timestamp']).toISOString()),
        'code': value['code'],
        'data': value['data'] == null ? undefined : ((value['data'] as Array<any>).map(MemberInfoDtoToJSON)),
        'message': value['message'],
        'isSuccess': value['isSuccess'],
    };
}

