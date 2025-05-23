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
import type { Sortnull } from './Sortnull';
import {
    SortnullFromJSON,
    SortnullFromJSONTyped,
    SortnullToJSON,
    SortnullToJSONTyped,
} from './Sortnull';
import type { Pageablenull } from './Pageablenull';
import {
    PageablenullFromJSON,
    PageablenullFromJSONTyped,
    PageablenullToJSON,
    PageablenullToJSONTyped,
} from './Pageablenull';
import type { ChatMessageResponse } from './ChatMessageResponse';
import {
    ChatMessageResponseFromJSON,
    ChatMessageResponseFromJSONTyped,
    ChatMessageResponseToJSON,
    ChatMessageResponseToJSONTyped,
} from './ChatMessageResponse';

/**
 * 
 * @export
 * @interface SliceChatMessageResponse
 */
export interface SliceChatMessageResponse {
    /**
     * 
     * @type {number}
     * @memberof SliceChatMessageResponse
     */
    size?: number;
    /**
     * 
     * @type {Array<ChatMessageResponse>}
     * @memberof SliceChatMessageResponse
     */
    content?: Array<ChatMessageResponse>;
    /**
     * 
     * @type {number}
     * @memberof SliceChatMessageResponse
     */
    number?: number;
    /**
     * 
     * @type {Sortnull}
     * @memberof SliceChatMessageResponse
     */
    sort?: Sortnull;
    /**
     * 
     * @type {number}
     * @memberof SliceChatMessageResponse
     */
    numberOfElements?: number;
    /**
     * 
     * @type {Pageablenull}
     * @memberof SliceChatMessageResponse
     */
    pageable?: Pageablenull;
    /**
     * 
     * @type {boolean}
     * @memberof SliceChatMessageResponse
     */
    first?: boolean;
    /**
     * 
     * @type {boolean}
     * @memberof SliceChatMessageResponse
     */
    last?: boolean;
    /**
     * 
     * @type {boolean}
     * @memberof SliceChatMessageResponse
     */
    empty?: boolean;
}

/**
 * Check if a given object implements the SliceChatMessageResponse interface.
 */
export function instanceOfSliceChatMessageResponse(value: object): value is SliceChatMessageResponse {
    return true;
}

export function SliceChatMessageResponseFromJSON(json: any): SliceChatMessageResponse {
    return SliceChatMessageResponseFromJSONTyped(json, false);
}

export function SliceChatMessageResponseFromJSONTyped(json: any, ignoreDiscriminator: boolean): SliceChatMessageResponse {
    if (json == null) {
        return json;
    }
    return {
        
        'size': json['size'] == null ? undefined : json['size'],
        'content': json['content'] == null ? undefined : ((json['content'] as Array<any>).map(ChatMessageResponseFromJSON)),
        'number': json['number'] == null ? undefined : json['number'],
        'sort': json['sort'] == null ? undefined : SortnullFromJSON(json['sort']),
        'numberOfElements': json['numberOfElements'] == null ? undefined : json['numberOfElements'],
        'pageable': json['pageable'] == null ? undefined : PageablenullFromJSON(json['pageable']),
        'first': json['first'] == null ? undefined : json['first'],
        'last': json['last'] == null ? undefined : json['last'],
        'empty': json['empty'] == null ? undefined : json['empty'],
    };
}

export function SliceChatMessageResponseToJSON(json: any): SliceChatMessageResponse {
    return SliceChatMessageResponseToJSONTyped(json, false);
}

export function SliceChatMessageResponseToJSONTyped(value?: SliceChatMessageResponse | null, ignoreDiscriminator: boolean = false): any {
    if (value == null) {
        return value;
    }

    return {
        
        'size': value['size'],
        'content': value['content'] == null ? undefined : ((value['content'] as Array<any>).map(ChatMessageResponseToJSON)),
        'number': value['number'],
        'sort': SortnullToJSON(value['sort']),
        'numberOfElements': value['numberOfElements'],
        'pageable': PageablenullToJSON(value['pageable']),
        'first': value['first'],
        'last': value['last'],
        'empty': value['empty'],
    };
}

