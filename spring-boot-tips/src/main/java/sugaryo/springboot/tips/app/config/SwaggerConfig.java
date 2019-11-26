package sugaryo.springboot.tips.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {
	
	@Autowired
	ConfigSet config;
	
	@Bean
	public Docket doclet() {
		return new Docket( DocumentationType.SWAGGER_2 )
				.select()
				.apis( RequestHandlerSelectors.any() )
				.paths( PathSelectors.any() )
				.build()
				.apiInfo( as( this.config.api ) );
	}
	
	private static ApiInfo as( ApiConfig api ) {
		return new ApiInfoBuilder()
				.title( api.title )
				.description( api.description )
				.version( api.version )
				.contact( new Contact(
						api.contact.name,
						api.contact.url,
						api.contact.email ) )
				.license( api.lisence.name )
				.licenseUrl( api.lisence.url )
				.termsOfServiceUrl( api.termsOfService.url )
				.build();
	}
}
