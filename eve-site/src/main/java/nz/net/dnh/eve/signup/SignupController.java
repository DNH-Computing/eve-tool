package nz.net.dnh.eve.signup;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import nz.net.dnh.eve.account.*;

@Controller
public class SignupController {
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private UserService userService;
	
	@RequestMapping(value = "signup")
	public SignupForm signup() {
		return new SignupForm();
	}
	
	@RequestMapping(value = "signup", method = RequestMethod.POST)
	public String signup(@Valid @ModelAttribute SignupForm signupForm, Errors errors) {
		if (errors.hasErrors()) {
			return null;
		}
		
		Account account = this.accountRepository.save(signupForm.createAccount());
		this.userService.signin(account);
		
		return "redirect:/";
	}
}
