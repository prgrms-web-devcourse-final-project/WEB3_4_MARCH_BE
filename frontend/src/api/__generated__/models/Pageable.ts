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
 * @interface Pageable
 */
export interface Pageable {
    /**
     * 
     * @type {number}
     * @memberof Pageable
     */
    page?: number;
    /**
     * 
     * @type {number}
     * @memberof Pageable
     */
    size?: number;
    /**
     * 
     * @type {Array<string>}
     * @memberof Pageable
     */
    sort?: Array<string>;
}

/**
 * Check if a given object implements the Pageable interface.
 */
export function instanceOfPageable(value: object): value is Pageable {
    return true;
}

export function PageableFromJSON(json: any): Pageable {
    return PageableFromJSONTyped(json, false);
}

export function PageableFromJSONTyped(json: any, ignoreDiscriminator: boolean): Pageable {
    if (json == null) {
        return json;
    }
    return {
        
        'page': json['page'] == null ? undefined : json['page'],
        'size': json['size'] == null ? undefined : json['size'],
        'sort': json['sort'] == null ? undefined : json['sort'],
    };
}

export function PageableToJSON(json: any): Pageable {
    return PageableToJSONTyped(json, false);
}

export function PageableToJSONTyped(value?: Pageable | null, ignoreDiscriminator: boolean = false): any {
    if (value == null) {
        return value;
    }

    return {
        
        'page': value['page'],
        'size': value['size'],
        'sort': value['sort'],
    };
}

