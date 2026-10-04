package dev.azhagesan.portfolio;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api")
public class ProfileController {
 private final String profile;
 public ProfileController() throws IOException {
  try (var stream = new ClassPathResource("profile.json").getInputStream()) {
   profile = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
  }
 }
 @GetMapping(value="/profile", produces=MediaType.APPLICATION_JSON_VALUE)
 public String profile() { return profile; }
 @GetMapping("/health") public Map<String, String> health() { return Map.of("status", "ok"); }
}
