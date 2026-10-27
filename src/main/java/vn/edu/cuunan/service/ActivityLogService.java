package vn.edu.cuunan.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.cuunan.entity.ActivityLog;
import vn.edu.cuunan.entity.User;
import vn.edu.cuunan.repository.ActivityLogRepository;

import java.util.List;

@Service
public class ActivityLogService {

    private static final Logger log = LoggerFactory.getLogger(ActivityLogService.class);

    private final ActivityLogRepository activityLogRepository;

    public ActivityLogService(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    @Transactional
    public ActivityLog log(User user, String action, String targetEntity, String details, String ipAddress) {
        ActivityLog auditLog = new ActivityLog(user, action, targetEntity, details, ipAddress);
        try {
            return activityLogRepository.save(auditLog);
        } catch (Exception e) {
            log.warn("Không thể lưu activity log: {}", e.getMessage());
            return auditLog;
        }
    }

    @Transactional(readOnly = true)
    public List<ActivityLog> getRecentLogs(int limit) {
        return activityLogRepository.findTop100ByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public Page<ActivityLog> getLogsPage(Pageable pageable) {
        return activityLogRepository.findAllByOrderByCreatedAtDesc(pageable);
    }
}
