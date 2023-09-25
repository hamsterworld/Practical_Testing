package testCodeCourse.demo.spring.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing // 이게 원래 DemoApplication 에 있던건데 여기로 옮겼다. 테스트에서 Bean 을 주입을못해서 presentationLayer Test 2강 11분 32초
@Configuration
public class JpaAuditingConfig {
}
