package sugaryo.springboot.tips.app.config;

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
	
	@Bean
	public Docket doclet() {
		return new Docket( DocumentationType.SWAGGER_2 )
				.select()
				.apis( RequestHandlerSelectors.any() )
				.paths( PathSelectors.any() )
				.build()
				.apiInfo( Api.info() );
	}
	
	private static class Api {
		
		private static ApiInfo info() {
			return new ApiInfoBuilder()
					.title( "sugaryo api / SpringBoot" )
					.description( "swagger demo." )
					.version( "0.0.1" )
					.contact( new Contact( "sugaryo", "https://github.com/sugaryo", "ryo.sugawara@gmail.com" ) )
					.license( "sample license." )
					.licenseUrl( "sample license url." )
					.termsOfServiceUrl( "" )
					.build();
		}
	}
}
