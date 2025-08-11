Board Service — README
스프링부트 + Thymeleaf 게시판.
목록/검색/페이지네이션/좋아요 토글(AJAX)/댓글 & 대댓글/고유 조회수/로그인 UI까지 포함.

<img width="1919" height="890" alt="Image" src="https://github.com/user-attachments/assets/4cd84215-2d60-4185-87cc-5d8c05399a06" />

Quick Start
bash
복사
편집
# 1) JDK 17+
# 2) DB 설정(application.yml) 확인 (H2/MySQL 등)
# 3) 실행
./mvnw spring-boot:run
# or
mvn spring-boot:run
기본 포트: 8080

기본 URL: http://localhost:8080

주요 기능
1) 목록 & 페이지네이션
GET / 또는 GET /list

10개 단위 페이징, 최신글(id desc)

UI 변수: posts, currentPage, totalPages

2) 검색 (드롭다운 범위 + 키워드 유효성)
헤더 검색 폼

범위(scope): 전체 | 제목 | 작성자 (부트스트랩 드롭다운)

키워드: 앞뒤 공백 제거, 빈 문자열 제출 방지(JavaScript로 안내)

GET /posts/search?keyword=...&scope=...&page=...

검색 결과 페이지네이션 목록과 동일 UI로 정렬

3) 조회수 (고유 카운트)
GET /posts/read/{id} 접속 시, 로그인 사용자 또는 IP 기준으로 중복 방지 카운팅

4) 좋아요 토글 (AJAX + 뒤로가기 이슈 해결)
POST /posts/{id}/like

로그인 필요

AJAX 요청일 때 JSON 응답: { liked: boolean, count: number }

버튼 라벨/스타일 & 카운트 즉시 반영

history.replaceState 사용으로 브라우저 히스토리에 쌓이지 않아 뒤로가기 여러 번 눌러야 하는 문제 해결

비-AJAX 요청이면 기존처럼 리다이렉트

5) 댓글 & 대댓글 (트리)
원댓글

POST /posts/{postId}/comments

대댓글

POST /posts/{postId}/comments/{parentId}/reply

Comment.depth = parent.depth + 1

화면에서는 depth * 16px 들여쓰기

댓글 삭제

POST /posts/{postId}/comments/{commentId}/delete

본인/관리자만 가능

컨트롤러 분리: 댓글 관련 라우팅은 CommentController로 이동(기존 PostController.reply 중복 매핑 제거).

6) UI/UX
헤더(민트색) + 네비게이션 가로 100% 확장 (HTML 수정 최소화, CSS로 처리)

검색 범위 드롭다운 색상/크기 보정

로그인 폼은 적당한 너비(약 420px) 로 단정한 레이아웃

search.html의 페이지네이션을 list.html과 동일한 스타일/변수로 통일

스크립트 로딩 순서 고정: jQuery → Popper → Bootstrap

중복/누출 스크립트 제거(페이지 하단에 텍스트로 보이던 문제 해결)

템플릿 & 포인트
공통 레이아웃
templates/layout.html

헤더의 검색 폼: 드롭다운 + 키워드 입력 + 검색 버튼

드롭다운은 Bootstrap 기본 + 수동 토글 스크립트(Popper 미적용 환경 대비)

키워드 검증 스크립트: 공백 제거 & 빈 값 방지

목록/검색
templates/list.html / templates/search.html

currentPage, totalPages, keyword, scope 사용

검색 페이지네이션 링크도 keyword, scope 유지

상세(read)
templates/read.html

좋아요 버튼: AJAX 토글

댓글/대댓글:

답글 폼 토글(.reply-toggle → 같은 아이템 내 .reply-form)

들여쓰기:

html
복사
편집
th:style="|margin-left: ${(c.depth == null ? 0 : c.depth) * 16}px|"
(Thymeleaf 문자열 리터럴 파이프 사용으로 파싱 오류 해결)

주의: CSRF 태그는 주석 처리되어 있음(아래 참고).

