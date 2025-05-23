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
 * @interface MemberSummary
 */
export interface MemberSummary {
    /**
     * 
     * @type {number}
     * @memberof MemberSummary
     */
    id?: number;
    /**
     * 
     * @type {string}
     * @memberof MemberSummary
     */
    name?: string;
    /**
     * 
     * @type {string}
     * @memberof MemberSummary
     */
    image?: string;
}

/**
 * Check if a given object implements the MemberSummary interface.
 */
export function instanceOfMemberSummary(value: object): value is MemberSummary {
    return true;
}

export function MemberSummaryFromJSON(json: any): MemberSummary {
    return MemberSummaryFromJSONTyped(json, false);
}

export function MemberSummaryFromJSONTyped(json: any, ignoreDiscriminator: boolean): MemberSummary {
    if (json == null) {
        return json;
    }
    return {
        
        'id': json['id'] == null ? undefined : json['id'],
        'name': json['name'] == null ? undefined : json['name'],
        'image': json['image'] == null ? undefined : json['image'],
    };
}

export function MemberSummaryToJSON(json: any): MemberSummary {
    return MemberSummaryToJSONTyped(json, false);
}

export function MemberSummaryToJSONTyped(value?: MemberSummary | null, ignoreDiscriminator: boolean = false): any {
    if (value == null) {
        return value;
    }

    return {
        
        'id': value['id'],
        'name': value['name'],
        'image': value['image'],
    };
}

