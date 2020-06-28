# 돈 뿌리기
- latest version : `1.1.0`
## HTTP Interfaces
### 뿌리기 생성
- uri : `/sprinkle`
- method : `POST`
- request body
```json
{
    "divideSize" : 10000, // 나눌 갯수
    "amount" : 5000000000 // 나눌 총액
}
```
- response body
```json
{
    "header": {
        "resultCode": 0, // 요청 성공 여부
        "message": "Success" // 요청 처리 메시지
    },
    "result": "GfO" // 생성된 뿌리기 토큰
}
```


### 뿌리기 정보 조회
- uri : `/sprinkle/{{tokenValue}}`
- method : `GET`
- response body
```json
{
    "header": {
        "resultCode": 0,
        "message": "Success"
    },
    "result": {
        "created": "2020-06-28T22:10:21",  // 뿌리기 생성 시간
        "initialAmount": 5000000000.00,    // 뿌리기 생성 총액
        "sprinkledAmount": 3229000000.00,  // 분배된 총액
        "receives": [
            {
                "receivedAmount": 0.00,                     // 분배받은 액수
                "receiver": "aaEfafd15251sf12fs1111111111"  // 분배받은 사람 ID
            },
            {
                "receivedAmount": 1150000000.00,
                "receiver": "aaEfafd15251sf12fs11111111111"
            },
            {
                "receivedAmount": 2079000000.00,
                "receiver": "aaEfafd15251sf12fs11111111111a"
            }
        ]
    }
}
```

### 뿌리기 분배 받기
- uri : `/sprinkle/{{tokenValue}}`
- method : `POST`
- request body = empty
- response body
```json
{
    "header": {
        "resultCode": 0,
        "message": "Success"
    },
    "result": 2079000000.00  // 분배받은 액수
}
```

## 개발 고려 사항
- 초기 버전 (until `1.0.3`) 까지는 분배 받기 요청이 이뤄질 시 실제 분배 액수가 결정이 이뤄지도록 구현됨
- 이후 버전 (from `1.1.0`) 부터는 뿌리기 생성하는 순간 분배 액 및 분배 횟수만큼 미리 분배 정보가 생성됨
    - 분배 받기 요청 동시 과부하에 대한 대응은 분배 정보와 실제 분배 이벤트를 분리 후 분배 이벤트 기록 시 `선 생성 분배 ID - 뿌리기 ID` 유니크 인덱스 생성
    - 실제 DB 에 쓰여질 시 유니크 정책에 위배되면 과부하에 의한 잘못된 분배이므로 익셉션 발생
    - 3회 재시도 후 안될 시에만 실패 응답
- 토큰 생성
    - URL Safe 한 문자들로만 토큰이 구성되도록. 다른 글자는 % 로 다뤄야 하므로
    - 토큰 랜덤 생성 후 이미 생성되어 있는 토큰인지 확인, 생성되었던 토큰이면 다시 랜덤 생성 (10회 체크)
        - 10회 체크 후 실패하면 토큰 생성 실패 응답
    - 10회 체크 성공 되었더라도 찰나의 순간 (가용 조합이 작아질수록) 중복 토큰이 생성될 수 있으므로 유니크 조건 지정
        - 유니크 조건에 위배되면 다시 생성 시도
    - 최대 약 30회의 토큰 재생성 시도하는 시나리오 가능 (10 * 3 = 30) 
- 아직 이래 저래 구현 테스트해본 과거 코드가 많이 정리되지 않은 상태
 
