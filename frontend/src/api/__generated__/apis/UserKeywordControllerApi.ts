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


import * as runtime from '../runtime';
import type {
  GenericResponseListUserKeywordResponse,
  GenericResponseVoid,
  UserKeywordSaveRequest,
} from '../models/index';
import {
    GenericResponseListUserKeywordResponseFromJSON,
    GenericResponseListUserKeywordResponseToJSON,
    GenericResponseVoidFromJSON,
    GenericResponseVoidToJSON,
    UserKeywordSaveRequestFromJSON,
    UserKeywordSaveRequestToJSON,
} from '../models/index';

export interface SaveKeywordsRequest {
    userKeywordSaveRequest: UserKeywordSaveRequest;
}

/**
 * 
 */
export class UserKeywordControllerApi extends runtime.BaseAPI {

    /**
     */
    async getMyKeywordsRaw(initOverrides?: RequestInit | runtime.InitOverrideFunction): Promise<runtime.ApiResponse<GenericResponseListUserKeywordResponse>> {
        const queryParameters: any = {};

        const headerParameters: runtime.HTTPHeaders = {};

        if (this.configuration && this.configuration.accessToken) {
            // oauth required
            headerParameters["Authorization"] = await this.configuration.accessToken("kakaoOAuth", []);
        }

        const response = await this.request({
            path: `/api/user-keywords`,
            method: 'GET',
            headers: headerParameters,
            query: queryParameters,
        }, initOverrides);

        return new runtime.JSONApiResponse(response, (jsonValue) => GenericResponseListUserKeywordResponseFromJSON(jsonValue));
    }

    /**
     */
    async getMyKeywords(initOverrides?: RequestInit | runtime.InitOverrideFunction): Promise<GenericResponseListUserKeywordResponse> {
        const response = await this.getMyKeywordsRaw(initOverrides);
        return await response.value();
    }

    /**
     */
    async saveKeywordsRaw(requestParameters: SaveKeywordsRequest, initOverrides?: RequestInit | runtime.InitOverrideFunction): Promise<runtime.ApiResponse<GenericResponseVoid>> {
        if (requestParameters['userKeywordSaveRequest'] == null) {
            throw new runtime.RequiredError(
                'userKeywordSaveRequest',
                'Required parameter "userKeywordSaveRequest" was null or undefined when calling saveKeywords().'
            );
        }

        const queryParameters: any = {};

        const headerParameters: runtime.HTTPHeaders = {};

        headerParameters['Content-Type'] = 'application/json';

        if (this.configuration && this.configuration.accessToken) {
            // oauth required
            headerParameters["Authorization"] = await this.configuration.accessToken("kakaoOAuth", []);
        }

        const response = await this.request({
            path: `/api/user-keywords`,
            method: 'POST',
            headers: headerParameters,
            query: queryParameters,
            body: UserKeywordSaveRequestToJSON(requestParameters['userKeywordSaveRequest']),
        }, initOverrides);

        return new runtime.JSONApiResponse(response, (jsonValue) => GenericResponseVoidFromJSON(jsonValue));
    }

    /**
     */
    async saveKeywords(requestParameters: SaveKeywordsRequest, initOverrides?: RequestInit | runtime.InitOverrideFunction): Promise<GenericResponseVoid> {
        const response = await this.saveKeywordsRaw(requestParameters, initOverrides);
        return await response.value();
    }

}
