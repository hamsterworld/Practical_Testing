package testCodeCourse.demo.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import testCodeCourse.demo.spring.domain.history.MailSendHistory;

@Repository
public interface MailSendHistoryRepository extends JpaRepository<MailSendHistory,Long> {


}
