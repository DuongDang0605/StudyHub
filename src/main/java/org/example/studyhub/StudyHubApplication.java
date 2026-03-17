package org.example.studyhub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.awt.Desktop;
import java.net.URI;

@SpringBootApplication
public class StudyHubApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(StudyHubApplication.class);
        application.setHeadless(false);
        application.run(args);
    }

    @EventListener({ApplicationReadyEvent.class})
    public void openBrowserAfterStartup() {
        try {
            String url = "http://localhost:8085/home";

            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
                System.out.println("🚀 Đã tự động mở trình duyệt tại: " + url);
            } else {
                Runtime runtime = Runtime.getRuntime();
                String os = System.getProperty("os.name").toLowerCase();

                if (os.contains("win")) {
                    runtime.exec("rundll32 url.dll,FileProtocolHandler " + url); // Windows
                } else if (os.contains("mac")) {
                    runtime.exec("open " + url); // MacOS
                } else if (os.contains("nix") || os.contains("nux")) {
                    runtime.exec("xdg-open " + url); // Linux
                }
            }
        } catch (Exception e) {
            System.err.println("⚠️ Không thể tự động mở trình duyệt: " + e.getMessage());
        }
    }
}