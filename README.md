# Are_You_There - 버스 운전자용 정류장 대기열 사전 인지 프로그램

버스 기사가 정류장 ID를 입력하면 해당 정류장의 대기 승객 수와 휠체어 승객 수를 실시간으로 조회하는 Android 앱입니다.  
[Im_Here](https://github.com/thdwjdrl401/Im_Here) (승객용 앱)와 연동되는 시스템의 기사 측 클라이언트입니다.

## 시스템 구성

```
[Im_Here - 승객 앱]                    [Are_You_There - 기사 앱]
  승객이 정류장 대기열 등록   →   서버 DB   ←   기사가 정류장 대기 현황 조회
```

## 주요 기능

- **대기 현황 조회**: 정류장 ID 입력 후 버튼 클릭으로 해당 정류장 총 대기 승객 수 조회
- **휠체어 승객 파악**: 대기 승객 중 휠체어 사용자 수를 별도로 표시
- **정류장 ID 오류 감지**: 존재하지 않는 정류장 ID 입력 시 오류 메시지 표시

## 기술 스택

| 항목 | 내용 |
|------|------|
| 플랫폼 | Android (Java) |
| 최소 SDK | API 24 (Android 7.0) |
| 타깃 SDK | API 29 (Android 10) |
| 서버 통신 | HttpURLConnection (AsyncTask) |
| 백엔드 | AWS EC2 (ap-northeast-1) + PHP |

## 동작 흐름

```
정류장 ID 입력
 └─ 조회 버튼 클릭
     └─ 서버 API 호출 (/driver/index.php?stop_id={id})
         ├─ 정상 응답 → "총 승차객: N / 승차객 중 휠체어: M" 표시
         └─ 오류 응답 → "정류소 아이디 오류" 표시
```

## 설치 및 실행

1. 저장소 클론
   ```bash
   git clone <repository-url>
   cd Are_You_There
   ```

2. Android Studio에서 프로젝트 열기 → **Run** (Shift+F10)

### 필요 권한

| 권한 | 용도 |
|------|------|
| `INTERNET` | 서버 API 통신 |

## API 엔드포인트

| 기능 | 메서드 | 경로 |
|------|--------|------|
| 정류장 대기 현황 조회 | GET | `/driver/index.php?stop_id={stop_id}` |

### 응답 예시

```json
{
  "next_stop": [
    {
      "stop_id": "12345",
      "people": "3",
      "wheel": "1"
    }
  ]
}
```

## 프로젝트 구조

```
Are_You_There/
├── app/
│   └── src/main/
│       ├── java/com/example/driver/
│       │   └── MainActivity.java    # 정류장 ID 입력 및 대기 현황 조회
│       ├── res/
│       │   └── layout/activity_main.xml
│       └── AndroidManifest.xml
└── build.gradle
```

## 관련 프로젝트

- **[Im_Here](https://github.com/thdwjdrl401/Im_Here)** — 승객이 버스 정류장 대기열에 등록하는 안드로이드 앱
