package sugaryo.springboot.tips.app.controller.view;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("view/what-time-is-it")
@Controller
public class WhatTimeIsItController {
	
	@GetMapping()
	public String view( Model model ) {
		
		String now = LocalDateTime.now().format( DateTimeFormatter.ISO_DATE_TIME );
				
		model.addAttribute( "datetime", now );
		
		return "wtii"; // ここで返すのはView名、つまり `wtii.html` の "wtii" を返す。 
	}
	
}
