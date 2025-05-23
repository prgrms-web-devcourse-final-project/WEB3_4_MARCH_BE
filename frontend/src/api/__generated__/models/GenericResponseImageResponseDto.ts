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
import type { ImageResponseDto } from './ImageResponseDto';
import {
    ImageResponseDtoFromJSON,
    ImageResponseDtoFromJSONTyped,
    ImageResponseDtoToJSON,
    ImageResponseDtoToJSONTyped,
} from './ImageResponseDto';

/**
 * 
 * @export
 * @interface GenericResponseImageResponseDto
 */
export interface GenericResponseImageResponseDto {
    /**
     * 
     * @type {Date}
     * @memberof GenericResponseImageResponseDto
     */
    timestamp?: Date;
    /**
     * 
     * @type {number}
     * @memberof GenericResponseImageResponseDto
     */
    code?: number;
    /**
     * 
     * @type {ImageResponseDto}
     * @memberof GenericResponseImageResponseDto
     */
    data?: ImageResponseDto;
    /**
     * 
     * @type {string}
     * @memberof GenericResponseImageResponseDto
     */
    message?: string;
    /**
     * 
     * @type {boolean}
     * @memberof GenericResponseImageResponseDto
     */
    isSuccess?: boolean;
}

/**
 * Check if a given object implements the GenericResponseImageResponseDto interface.
 */
export function instanceOfGenericResponseImageResponseDto(value: object): value is GenericResponseImageResponseDto {
    return true;
}

export function GenericResponseImageResponseDtoFromJSON(json: any): GenericResponseImageResponseDto {
    return GenericResponseImageResponseDtoFromJSONTyped(json, false);
}

export function GenericResponseImageResponseDtoFromJSONTyped(json: any, ignoreDiscriminator: boolean): GenericResponseImageResponseDto {
    if (json == null) {
        return json;
    }
    return {
        
        'timestamp': json['timestamp'] == null ? undefined : (new Date(json['timestamp'])),
        'code': json['code'] == null ? undefined : json['code'],
        'data': json['data'] == null ? undefined : ImageResponseDtoFromJSON(json['data']),
        'message': json['message'] == null ? undefined : json['message'],
        'isSuccess': json['isSuccess'] == null ? undefined : json['isSuccess'],
    };
}

export function GenericResponseImageResponseDtoToJSON(json: any): GenericResponseImageResponseDto {
    return GenericResponseImageResponseDtoToJSONTyped(json, false);
}

export function GenericResponseImageResponseDtoToJSONTyped(value?: GenericResponseImageResponseDto | null, ignoreDiscriminator: boolean = false): any {
    if (value == null) {
        return value;
    }

    return {
        
        'timestamp': value['timestamp'] == null ? undefined : ((value['timestamp']).toISOString()),
        'code': value['code'],
        'data': ImageResponseDtoToJSON(value['data']),
        'message': value['message'],
        'isSuccess': value['isSuccess'],
    };
}

