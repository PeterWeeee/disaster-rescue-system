package vn.edu.cuunan;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class DisasterRescueApplication {

    @PostConstruct
    public void init() {
        // Cố định múi giờ hệ thống là GMT+7 (Asia/Ho_Chi_Minh) cho toàn bộ ứng dụng
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
    }

    public static void main(String[] args) {
        SpringApplication.run(DisasterRescueApplication.class, args);
    }
}
