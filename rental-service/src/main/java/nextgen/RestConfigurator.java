package nextgen;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class RestConfigurator extends RouteBuilder {

	@Autowired
	Environment environment;

	@Override
	public void configure() throws Exception {
		restConfiguration()
		.component("servlet")
		.bindingMode(RestBindingMode.json)
		.contextPath(environment.getProperty("camelrest.contextPath"))
		.port(environment.getProperty("camelrest.port"))
		.apiContextPath("/api-docs")
		.apiProperty("cors", "true")
		.apiProperty("api.title", environment.getProperty("camel.springboot.name"))
		.apiProperty("api.version", environment.getProperty("camelrest.apiversion"))
		.host(environment.getProperty("camelrest.host"))
		.dataFormatProperty("prettyPrint", "true");
		
	}


}
