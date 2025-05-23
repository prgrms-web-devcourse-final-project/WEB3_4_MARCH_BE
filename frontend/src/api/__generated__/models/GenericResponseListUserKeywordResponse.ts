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
import type { UserKeywordResponse } from './UserKeywordResponse';
import {
    UserKeywordResponseFromJSON,
    UserKeywordResponseFromJSONTyped,
    UserKeywordResponseToJSON,
    UserKeywordResponseToJSONTyped,
} from './UserKeywordResponse';

/**
 * 
 * @export
 * @interface GenericResponseListUserKeywordResponse
 */
export interface GenericResponseListUserKeywordResponse {
    /**
     * 
     * @type {Date}
     * @memberof GenericResponseListUserKeywordResponse
     */
    timestamp?: Date;
    /**
     * 
     * @type {number}
     * @memberof GenericResponseListUserKeywordResponse
     */
    code?: number;
    /**
     * 
     * @type {Array<UserKeywordResponse>}
     * @memberof GenericResponseListUserKeywordResponse
     */
    data?: Array<UserKeywordResponse>;
    /**
     * 
     * @type {string}
     * @memberof GenericResponseListUserKeywordResponse
     */
    message?: string;
    /**
     * 
     * @type {boolean}
     * @memberof GenericResponseListUserKeywordResponse
     */
    isSuccess?: boolean;
}

/**
 * Check if a given object implements the GenericResponseListUserKeywordResponse interface.
 */
export function instanceOfGenericResponseListUserKeywordResponse(value: object): value is GenericResponseListUserKeywordResponse {
    return true;
}

export function GenericResponseListUserKeywordResponseFromJSON(json: any): GenericResponseListUserKeywordResponse {
    return GenericResponseListUserKeywordResponseFromJSONTyped(json, false);
}

export function GenericResponseListUserKeywordResponseFromJSONTyped(json: any, ignoreDiscriminator: boolean): GenericResponseListUserKeywordResponse {
    if (json == null) {
        return json;
    }
    return {
        
        'timestamp': json['timestamp'] == null ? undefined : (new Date(json['timestamp'])),
        'code': json['code'] == null ? undefined : json['code'],
        'data': json['data'] == null ? undefined : ((json['data'] as Array<any>).map(UserKeywordResponseFromJSON)),
        'message': json['message'] == null ? undefined : json['message'],
        'isSuccess': json['isSuccess'] == null ? undefined : json['isSuccess'],
    };
}

export function GenericResponseListUserKeywordResponseToJSON(json: any): GenericResponseListUserKeywordResponse {
    return GenericResponseListUserKeywordResponseToJSONTyped(json, false);
}

export function GenericResponseListUserKeywordResponseToJSONTyped(value?: GenericResponseListUserKeywordResponse | null, ignoreDiscriminator: boolean = false): any {
    if (value == null) {
        return value;
    }

    return {
        
        'timestamp': value['timestamp'] == null ? undefined : ((value['timestamp']).toISOString()),
        'code': value['code'],
        'data': value['data'] == null ? undefined : ((value['data'] as Array<any>).map(UserKeywordResponseToJSON)),
        'message': value['message'],
        'isSuccess': value['isSuccess'],
    };
}