컨트롤러 요약
PostController
GET /, /list : 목록/검색 분기

GET /posts/read/{id} : 상세 + 고유 조회수 증가 + 좋아요 상태/수

POST /posts/{id}/like : 좋아요 토글 (AJAX 시 JSON)

(글쓰기/삭제 등 일반 게시글 기능)

CommentController
POST /posts/{postId}/comments : 원댓글 등록

POST /posts/{postId}/comments/{parentId}/reply : 대댓글 등록

POST /posts/{postId}/comments/{commentId}/delete : 삭제(본인/관리자)

CommentService (핵심)
add(postId, content, nickname) : parent = null, depth = 0

reply(postId, parentId, content, nickname) : parent 지정, depth = parent.depth + 1

Lombok 경고 해결: children 컬렉션은 @Builder.Default 로 초기화

CSRF 관련 (현재 템플릿은 주석 처리됨)
read.html 내 폼들에 있는

html
복사
편집
<!-- <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}" /> -->
항목은 주석 처리되어 있습니다.

CSRF를 활성화하는 경우:

위 줄을 주석 해제하거나,

안전하게 다음처럼 사용:

html
복사
편집
<input type="hidden" th:if="${_csrf != null}"
       th:name="${_csrf?.parameterName}" th:value="${_csrf?.token}" />
CSRF를 비활성화하는 경우(개발용): Security 설정에서 disable.

자주 만나는 이슈 & 해결
드롭다운이 열리지 않음

Popper 미로딩/순서 문제 → jQuery → Popper → Bootstrap 순으로 스크립트 로드

수동 토글 스크립트 포함(의존성 없을 때도 동작)

검색어 없이 검색

폼 submit 가로채서 공백 제거, 빈 값이면 alert + focus

좋아요 취소 후 뒤로가기가 여러 번 필요

토글을 AJAX로 처리 + history.replaceState 적용 → 히스토리 누적 방지

Thymeleaf 식 오류
Could not parse as expression: "'margin-left:' + (c.depth * 16) + 'px'"

파이프 리터럴로 변경:

html
복사
편집
th:style="|margin-left: ${(c.depth == null ? 0 : c.depth) * 16}px|"
페이지 하단에 JS 코드가 텍스트로 보임

</html> 이후 중복/누락된 <script> 블록 제거

대댓글이 원댓글로 저장됨

reply(postId, parentId, ...)에서 반드시 parent 조회 후

java
복사
편집
.parent(parent)
.depth(parent.getDepth() + 1)
라우팅은 CommentController가 담당(중복 매핑 제거)

Tomcat "Invalid character found in method name [0x16...]"

HTTPS 패킷이 HTTP로 들어올 때 발생 → 로컬은 http로 접근하거나, 프록시/TLS 설정 일치시키기

스타일(CSS) 스니펫 (핵심만)
css
복사
편집
/* 헤더/네비 전체 너비 */
#header, #nav { width: 100%; }

/* 검색 드롭다운/인풋 그룹 높이/색상 보정 */
#header .input-group .btn,
#header .input-group .form-control {
  height: 36px;
  line-height: 36px;
}

.dropdown-menu {
  min-width: 140px;
}

.reply-form.d-none { display: none !important; }
디렉터리(주요)
swift
복사
편집
src/
 └─ main/
    ├─ java/com/example/Board_basic/
    │   ├─ Controller/
    │   │   ├─ PostController.java
    │   │   └─ CommentController.java
    │   ├─ Service/
    │   │   ├─ PostService.java
    │   │   └─ CommentService.java
    │   ├─ Entity/
    │   │   ├─ Post.java
    │   │   └─ Comment.java   // children: @Builder.Default
    │   └─ Repository/
    └─ resources/
        ├─ templates/
        │   ├─ layout.html
        │   ├─ list.html
        │   ├─ search.html
        │   ├─ read.html
        │   └─ write.html
        └─ static/css/app.css
