package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @BeforeEach
    void initStations() {
        Map<String, String> params1 = new HashMap<>();
        params1.put("name", "강남역");
        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "합정역");
        Map<String, String> params3 = new HashMap<>();
        params3.put("name", "교대역");

        StationAcceptanceTest.지하철_역_생성_요청(params1);
        StationAcceptanceTest.지하철_역_생성_요청(params2);
        StationAcceptanceTest.지하철_역_생성_요청(params3);
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        String name = "신분당선";
        String color = "red";
        LineRequest lineRequest = LineRequest.builder()
                .name(name)
                .color(color)
                .upStationId(1L)
                .downStationId(2L)
                .distance(10)
                .build();
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(lineRequest);

        // then
        지하철_노선_생성_응답됨(response);
        지하철_노선_값_검증됨(response, name, color);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        String name = "신분당선";
        String color = "red";
        LineRequest lineRequest = LineRequest.builder()
                .name(name)
                .color(color)
                .upStationId(1L)
                .downStationId(2L)
                .distance(10)
                .build();
        지하철_노선_생성_요청(lineRequest);

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(lineRequest);

        // then
        지하철_노선_생성_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        LineRequest lineRequest1 = LineRequest.builder()
                .name("신분당선")
                .color("red")
                .upStationId(1L)
                .downStationId(2L)
                .distance(10)
                .build();
        LineRequest lineRequest2 = LineRequest.builder()
                .name("1호선")
                .color("blue")
                .upStationId(1L)
                .downStationId(2L)
                .distance(10)
                .build();
        ExtractableResponse<Response> createdResponse1 = 지하철_노선_생성_요청(lineRequest1);
        ExtractableResponse<Response> createdResponse2 = 지하철_노선_생성_요청(lineRequest2);

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        지하철_노선_응답됨(response);
        지하철_노선_목록_포함됨(response, createdResponse1, createdResponse2);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        String name = "신분당선";
        String color = "red";
        LineRequest lineRequest = LineRequest.builder()
                .name(name)
                .color(color)
                .upStationId(1L)
                .downStationId(2L)
                .distance(10)
                .build();
        ExtractableResponse<Response> createdResponse = 지하철_노선_생성_요청(lineRequest);

        // when
        String uri = createdResponse.header("Location");
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(uri);

        // then
        지하철_노선_응답됨(response);
        지하철_노선_값_검증됨(response, name, color);
    }

    @DisplayName("존재하지 않는 지하철 노선을 조회한다.")
    @Test
    void getLineFail() {
        // give
        String uri = "lines/0";

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(uri);

        // then
        지하철_노선_조회_실패_응답됨(response);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        LineRequest createRequest = LineRequest.builder()
                .name("신분당선")
                .color("red")
                .upStationId(1L)
                .downStationId(2L)
                .distance(10)
                .build();
        ExtractableResponse<Response> createdResponse = 지하철_노선_생성_요청(createRequest);
        LineRequest updateRequest = LineRequest.builder()
                .name("1호선")
                .color("blue")
                .upStationId(1L)
                .downStationId(3L)
                .distance(5)
                .build();

        // when
        String uri = createdResponse.header("Location");
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(uri, updateRequest);

        // then
        지하철_노선_응답됨(response);
        지하철_노선_값_검증됨(response, updateRequest.getName(), updateRequest.getColor());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        LineRequest createRequest = LineRequest.builder()
                .name("신분당선")
                .color("red")
                .upStationId(1L)
                .downStationId(2L)
                .distance(10)
                .build();
        ExtractableResponse<Response> createdResponse = 지하철_노선_생성_요청(createRequest);

        // when
        String uri = createdResponse.header("Location");
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(uri);

        // then
        지하철_노선_삭제됨(response);
    }

    private ExtractableResponse<Response> 지하철_노선_생성_요청(final LineRequest request) {
        return given().log().all()
                .body(request)
                .accept(MediaType.ALL_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then()
                .log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청(final String uri) {
        return given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(uri)
                .then()
                .log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines")
                .then()
                .log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(final String uri, final LineRequest request) {
        return given().log().all()
                .body(request)
                .accept(MediaType.ALL_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(uri)
                .then()
                .log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_제거_요청(final String uri) {
        return given().log().all()
                .accept(MediaType.ALL_VALUE)
                .when()
                .delete(uri)
                .then()
                .log().all()
                .extract();
    }

    private void 지하철_노선_응답됨(final ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
    }

    private void 지하철_노선_조회_실패_응답됨(final ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    private void 지하철_노선_목록_포함됨(final ExtractableResponse<Response> response,
                               final ExtractableResponse<Response>... createdResponses) {
        LineResponse[] lineResponses = Arrays.stream(createdResponses)
                .map(createdResponse -> createdResponse.as(LineResponse.class))
                .toArray(LineResponse[]::new);
        assertThat(response.as(LineResponse[].class)).contains(lineResponses);
    }

    private void 지하철_노선_생성_응답됨(final ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private void 지하철_노선_값_검증됨(final ExtractableResponse<Response> response, final String name, final String color) {
        LineResponse lineResponse = response.as(LineResponse.class);
        assertAll(
                () -> assertThat(lineResponse.getId()).isNotNull(),
                () -> assertThat(lineResponse.getName()).isEqualTo(name),
                () -> assertThat(lineResponse.getColor()).isEqualTo(color),
                () -> assertThat(lineResponse.getModifiedDate()).isNotNull(),
                () -> assertThat(lineResponse.getCreatedDate()).isNotNull(),
                () -> assertThat(lineResponse.getStations()).isNotNull(),
                () -> assertThat(lineResponse.getStations().size()).isGreaterThanOrEqualTo(1)
        );
    }

    private void 지하철_노선_삭제됨(final ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private void 지하철_노선_생성_실패됨(final ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
