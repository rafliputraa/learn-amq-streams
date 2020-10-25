package org.mycompany.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestParamType;
import org.apache.camel.spi.RestConfiguration;
import org.springframework.stereotype.Component;

@Component
public class HelloWorldRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		
		restConfiguration()
		  .contextPath("/v1") 
		  .host("{{server.address}}")
		  .port("{{conf.port}}")
		  .component("jetty")
		  ;
		
		rest()
		  
		  .post("/print").id("print-your-message")
		  .param().name("message").type(RestParamType.query).endParam()
		  .to("direct:print-your-message")
		  ;
		
		String topic = "{{helloWorldTopic}}";
		String kafkaServer = "brokers={{broker}}";
		String sslTruststoreLocation = "sslTruststoreLocation={{truststore.location}}";
		String sslTruststorePassword = "sslTruststorePassword={{truststore.password}}";
		
//		Without SSL
//		String toKafka = new StringBuilder().append("kafka:").append(topic).append("?").append(kafkaServer).toString();
//		String fromKafka = new StringBuilder().append("kafka:").append(topic).append("?").append(kafkaServer).toString();
		
		String toKafka = new StringBuilder().append("kafka:").append(topic).append("?").append(kafkaServer).append("&")
				.append("securityProtocol=SSL").append("&").append(sslTruststoreLocation).append("&")
				.append(sslTruststorePassword).toString();
		String fromKafka = new StringBuilder().append("kafka:").append(topic).append("?").append(kafkaServer).append("&")
				.append("securityProtocol=SSL").append("&").append(sslTruststoreLocation).append("&")
				.append(sslTruststorePassword).toString();
		
		

		from("direct:print-your-message")
		.setBody(simple("Message: ${header.message}"))
		.to(toKafka);
		
		from(fromKafka)
	    .log("Message received from Kafka : ${body}");
		
	}

}
