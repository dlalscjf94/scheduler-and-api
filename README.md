# 스케줄러 및 api 서버 구현

## 요구사항
- 특정 경로의 파일을 매일 자정에 조회하여 정보를 추출하여 DB에 저장한다.
    - 파일의 형태는 csv, txt 등이 있고 추가적인 파일 형태도 생길 수 있다는 점을 고려하여 설계
    - 파일을 읽어서 DB에 저장할 때는 파일 전체를 모두 입력해야 한다. 파일 중간에 오류가 있을 경우 미입력 처리
      > 파일 형식 : 시간|가입자수|탈퇴자수|결제금액|사용금액|매출금액 ( 각 금액 정보는 임의의 값으로 입력 ) 
      > 파일 형태에 따라 구분자는 임의 선택 ( | - ,  등 )
      
- 1번 기능에 의해 저장된 정보를 조회, 등록, 수정, 삭제하는 api 구현 (Restful API)
    - 시간대별 가입자수 조회, 시간대별 결제금액 조회 등 시간대별 각 항목 입력, 조회, 수정, 삭제 API 구현
    - 각 요청 연산에 맞는 적절한 HTTP 메서드 사용
    - 모든 API은 설정파일(properties or yml)에 등록된 아이피에서만 접근을 허용, 나머지 deny 처리
    - 각 API 별로 공통적으로 request 시간, response 시간, 걸린 시간을 공통적으로 로그 기록
      > 로그 파일명 : 프로세스명_YYYYMMDDHH.log    * 로그 형식 : [YYYY-MM-DD HH:MM:SS]-[로그레벨]-[메서드]-[로그 내용]
    - 입력에 대한 유효성 검증을 하고 잘못된 요청에 대해서는 공통 예외, Response 처리

- api 호출 client 인증 및 rate limit 처리 구현
    
    - 인가받지 않은 사용자가 무분별하게 api를 호출할 수 없도록 api 호출시 client 인증 처리 구현
    - 임계치를 넘어가는 과도한 API 호출을 방어할 수 있도록 rate limit 구현
    
## 개발 환경
  - 기본 환경
    - IDE: IDE: IntelliJ IDEA Ultimate
    - OS: Mac OS X
    - GIT

  - 서버 환경
    - JAVA 1.8
    - Spring Boot 2.7.6
    - Mybatis
    - MySQL 8.0
    - gradle
    - swagger     
    - docker
  
## ERD
  - 주어진 요구사항을 통해 ERD를 구성한다. (dbdiagram.io 사용)
![Untitled](https://user-images.githubusercontent.com/30383018/210177780-e8abee51-f22a-42ed-a2c5-13789cf05210.png)
  - TABLE은 각각 파일의 기본정보를 저장하는 fileinfo 테이블 및 filedata 테이블 총 2개로 구성
  - fileinfo 테이블의 각 컬럼 (id : 파일_id , file_name : 파일명, ext_name : 파일 확장자명, file_path : 파일 경로, jobdate : 작업 시간) 
    - 추가적인 파일 형태도 생길 수 있다는 점은 파일 확장자명 컬럼(ext_name)을 통해서 대응
  - filedata 테이블의 각 컬럼 (file_id : 파일_id, data_time: 시간, subs_num : 가입자 수, resign_num : 탈퇴자 수, pay_amount : 결제금액, used_amount : 사용금액, sales_amount : 매출금액)
    - fileinfo.id > filedata.file_id 는 one-to-many 의 관계를 가진다.
