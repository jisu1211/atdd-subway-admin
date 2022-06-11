## 3단계 - 구간 제거 기능

### 기능 요구 사항

- [v] 지하철 노선의 상행 종점 역을 삭제한다.
    * Given : 지하철 노선 생성하고, 구간을 추가한다.
    * When : 노선의 상행 종점역을 삭제한다.
    * Then : 다음으로 오던 역이 상행 종점역이 된 순서로 조회 된다.

- [v] 지하철 노선의 하행 종점 역을 삭제한다.
    * Given : 지하철 노선 생성하고, 구간을 추가한다.
    * When : 노선의 하행 종점역을 삭제한다.
    * Then : 이전 역이 하행 종점역이 된 순서로 조회 된다.

- [v] 지하철 노선의 중간 역을 삭제한다.
    * Given : 지하철 노선 생성하고, 구간을 추가한다.
    * When : 노선의 중간역을 삭제한다.
    * Then : 중간역이 삭제된 순서로 조회 된다.

- [v] 노선에 등록되지 않은 역을 삭제한다.
    * Given : 지하철 노선 생성하고, 구간을 추가한다.
    * When : 노선에 존재하지 않는 역을 삭제한다.
    * Then : 삭제되지 않고 에러 발생

- [v] 구간이 하나인 노선에서 마지막 역을 삭제한다.
    * Given : 지하철 노선 생성한다.
    * When : 노선의 마지막 역을 삭제한다.
    * Then : 삭제되지 않고 에러 발생


