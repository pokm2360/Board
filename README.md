Board Service
Spring Boot + Thymeleaf 게시판
목록/검색/페이지네이션/좋아요 토글(AJAX)/댓글 & 대댓글(트리)/고유 조회수/간단한 로그인 UI

<img width="1919" height="890" alt="Image" src="https://github.com/user-attachments/assets/4cd84215-2d60-4185-87cc-5d8c05399a06" />

✨ Features
게시글 목록 & 페이지네이션 (10개/페이지, 최신순)

검색 드롭다운: 전체 | 제목 | 작성자 + 검색어 유효성 검사(공백만 제출 방지)

고유 조회수 카운트: 로그인 사용자 또는 IP 기준 중복 방지

좋아요 토글 (AJAX)

JSON 응답으로 버튼/카운트 즉시 갱신

history.replaceState 적용으로 뒤로가기 여러 번 눌러야 하는 문제 해결

댓글 & 대댓글(트리)

depth 기반 들여쓰기(depth * 16px)

본인/관리자만 삭제 가능

일관된 UI

네비게이션 가로 100%

목록/검색 페이지네이션 UI 통일

로그인 폼 적당한 너비(약 420px)

(선택) CSRF

템플릿의 CSRF hidden input은 주석 처리되어 있음(필요 시 주석 해제)

🧰 Tech Stack
Backend: Spring Boot 3, Spring Security 6, Spring Data JPA

View: Thymeleaf 3, Bootstrap 4.3.1

Build: Maven

DB: H2 / MySQL (선택)

📦 Project Structure (주요)
swift
복사
편집
src/main/java/com/example/Board_basic/
 ├─ Controller/
 │   ├─ PostController.java
 │   └─ CommentController.java     // 댓글 라우팅 전담
 ├─ Service/
 │   ├─ PostService.java
 │   └─ CommentService.java        // reply: depth = parent.depth + 1
 ├─ Entity/
 │   ├─ Post.java
 │   └─ Comment.java               // children: @Builder.Default
 └─ Repository/
src/main/resources/
 ├─ templates/
 │   ├─ layout.html
 │   ├─ list.html / search.html
 │   ├─ read.html
 │   └─ write.html
 └─ static/css/app.css
🚀 Getting Started
Prerequisites
JDK 17+

Maven 3.9+

Run
bash
복사
편집
# 1) (선택) DB 설정(application.yml) 확인
# 2) 실행
./mvnw spring-boot:run
# or
mvn spring-boot:run
기본 포트: http://localhost:8080

Configuration (예시)
H2 (in-memory)

yaml
복사
편집
spring:
  datasource:
    url: jdbc:h2:mem:board;MODE=MySQL;DB_CLOSE_DELAY=-1
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
MySQL

yaml
복사
편집
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/board?serverTimezone=Asia/Seoul&characterEncoding=UTF-8
    username: root
    password: your_pw
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate.format_sql: true
🖼️ Screenshots
필요 시 /docs 폴더에 스크린샷을 추가하고 여기에 링크하세요.
예) 목록 / 상세 / 검색 / 대댓글 / 좋아요 토글 등

🔍 Search & Pagination
경로: GET /posts/search?keyword=...&scope=...&page=...

검색 범위(scope): all | title | writer

키워드 공백 제거 & 빈 문자열 제출 방지(자바스크립트로 alert)

❤️ Like (AJAX Toggle)
경로: POST /posts/{id}/like

응답(JSON):

json
복사
편집
{ "liked": true, "count": 3 }
브라우저 히스토리 누적 방지:

js
복사
편집
if (window.history && history.replaceState) {
  history.replaceState(null, '', location.href);
}
💬 Comments & Replies
원댓글: POST /posts/{postId}/comments

대댓글: POST /posts/{postId}/comments/{parentId}/reply

삭제: POST /posts/{postId}/comments/{commentId}/delete (본인/관리자)

핵심 로직

java
복사
편집
// CommentService#reply
Comment parent = commentRepository.findById(parentId)
    .orElseThrow(() -> new IllegalArgumentException("부모 댓글이 존재하지 않습니다."));

Comment c = commentRepository.save(Comment.builder()
    .post(post)
    .parent(parent)
    .depth(parent.getDepth() + 1)   // <= 대댓글 들여쓰기 깊이
    .content(content)
    .writer(nickname)
    .createdDate(LocalDateTime.now())
    .build());
Thymeleaf 들여쓰기

html
복사
편집
<li class="list-group-item"
    th:each="c : ${comments}"
    th:style="|margin-left: ${(c.depth == null ? 0 : c.depth) * 16}px|">
🔐 CSRF
read.html 내 CSRF hidden input은 주석 처리되어 있습니다.

CSRF를 활성화하려면 주석을 해제하거나 아래처럼 th:if를 사용하세요:

html
복사
편집
<input type="hidden" th:if="${_csrf != null}"
       th:name="${_csrf?.parameterName}" th:value="${_csrf?.token}" />
🧩 Troubleshooting
드롭다운이 열리지 않음
스크립트 로딩 순서 확인: jQuery → Popper → Bootstrap.
수동 토글 스크립트도 포함되어 있어 Popper 없이도 동작.

검색어 없이 검색됨
폼 submit에서 공백 제거, 빈 값이면 alert 후 focus.

좋아요 취소 후 뒤로가기를 여러 번 눌러야 함
AJAX + history.replaceState 적용으로 해결.

Thymeleaf 식 오류
"'margin-left:' + (c.depth * 16) + 'px'" → 파이프 리터럴 사용:

html
복사
편집
th:style="|margin-left: ${(c.depth == null ? 0 : c.depth) * 16}px|"
페이지 하단에 JS 코드가 텍스트로 노출
중복된 <script> 블록/닫는 태그 순서 확인, 불필요한 스크립트 제거.

Tomcat "Invalid character found in method name [0x16...]"
HTTPS 패킷을 HTTP로 받는 경우 발생. 로컬은 http로 접근하거나 프록시/TLS 설정을 일치.

🗺️ Roadmap (Optional)
댓글 접기/펼치기, 댓글 페이지네이션

조회수/좋아요 캐싱(예: Redis) 및 동시성 보강

검색 정렬/필터 고도화

테스트(단위/통합) 추가

CSRF 정식 활성화 및 폼 전반 적용

📄 License
개인 학습/포트폴리오 용도.

라이선스가 필요하면 LICENSE 파일을 추가하세요.

🙌 Acknowledgements
Spring Boot, Spring Security, Spring Data JPA

Thymeleaf, Bootstrap, Bootstrap Icons

개선 아이디어나 PR 환영합니다!









ChatGPT에게 묻기

