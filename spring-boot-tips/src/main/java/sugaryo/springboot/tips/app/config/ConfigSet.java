package sugaryo.springboot.tips.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConfigSet {
	@Autowired
	ApiConfig api;
}
