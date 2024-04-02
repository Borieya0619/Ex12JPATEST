package com.study.Ex12H2DB;

//데이타 모델링 클래스(데이타를 담고 있는)의 종류
//1. DTO 클래스 : Data Transfer Object, 데이타가 자주 바뀜. 로직(코드) 포함 안됨.
//             : 다른 계층간에 데이타 전송시 사용.
//2. VO 클래스 : View Object, 데이타가 안바뀜. 로직이 포함됨.
//            : 바뀌지 않는 데이타 보관시 사용.
//3. Entity 클래스 : DB 테이블 1:1 매칭, 로직(코드) 포함 안됨. JPA에서 사용.

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

//@Entity : DB테이블과 1:1로 매칭되는 JPA 클래스에 사용함.
//@Table : 클래스이름이 테이블이름으로 기본 설정되는데("memberEntity")
//         테이블이름을 바꿔주는데 사용
@Entity
@Table(name="member")
@Getter
//@Setter : 비추천 - 엔티티 객체에 set함수를 사용하면, 실제 DB 중복적용함.
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberEntity {
    //@Id : 기본키 id열로 사용한다는 의미.
    @Id
    //@GeneratedValue : 해당 ID값을 어떻게 생성할지 전략을 선택한다.
    // 1. IDENTITY : MySQL, MariaDB, PostreSQL, H2DB
    // 2. SEQUENCE : Oracle, PostreSQL
    // 3. AUTO : 자동으로 선택함.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;        //bigint
    //@Column : DB열과 매칭 - 변수이름과 DB열의 이름이 다를때 매칭해준다.
    @Column(name="user_id")
    //DB필드 "user_id" -> "userId" 언더바가 생략되어 매핑됨.
    private String userId;  //varchar
    @Column(name="user_pw")
    private String userPw;
    @Column(name="user_name")
    private String userName;
    @Column(name="user_role")
    private String userRole;
    @Column
    //@JsonFormat : @RequestBody @ResponseBody 매핑
    //@DateTimeFormat : @RequestParam @ModelAttribute 매핑
    //     DB Date 데이타를 가져올 때, 형식화해줌.
    @DateTimeFormat(pattern = "yyyy-mm-dd")
    private LocalDate joindate;

    public void update(String userId, String userPw, String userName,
                       String userRole){
        this.userId = userId;
        this.userPw = userPw;
        this.userName = userName;
        this.userRole = userRole;
    }
}
