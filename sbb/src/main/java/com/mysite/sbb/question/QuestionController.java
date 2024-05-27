package com.mysite.sbb.question;

import java.security.Principal;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.answer.AnswerForm;
import com.mysite.sbb.answer.AnswerService;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/question")
@RequiredArgsConstructor
@Controller
public class QuestionController {

	private final QuestionService questionService;
	private final UserService userService;
	private final AnswerService answerService;

	@GetMapping("/list/{type}")
	public String list(Model model, @PathVariable(required = true, name = "type") String type,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "kw", defaultValue = "") String kw) {
		int category = switch (type) {
		case "qna" -> QuestionEnum.QNA.getStatus();
		case "free" -> QuestionEnum.FREE.getStatus();
		case "bug" -> QuestionEnum.BUG.getStatus();
		default -> throw new RuntimeException("올바르지 않은 접근입니다.");
	};

	model.addAttribute("boardName", category);
	Page<Question> paging = questionService.getList(category, page, kw);
		model.addAttribute("paging", paging);
		model.addAttribute("kw", kw);
		return "question_list";
	}
	

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/list/byQuestion/{id}")
	public String personalListByQuestionUserId(Model model, @PathVariable(required = true, name = "id") Long id,
		@RequestParam(name="page", required = false, defaultValue = "0") int page, @RequestParam(name= "kw",required = false, defaultValue = "") String kw,
		Principal principal) {

		SiteUser siteUser = userService.getUser(principal.getName());

		if(siteUser.getId() != id) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "조회 권한이 없습니다.");
		}

		Page<Question> paging = questionService.getPersonalQuestionListByQuestionAuthorId(page, kw, id);
		model.addAttribute("user", siteUser);
		model.addAttribute("paging", paging);
		// 동일한 템플릿 사용 -> 총 답변수로 표기하기 위함
				model.addAttribute("type", "총 질문수");
		return "personal_list";
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/list/byAnswer/{id}")
	public String personalListByAnswerUserId(Model model, @PathVariable(required = true, name = "id") Long id,
		@RequestParam(name="page", required = false, defaultValue = "0") int page, @RequestParam(name= "kw",required = false, defaultValue = "") String kw, Principal principal) {
		SiteUser siteUser = userService.getUser(principal.getName());

		if (siteUser.getId() != id) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "조회 권한이 없습니다.");
		}

		// 총 답변 수 View에 나타내기 위함
		Long answerCount = answerService.getAnswerCount(siteUser);
		model.addAttribute("answerCount", answerCount);

		Page<Question> paging = questionService.getPersonalQuestionListByAnswer_AuthorId(page, kw, id);
		model.addAttribute("user", siteUser);
		model.addAttribute("paging", paging);
		// 동일한 템플릿 사용 -> 총 답변수로 표기하기 위함
		model.addAttribute("type", "총 답변수");
		return "personal_list";
	}

	@GetMapping(value = "/detail/{id}")
	public String detail(Model model, @PathVariable(required = true, name = "id") Integer id, AnswerForm answerForm, @RequestParam(name="page", required = false, defaultValue = "0") int page,
			@RequestParam(name="sort", required = false, defaultValue = "0") String sort) {
		Question question = questionService.getQuestion(id);
		Page<Answer> paging = answerService.getAnswerPage(question, page,sort);
		questionService.updateQuestionView(question);
		model.addAttribute("question", question);
		model.addAttribute("paging", paging);
		return "question_detail";
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/create/{type}")
	public String showCreate(@PathVariable(required = true, name = "type") String type, QuestionForm questionForm, Model model) {
		switch (type) {
			case "qna" -> model.addAttribute("boardName", "질문과답변 작성");
			case "free" -> model.addAttribute("boardName", "자유게시판 작성");
			case "bug" -> model.addAttribute("boardName", "버그및건의 작성");
			default -> throw new RuntimeException("올바르지 않은 접근입니다.");
		}
		return "question_form";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/create/{type}")
	public String questionCreate(@Valid QuestionForm questionForm, BindingResult bindingResult, @PathVariable(required = true, name = "type") String type, Principal principal) {
		if (bindingResult.hasErrors()) {
			return "question_form";
		}

		int category = switch (type) {
			case "qna" -> QuestionEnum.QNA.getStatus();
			case "free" -> QuestionEnum.FREE.getStatus();
			case "bug" -> QuestionEnum.BUG.getStatus();
			default -> throw new RuntimeException("올바르지 않은 접근입니다.");
		};

		SiteUser siteUser = this.userService.getUser(principal.getName());
		questionService.create(questionForm.getSubject(), questionForm.getContent(), siteUser, category);
		return "redirect:/question/list/%s".formatted(type);
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/modify/{id}")
	public String questionModify(QuestionForm questionForm, @PathVariable("id") Integer id, Principal principal, Model model) {
		Question question = this.questionService.getQuestion(id);
		if (!question.getAuthor().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
		}
		switch (question.getCategoryAsEnum()) {
		case QNA-> model.addAttribute("boardName", "질문과답변 수정");
		case FREE -> model.addAttribute("boardName", "자유게시판 수정");
		case BUG -> model.addAttribute("boardName", "버그및건의 수정");
		default -> throw new RuntimeException("올바르지 않은 접근입니다.");
	}
		questionForm.setSubject(question.getSubject());
		questionForm.setContent(question.getContent());
		return "question_form";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/modify/{id}")
	public String questionModify(@Valid QuestionForm questionForm, BindingResult bindingResult, Principal principal,
			@PathVariable("id") Integer id) {
		if (bindingResult.hasErrors()) {
			return "question_form";
		}
		Question question = this.questionService.getQuestion(id);
		if (!question.getAuthor().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
		}
		this.questionService.modify(question, questionForm.getSubject(), questionForm.getContent());
		return String.format("redirect:/question/detail/%s", id);
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/delete/{id}")
	public String questionDelete(Principal principal, @PathVariable("id") Integer id) {
		Question question = this.questionService.getQuestion(id);
		if (!question.getAuthor().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
		}
		this.questionService.delete(question);
		return "redirect:/";
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/vote/{id}")
	public String questionVote(Principal principal, @PathVariable("id") Integer id) {
		Question question = this.questionService.getQuestion(id);
		SiteUser siteUser = this.userService.getUser(principal.getName());
		this.questionService.vote(question, siteUser);
		return String.format("redirect:/question/detail/%s", id);
	}
}
