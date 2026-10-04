package dev.azhagesan.portfolio;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;
@Service
public class ContactService {
 private final RestClient client;
 private final boolean enabled;
 private final String key, from, to;
 public ContactService(@Value("${portfolio.brevo.url}") String url,
   @Value("${portfolio.brevo.key}") String key,
   @Value("${portfolio.contact.enabled}") boolean enabled,
   @Value("${portfolio.contact.from}") String from,
   @Value("${portfolio.contact.to}") String to) {
  var http=HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
  var factory=new JdkClientHttpRequestFactory(http);
  factory.setReadTimeout(Duration.ofSeconds(10));
  client=RestClient.builder().baseUrl(url).requestFactory(factory).build();
  this.key=key; this.enabled=enabled; this.from=from; this.to=to;
 }
 public void send(ContactRequest request) {
  if (request.website()!=null && !request.website().isBlank())
   throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Message could not be sent.");
  if (!enabled || key.isBlank() || from.isBlank())
   throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"The contact service is unavailable. Please use the email link.");
  var payload=Map.of("sender",Map.of("name","Azhagesan Portfolio","email",from),
    "to",List.of(Map.of("email",to)),"replyTo",Map.of("email",request.email()),
    "subject","Portfolio contact message",
    "textContent","From: "+request.name()+"\nEmail: "+request.email()+"\n\n"+request.message());
  try {
   var response=client.post().uri("/smtp/email").header("api-key",key)
    .contentType(MediaType.APPLICATION_JSON).body(payload).retrieve().body(Map.class);
   if (response==null || !(response.get("messageId") instanceof String id) || id.isBlank())
    throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"Email delivery could not be confirmed. Please use the email link.");
  } catch (RestClientException e) {
   // Provider responses and credentials must not be exposed to visitors.
   throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"Email delivery failed. Please try again or use the email link.");
  }
 }
}
