package com.vanakkam.skillroute.repository;

import com.vanakkam.skillroute.model.EmailLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EmailLogRepository extends JpaRepository<EmailLog, Long> {
    List<EmailLog> findByUserIdAndEmailType(Long userId, String emailType);
    boolean existsByUserIdAndEmailType(Long userId, String emailType);
}