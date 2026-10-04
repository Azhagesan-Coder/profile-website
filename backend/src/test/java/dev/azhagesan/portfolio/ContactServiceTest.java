package dev.azhagesan.portfolio;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;
class ContactServiceTest {
 final ContactRequest request=new ContactRequest("Visitor","visitor@example.com","Hello, I would like to connect.","");
 @Test void disabledOrMissingKeyCannotClaimDelivery() {
  assertThrows(ResponseStatusException.class,()->new ContactService("http://localhost:1/v3","",true,"sender@example.com","owner@example.com").send(request));
  assertThrows(ResponseStatusException.class,()->new ContactService("http://localhost:1/v3","test",false,"sender@example.com","owner@example.com").send(request));
 }
 @Test void sendsCorrectRequestAndRequiresAcceptance() throws Exception {
  var body=new AtomicReference<String>(); var key=new AtomicReference<String>();
  var server=HttpServer.create(new InetSocketAddress("127.0.0.1",0),0);
  server.createContext("/v3/smtp/email",e->{
   key.set(e.getRequestHeaders().getFirst("api-key"));
   body.set(new String(e.getRequestBody().readAllBytes(),StandardCharsets.UTF_8));
   var response="{\"messageId\":\"test-message\"}".getBytes(StandardCharsets.UTF_8);
   e.getResponseHeaders().add("Content-Type","application/json");
   e.sendResponseHeaders(201,response.length);e.getResponseBody().write(response);e.close();
  }); server.start();
  try {
   service(server).send(request); assertEquals("test-key",key.get());
   assertTrue(body.get().contains("\"replyTo\":{\"email\":\"visitor@example.com\"}"));
   assertTrue(body.get().contains("owner@example.com"));assertTrue(body.get().contains("sender@example.com"));
   assertTrue(body.get().contains("Hello, I would like to connect."));
  } finally {server.stop(0);}
 }
 @Test void providerFailureDoesNotLeakDetails() throws Exception {
  var server=HttpServer.create(new InetSocketAddress("127.0.0.1",0),0);
  server.createContext("/v3/smtp/email",e->{
   var bytes="private-provider-error".getBytes(StandardCharsets.UTF_8);
   e.sendResponseHeaders(401,bytes.length);e.getResponseBody().write(bytes);e.close();
  }); server.start();
  try {var e=assertThrows(ResponseStatusException.class,()->service(server).send(request));
   assertEquals(503,e.getStatusCode().value());assertFalse(e.getReason().contains("private-provider-error"));
  } finally {server.stop(0);}
 }
 @Test void emptyAcceptanceResponseFails() throws Exception {
  var server=HttpServer.create(new InetSocketAddress("127.0.0.1",0),0);
  server.createContext("/v3/smtp/email",e->{
   e.getResponseHeaders().add("Content-Type","application/json");
   e.sendResponseHeaders(201,2);e.getResponseBody().write("{}".getBytes());e.close();
  }); server.start();
  try {assertThrows(ResponseStatusException.class,()->service(server).send(request));} finally {server.stop(0);}
 }
 private ContactService service(HttpServer server) {
  return new ContactService("http://127.0.0.1:"+server.getAddress().getPort()+"/v3","test-key",true,"sender@example.com","owner@example.com");
 }
}
