package testCodeCourse.demo.spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import testCodeCourse.demo.spring.client.MailSendClient;
import testCodeCourse.demo.spring.domain.history.MailSendHistory;
import testCodeCourse.demo.spring.repository.MailSendHistoryRepository;

@Service
@RequiredArgsConstructor
public class MailService {

    private final MailSendClient mailSendClient;
    private final MailSendHistoryRepository mailSendHistoryRepository;

    // 나는 sendEmail 만 stubbing 하고싶고
    // 아래의 a,b,c,d 는 원래객체대로 작동했으면 좋겠을때.
    // mock 으로 돌리면 a,b,c,d 에 기능인 log 찍기가 발동안한다. 가짜니까
    public boolean sendMail(String fromEmail, String toEmail, String subject, String content) {
        boolean result = mailSendClient.sendEmail(fromEmail,toEmail,subject,content);
        if(result){
            mailSendHistoryRepository.save(MailSendHistory.builder()
                    .fromEmail(fromEmail)
                    .toEmail(toEmail)
                    .subject(subject)
                    .content(content)
                    .build()
            );
            mailSendClient.a();
            mailSendClient.b();
            mailSendClient.c();
            mailSendClient.d();
            return true;
        }
        return false;
    }


}
