package sugaryo.springboot.tips.app.controller.rest;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/swagger/demo")
public class SwaggerDemoController {
	
	@GetMapping
	public String get() {
		return "{}";
	}
	
	@PostMapping
	public String post( @RequestBody String body ) {
		return body;
	}
	
	@DeleteMapping
	public String delete() {
		return "deleted.";
	}
}
