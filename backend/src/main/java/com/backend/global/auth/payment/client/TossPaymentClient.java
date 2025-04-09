package com.backend.global.auth.payment.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class TossPaymentClient {
    // 실제 환경에 맞게 설정 (예: application-local.yml 또는 환경변수를 통해 주입)
    @Value("${toss.payments.secret}")
    private String tossSecretKey;

    // 테스트 시나리오용 TossPayments-Test-Code 헤더 (필요시 설정, 없으면 빈 문자열)
    @Value("${toss.payments.test-code:}")
    private String tossTestCode;

    // 인증 헤더 생성: 시크릿 키 뒤에 콜론을 붙이고 Base64 인코딩
    private String getAuthorizations() {
        String credentials = tossSecretKey + ":";
        return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 결제 승인 요청 API 호출
     * 실제 API 엔드포인트: https://api.tosspayments.com/v1/payments/confirm
     * 요청 JSON: { "paymentKey": ..., "orderId": ..., "amount": ... }
     * 테스트 시나리오를 위해 TossPayments-Test-Code 헤더를 옵션으로 추가합니다.
     */
    public HttpResponse<String> requestConfirm(String paymentKey, String orderId, Integer amount) throws IOException, InterruptedException {
        String requestBody = String.format("{\"paymentKey\":\"%s\", \"orderId\":\"%s\", \"amount\":%d}", paymentKey, orderId, amount);

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create("https://api.tosspayments.com/v1/payments/confirm"))
                .header("Authorization", getAuthorizations())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody));

        // 테스트용 에러 코드를 재현하고 싶은 경우, 프로퍼티에서 주입받은 tossTestCode가 있으면 헤더에 추가.
        if(tossTestCode != null && !tossTestCode.isEmpty()){
            requestBuilder.header("TossPayments-Test-Code", tossTestCode);
        }

        HttpRequest request = requestBuilder.build();
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * 결제 취소 요청 API 호출
     * 실제 API 엔드포인트: https://api.tosspayments.com/v1/payments/{paymentKey}/cancel
     * 요청 JSON: { "cancelReason": "..." }
     */
    public HttpResponse<String> requestPaymentCancel(String paymentKey, String cancelReason) throws IOException, InterruptedException {
        String requestBody = String.format("{\"cancelReason\":\"%s\"}", cancelReason);
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create("https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel"))
                .header("Authorization", getAuthorizations())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody));
        if(tossTestCode != null && !tossTestCode.isEmpty()){
            requestBuilder.header("TossPayments-Test-Code", tossTestCode);
        }
        HttpRequest request = requestBuilder.build();
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }

    // 결제 승인 요청 등 다른 API 호출 로직도 추가 구현 예정
}