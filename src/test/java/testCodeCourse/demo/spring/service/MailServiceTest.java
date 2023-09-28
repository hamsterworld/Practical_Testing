package testCodeCourse.demo.spring.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import testCodeCourse.demo.spring.client.MailSendClient;
import testCodeCourse.demo.spring.domain.history.MailSendHistory;
import testCodeCourse.demo.spring.repository.MailSendHistoryRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

// 아래처럼 설정해줘야지 나 mock 으로 뭐만들어줄거야가 된다.
@ExtendWith(MockitoExtension.class)
class MailServiceTest {

    @Mock
    private MailSendClient mailSendClient;

    // 이렇게 anotation Spy 도 따로있다.
    // 만약에 MailSendClient 가 기능이 많다고 생각해보자.
    // spy 는 기본적으로 진짜 객체를 기반으로 만들어진다.
//    @Spy
//    private MailSendClient mailSendClient;

    @Mock
    private MailSendHistoryRepository mailSendHistoryRepository;

    @InjectMocks
    private MailService mailService;

    @Test
    @DisplayName("메일 전송 테스트")
    void sendMail() {
        // 준비
        // 아래처럼 mock 만드는것을 위에서처럼만들어줄수있다.
//        MailSendClient mailSendClient = mock(MailSendClient.class);
//        MailSendHistoryRepository mailSendHistoryRepository = mock(MailSendHistoryRepository.class);

//        MailService mailService = new MailService(mailSendClient,mailSendHistoryRepository);

        // 아래처럼 해주는게 stubbing
//        when(mailSendClient.sendEmail(anyString(),anyString(),anyString(),anyString()))
//                .thenReturn(true);

        // 생각해보니 준비단계에는 given 인데 여기에 when 이 들어가있다. 그래서 위에것을 대체하기위해서
        // given 이 좀더 자연스럽긴하다. BDDMockito 는 그냥 Mockito 를 상속받아서 매소드명만바꾼것 다른건 모두동일하다.
        // 그래서 앞으로 BDDMockito 를 사용하면 될것같다.
        BDDMockito.given(mailSendClient.sendEmail(anyString(),anyString(),anyString(),anyString()))
                .willReturn(true);

        // spy 는 do로 실행해줘야한다.
//        doReturn(true)
//                .when(mailSendClient)
//                .sendEmail(anyString(),anyString(),anyString(),anyString());


        // 실행
        boolean result = mailService.sendMail("", "", "", "");


        // 검증
        assertThat(result).isTrue();
        // 아래가 조금 spy 기능이랑 비슷하다.
        verify(mailSendHistoryRepository,times(1)).save(any(MailSendHistory.class));
    }
}