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
  GenericResponseLoginResponseDto,
  GenericResponseVoid,
} from '../models/index';
import {
    GenericResponseLoginResponseDtoFromJSON,
    GenericResponseLoginResponseDtoToJSON,
    GenericResponseVoidFromJSON,
    GenericResponseVoidToJSON,
} from '../models/index';

export interface LoginCallbackRequest {
    code?: string;
    error?: string;
    errorDescription?: string;
}

/**
 * 
 */
export class KakaoAuthControllerApi extends runtime.BaseAPI {

    /**
     */
    async getKakaoLoginUrlRaw(initOverrides?: RequestInit | runtime.InitOverrideFunction): Promise<runtime.ApiResponse<string>> {
        const queryParameters: any = {};

        const headerParameters: runtime.HTTPHeaders = {};

        if (this.configuration && this.configuration.accessToken) {
            // oauth required
            headerParameters["Authorization"] = await this.configuration.accessToken("kakaoOAuth", []);
        }

        const response = await this.request({
            path: `/api/auth/kakao/login-url`,
            method: 'GET',
            headers: headerParameters,
            query: queryParameters,
        }, initOverrides);

        if (this.isJsonMime(response.headers.get('content-type'))) {
            return new runtime.JSONApiResponse<string>(response);
        } else {
            return new runtime.TextApiResponse(response) as any;
        }
    }

    /**
     */
    async getKakaoLoginUrl(initOverrides?: RequestInit | runtime.InitOverrideFunction): Promise<string> {
        const response = await this.getKakaoLoginUrlRaw(initOverrides);
        return await response.value();
    }

    /**
     */
    async loginCallbackRaw(requestParameters: LoginCallbackRequest, initOverrides?: RequestInit | runtime.InitOverrideFunction): Promise<runtime.ApiResponse<GenericResponseLoginResponseDto>> {
        const queryParameters: any = {};

        if (requestParameters['code'] != null) {
            queryParameters['code'] = requestParameters['code'];
        }

        if (requestParameters['error'] != null) {
            queryParameters['error'] = requestParameters['error'];
        }

        if (requestParameters['errorDescription'] != null) {
            queryParameters['error_description'] = requestParameters['errorDescription'];
        }

        const headerParameters: runtime.HTTPHeaders = {};

        if (this.configuration && this.configuration.accessToken) {
            // oauth required
            headerParameters["Authorization"] = await this.configuration.accessToken("kakaoOAuth", []);
        }

        const response = await this.request({
            path: `/api/auth/kakao/callback`,
            method: 'GET',
            headers: headerParameters,
            query: queryParameters,
        }, initOverrides);

        return new runtime.JSONApiResponse(response, (jsonValue) => GenericResponseLoginResponseDtoFromJSON(jsonValue));
    }

    /**
     */
    async loginCallback(requestParameters: LoginCallbackRequest = {}, initOverrides?: RequestInit | runtime.InitOverrideFunction): Promise<GenericResponseLoginResponseDto> {
        const response = await this.loginCallbackRaw(requestParameters, initOverrides);
        return await response.value();
    }

    /**
     */
    async logoutRaw(initOverrides?: RequestInit | runtime.InitOverrideFunction): Promise<runtime.ApiResponse<GenericResponseVoid>> {
        const queryParameters: any = {};

        const headerParameters: runtime.HTTPHeaders = {};

        if (this.configuration && this.configuration.accessToken) {
            // oauth required
            headerParameters["Authorization"] = await this.configuration.accessToken("kakaoOAuth", []);
        }

        const response = await this.request({
            path: `/api/auth/kakao/logout`,
            method: 'POST',
            headers: headerParameters,
            query: queryParameters,
        }, initOverrides);

        return new runtime.JSONApiResponse(response, (jsonValue) => GenericResponseVoidFromJSON(jsonValue));
    }

    /**
     */
    async logout(initOverrides?: RequestInit | runtime.InitOverrideFunction): Promise<GenericResponseVoid> {
        const response = await this.logoutRaw(initOverrides);
        return await response.value();
    }

    /**
     */
    async refreshTokenRaw(initOverrides?: RequestInit | runtime.InitOverrideFunction): Promise<runtime.ApiResponse<GenericResponseLoginResponseDto>> {
        const queryParameters: any = {};

        const headerParameters: runtime.HTTPHeaders = {};

        if (this.configuration && this.configuration.accessToken) {
            // oauth required
            headerParameters["Authorization"] = await this.configuration.accessToken("kakaoOAuth", []);
        }

        const response = await this.request({
            path: `/api/auth/kakao/refresh`,
            method: 'GET',
            headers: headerParameters,
            query: queryParameters,
        }, initOverrides);

        return new runtime.JSONApiResponse(response, (jsonValue) => GenericResponseLoginResponseDtoFromJSON(jsonValue));
    }

    /**
     */
    async refreshToken(initOverrides?: RequestInit | runtime.InitOverrideFunction): Promise<GenericResponseLoginResponseDto> {
        const response = await this.refreshTokenRaw(initOverrides);
        return await response.value();
    }

    /**
     */
    async reissueRaw(initOverrides?: RequestInit | runtime.InitOverrideFunction): Promise<runtime.ApiResponse<GenericResponseLoginResponseDto>> {
        const queryParameters: any = {};

        const headerParameters: runtime.HTTPHeaders = {};

        if (this.configuration && this.configuration.accessToken) {
            // oauth required
            headerParameters["Authorization"] = await this.configuration.accessToken("kakaoOAuth", []);
        }

        const response = await this.request({
            path: `/api/auth/kakao/reissue`,
            method: 'POST',
            headers: headerParameters,
            query: queryParameters,
        }, initOverrides);

        return new runtime.JSONApiResponse(response, (jsonValue) => GenericResponseLoginResponseDtoFromJSON(jsonValue));
    }

    /**
     */
    async reissue(initOverrides?: RequestInit | runtime.InitOverrideFunction): Promise<GenericResponseLoginResponseDto> {
        const response = await this.reissueRaw(initOverrides);
        return await response.value();
    }

}
