package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 추가 관련 기능")
public class LineSectionAcceptanceTest extends AcceptanceTest {
    private LineResponse 일호선;
    private StationResponse 구일역;
    private StationResponse 구로역;
    private StationResponse 청량리역;
    private StationResponse 회기역;

    @BeforeEach
    public void setUp() {
        super.setUp();

        구일역 = StationAcceptanceTest.지하철역_등록_요청("구일역").as(StationResponse.class);
        구로역 = StationAcceptanceTest.지하철역_등록_요청("구로역").as(StationResponse.class);
        청량리역 = StationAcceptanceTest.지하철역_등록_요청("청량리역").as(StationResponse.class);
        회기역 = StationAcceptanceTest.지하철역_등록_요청("회기역").as(StationResponse.class);

        Map<String, Object> lineCreateParams;
        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", "1호선");
        lineCreateParams.put("color", "blue");
        lineCreateParams.put("upStationId", 구일역.getId());
        lineCreateParams.put("downStationId", 회기역.getId());
        lineCreateParams.put("distance", 100);
        일호선 = LineAcceptanceTest.지하철_노선_생성_요청(lineCreateParams).as(LineResponse.class);
    }

    @DisplayName("노선에 구간을 추가한다.")
    @Test
    void addSections() {
        ExtractableResponse<Response> response = 구간_추가_요청(구간_추가(구일역, 구로역, 10));

        구간_추가됨(response);
    }

    @DisplayName("노선에 구간을 여러개 추가하고 정렬을 확인한다.")
    @Test
    void addSections2() {
        StationResponse 신도림역 = StationAcceptanceTest.지하철역_등록_요청("신도림역").as(StationResponse.class);
        구간_추가_요청(구간_추가(구일역, 회기역, 150));
        ExtractableResponse<Response> response = 구간_추가_요청(구간_추가(구일역, 구로역, 100));

        구간_추가됨(response);
        추가된_구간_정렬됨(response);
    }

    private void 추가된_구간_정렬됨(ExtractableResponse<Response> response) {
        LineResponse line = response.as(LineResponse.class);
        List<String> resultNameList = line.getStations().stream()
                .map(it -> it.getName())
                .collect(Collectors.toList());

        List<String> stationNameLIst = Arrays.asList("구일역", "구로역", "회기역");

        assertThat(resultNameList).containsExactlyElementsOf(stationNameLIst);
    }

    @DisplayName("상/하행선이 동일한 구간 추가 불가")
    @Test
    void addDuplicationSection() {
        ExtractableResponse<Response> response = 구간_추가_요청(구간_추가(구일역, 회기역, 10));

        지하철_구간_중복_등록_불가(response);
    }

    @DisplayName("상/하행선 둘중 하나와도 일치하지 않는 구간 추가 불가")
    @Test
    void addNotSameSection() {
        ExtractableResponse<Response> response = 구간_추가_요청(구간_추가(구로역, 청량리역, 10));

        지하철_구간_상하행_모두_불일치_등록_불가(response);
    }

    @DisplayName("중간에 구간 추가시 기존 구간보다 긴 거리값 가지고 있을 경우 추가 불가")
    @Test
    void addLongDistanceSection() {
        ExtractableResponse<Response> response = 구간_추가_요청(구간_추가(구일역, 구로역, 300));

        지하철_구간_거리_길다면_등록_불가(response);
    }


    @DisplayName("종점역 삭제")
    @Test
    void deleteEndSection() {
        구간_추가_요청(구간_추가(구일역, 구로역, 30));

        ExtractableResponse<Response> response = 구간_삭제_요청(일호선, 구일역);

        종점역_삭제후_정렬됨(response);
    }

    @DisplayName("중간역 삭제")
    @Test
    void deleteMiddleSection() {
        구간_추가_요청(구간_추가(구일역, 구로역, 30));
        구간_추가_요청(구간_추가(구로역, 청량리역, 20));

        ExtractableResponse<Response> response = 구간_삭제_요청(일호선, 구로역);

        중간역_삭제후_정렬됨(response);
    }

    @DisplayName("노선에 등록되어있지 않은 역을 제거시 예외 발생")
    @Test
    void deleteException1() {
        ExtractableResponse<Response> response = 구간_삭제_요청(일호선, 청량리역);
        노선_미등록_역_제거_예외(response);
    }

    @DisplayName("구간이 하나인 노선에서 마지막 구간을 제거시 예외 발생")
    @Test
    void deleteStationException2() {
        ExtractableResponse<Response> response = 구간_삭제_요청(일호선, 구로역);
        마지막_구간_제거_예외(response);
    }

    private ExtractableResponse<Response> 구간_삭제_요청(LineResponse line, StationResponse station) {
        return RestAssured.given().log().all().
                when().
                delete("/lines/{lineId}/sections?stationId={stationId}", line.getId(), station.getId()).
                then().
                log().all().
                extract();
    }

    private void 종점역_삭제후_정렬됨(ExtractableResponse<Response> response) {
        LineResponse line = response.as(LineResponse.class);
        List<StationResponse> stations = line.getStations();
        assertThat(stations.get(0).getId()).isEqualTo(구로역.getId());
        assertThat(stations.get(1).getId()).isEqualTo(회기역.getId());
    }

    private void 중간역_삭제후_정렬됨(ExtractableResponse<Response> response) {
        LineResponse line = response.as(LineResponse.class);
        List<StationResponse> stations = line.getStations();
        assertThat(stations.get(0).getId()).isEqualTo(구일역.getId());
        assertThat(stations.get(1).getId()).isEqualTo(청량리역.getId());
        assertThat(stations.get(2).getId()).isEqualTo(회기역.getId());
    }

    private static void 노선_미등록_역_제거_예외 (ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private static void 마지막_구간_제거_예외(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private ExtractableResponse<Response> 구간_추가_요청(Map<String, Object> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("lines/{lineId}/sections", 일호선.getId())
                .then().log().all()
                .extract();
    }

    private void 구간_추가됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private Map<String, Object> 구간_추가(StationResponse station1, StationResponse station2, int distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", station1.getId());
        params.put("downStationId", station2.getId());
        params.put("distance", distance);
        return params;
    }

    private static void 지하철_구간_중복_등록_불가(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private static void 지하철_구간_상하행_모두_불일치_등록_불가(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private static void 지하철_구간_거리_길다면_등록_불가(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
