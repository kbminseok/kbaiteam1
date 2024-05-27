package com.mysite.sbb.user;

import java.security.Principal;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.answer.AnswerService;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

	private final UserService userService;
	private final QuestionService questionService;
	private final AnswerService answerService;
	private final PasswordEncoder passwordEncoder;

	@GetMapping("/signup")
	public String signup(UserCreateForm userCreateForm) {
		return "signup_form";
	}
	

	@GetMapping("/mypage")
	@PreAuthorize("isAuthenticated()")
	public String showmyPage(Model model, Principal principal)
	{
		SiteUser user = userService.getUser(principal.getName());

		if(user == null) {
			throw new DataNotFoundException("사용자를 찾을 수 없습니다.");
		}
		model.addAttribute("user", user);

		Long questionCount = questionService.getQuestionCount(user);
		model.addAttribute("questionCount", questionCount);

		List<Question> questionList = questionService.getQuestionTop5LatestByUser(user);
		model.addAttribute("questionList", questionList);

		Long answerCount = answerService.getAnswerCount(user);
		model.addAttribute("answerCount", answerCount);

		List<Answer> answerList = answerService.getAnswerTop5LatestByUser(user);
		model.addAttribute("answerList", answerList);

		return "my_page";
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/change/passwd")
	public String showChangePW(@ModelAttribute("pwChangeForm") PWChangeForm pwChangeForm) {
		return "pw_change";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/change/passwd")
	public String changePW(@Valid @ModelAttribute("pwChangeForm") PWChangeForm pwChangeForm, BindingResult bindingResult, Model model,
		Principal principal, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			return "pw_change";
		}

		SiteUser user = userService.getUser(principal.getName());

		// 이전 패스워드와 맞지 않을경우
		if (!passwordEncoder.matches(pwChangeForm.getPrePassword(), user.getPassword())) {
			bindingResult.reject("notMatchPW", "이전 비밀번호가 일치하지 않습니다.");
			return "user/pw_change";
		}
		// 새 비밀번호, 비밀번호 확인 창 일치하지 않을경우
		if (!pwChangeForm.getNewPassword1().equals(pwChangeForm.getNewPassword2())) {
			bindingResult.reject("notMatchNewPW", "새 비밀번호와 확인이 일치하지 않습니다.");
			return "user/pw_change";
		}

		userService.updatePassWord(user, pwChangeForm.getNewPassword1());

		// 로그인 페이지에서 보여줄 성공 메시지를 플래시 애트리뷰트로 추가
		redirectAttributes.addFlashAttribute("successMessage", "비밀번호 변경 성공!");

		return "redirect:/user/mypage";
	}
	
	@PostMapping("/signup")
	public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "signup_form";
		}

		if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
			bindingResult.rejectValue("password2", "passwordInCorrect", "2개의 패스워드가 일치하지 않습니다.");
			return "signup_form";
		}

		try {
			userService.create(userCreateForm.getUsername(), userCreateForm.getEmail(), userCreateForm.getPassword1());
		} catch (DataIntegrityViolationException e) {
			e.printStackTrace();
			bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
			return "signup_form";
		} catch (Exception e) {
			e.printStackTrace();
			bindingResult.reject("signupFailed", e.getMessage());
			return "signup_form";
		}

		return "redirect:/";
	}

	@GetMapping("/login")
	public String login() {
		return "login_form";
	}
}
