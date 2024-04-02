package com.study.Ex12H2DB;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Member;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

//@RequiredArgsConstructor
public class MemberRepositoryTest extends Ex12H2DbApplicationTests {
    //테스트 클래스에서는 생성자 주입 안됨.
    @Autowired
    MemberRepository memberRepository;

    @BeforeAll  //static 메소드
    static void setup(){
        System.out.println("@BeforeAll - 클래스를 초기화시 한번 수행");
    }
    @BeforeEach //non-static 메소드
    void init(){
        System.out.println("@BeforeEach - @Test 메소드를 호출시마다 한번 수행");
        save();
    }
    @Test //테스트할 메소드에 사용
    @DisplayName("save 테스트") //콘솔에 출력되는 메소드 이름
    public void save(){
        memberRepository.save(new MemberEntity(Long.valueOf(1),
                "hong", "1234", "홍길동", "ROLE_USER", LocalDate.parse("2023-01-01")
        ));
        memberRepository.save(new MemberEntity(Long.valueOf(2),
                "tom", "1234", "톰아저씨", "ROLE_USER", LocalDate.parse("2023-02-01")
        ));
        memberRepository.save(new MemberEntity(Long.valueOf(3),
                "admin", "1234", "관리자", "ROLE_ADMIN", LocalDate.parse("2023-03-01")
        ));

        List<MemberEntity> list = memberRepository.findAll();
        for( MemberEntity m : list ){
            System.out.println( m.getUserName() );
        }

        //단정함수 (Assert Function)
        //assertEquals( 3, list.size() );
        //assertEquals( 4, list.size() );
    }
    @Test
    @DisplayName("findById 테스트")
    public void findById(){
        //Optional 클래스 : JDK 9부터 제공. null safety를 제공한다.
        Optional<MemberEntity> member = memberRepository.findById( 1L );
        member.ifPresent( unwrappedMemberEntity -> {
            //null이 아닐때 수행되는 람다식
            System.out.println( unwrappedMemberEntity.getUserName() );
            assertEquals( "홍길동", unwrappedMemberEntity.getUserName() );
        });
        if( member.isPresent() ) {
            String name = member.get().getUserName();
            assertEquals( "홍길동", name );
        }else{
            fail("member 값 가져오기 실패");
        }
        member.ifPresentOrElse((unwrappedMemberEntity) -> {
            // null이 아닐 때 수행되는 람다식
            System.out.println(unwrappedMemberEntity.getUserName());
            assertEquals("홍길동", unwrappedMemberEntity.getUserName());
        }, () -> fail("member 엔티티가 null입니다."));
    }
    @Test
    @DisplayName("count 테스트")
    public void count(){
        //SQL : select count(*) from member
        Long count = memberRepository.count();
        System.out.println("count : " + count );
        assertEquals( 3, count);
    }
    @Test
    @DisplayName("쿼리메소드 테스트")
    public void query(){
        //패턴 : find + By + 필드이름 + And + 필드이름
        List<MemberEntity> list =
                memberRepository.findByUserId("tom");
        assertEquals( 1, list.size() );
        assertEquals( "톰아저씨", list.get(0).getUserName() );

        List<MemberEntity> list2 =
                //SQL : select * from member where user_id = :userId
                //      and user_name = :userName order by id desc limit 5;
                memberRepository.findFirst5ByUserIdAndUserNameOrderByIdDesc(
                        "hong", "홍길동"
                );
        assertEquals( 1, list2.size() );

        Boolean isExist =
                memberRepository.existsByJoindateLessThanEqual( LocalDate.now() );
        System.out.println( "isExist : " + isExist );

        long count = memberRepository.countByUserNameIgnoreCaseLike("길");
        System.out.println( "count : " + count);

    }
    @Test
    @DisplayName("JPQL 테스트")
    public void jpqlQuery(){
        List<MemberEntity> list =
                memberRepository.findByUserId_JPQL_Query("tom");
        assertEquals(1, list.size());
        assertEquals("톰아저씨", list.get(0).getUserName());
    }
    @Test
    @DisplayName("Native Query 테스트")
    public void nativeQuery(){
        List<MemberEntity> list =
                memberRepository.findByUserId_nativeQuery("admin");
        assertEquals(1, list.size());
        assertEquals("관리자", list.get(0).getUserName());
    }
    @Test
    @DisplayName("update 테스트")
    public void update(){
        Optional<MemberEntity> member =
                memberRepository.findById(1L);

        member.ifPresentOrElse( (newMember) -> {
            assertEquals("hong", newMember.getUserId());
            newMember.update("hong2",
                            newMember.getUserPw(),
                            newMember.getUserName(),
                            newMember.getUserRole());
            memberRepository.save(newMember);

            findById();
        }, () -> {
            fail("member 엔티티가 null입니다.");
        } );

    }
    @Test
    @DisplayName("update native SQL 테스트")
    public void update_nativeSql(){
        int result = memberRepository.updateById_nativeQuery(1L, "hong3");
        assertEquals(1, result);

        Optional<MemberEntity> member =
                memberRepository.findById(1L);
        assertEquals("hong3", member.get().getUserId() );
    }

//연습문제
//아래와 같이 H2DB를 초기화 하고, 아래 결과를 출력하시오.

//1. member 테이블 안에 암호가 "1234"인 회원이 있는지 테스트 하시오.
//2. 23년도 3월에 가입한 회원의 수가 1인지 테스트하시오.
//3. "lee"라는 아이디로 회원가입하고자 할때, 아이디 중복인지 테스트하시오.
//4. "tom"이라는 아이디의 회원정보를 수정하고, 잘 수정되었는지 테스트하시오.
//    톰아저씨 -> 마이클, 암호 -> 3456
//* 쿼리 메소드 또는 Native SQL, JPQL 방법 중 하나를 사용하시오.

    @Test
    @DisplayName("연습문제")
    public void practice(){
        assert !memberRepository.findByUserPw("1234").isEmpty();
        assertEquals(1, memberRepository.findByJoindateYearAndMonth_nativeQuery(2023, 2));
        assert memberRepository.findByUserName("lee").isEmpty();
        memberRepository.updateByUserName_nativeQuery("톰아저씨", "마이클", "3456");
        MemberEntity tom = memberRepository.findFirstByUserName("마이클");
        assertEquals("3456", tom.getUserPw());

    }


}
