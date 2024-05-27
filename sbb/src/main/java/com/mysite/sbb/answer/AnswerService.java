package com.mysite.sbb.answer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.user.SiteUser;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AnswerService {

	private final AnswerRepository answerRepository;

	public Answer create(Question question, String content, SiteUser author) {
		Answer answer = new Answer();
		answer.setContent(content);
		answer.setCreateDate(LocalDateTime.now());
		answer.setQuestion(question);
		answer.setAuthor(author);
		this.answerRepository.save(answer);
		return answer;
	}

	public Answer getAnswer(Integer id) {
		Optional<Answer> answer = this.answerRepository.findById(id);
		if (answer.isPresent()) {
			return answer.get();
		} else {
			throw new DataNotFoundException("answer not found");
		}
	}

	public void modify(Answer answer, String content) {
		answer.setContent(content);
		answer.setModifyDate(LocalDateTime.now());
		this.answerRepository.save(answer);
	}

	public void delete(Answer answer) {
		this.answerRepository.delete(answer);
	}

	public void vote(Answer answer, SiteUser siteUser) {
		answer.getVoter().add(siteUser);
		this.answerRepository.save(answer);
	}
	
	public Long getAnswerCount(SiteUser author) {
		return answerRepository.countByAuthor(author);
	}

	public List<Answer> getAnswerTop5LatestByUser(SiteUser user) {
		return answerRepository.findTop5ByAuthorOrderByCreateDateDesc(user);
}
	public List<Answer> getAnswerTop15Latest() {
		return answerRepository.findTop15ByOrderByCreateDateDesc();
	}
	
	public Page<Answer> getAnswerPage(Question question, int page, String sort) {
		Pageable pageable;

		// 최신순
		if (sort.equals("createDate")) {
			List<Sort.Order> sorts = new ArrayList<>();
			sorts.add(Sort.Order.desc("createDate"));
			pageable = PageRequest.of(page, 10, Sort.by(sorts)); //페이지 번호, 개수
			return answerRepository.findAllByQuestion(question, pageable);
		}

		// 추천순, 기본
		else {
			pageable = PageRequest.of(page, 10); // 페이지네이션 정보
			// 추천순 : 10개에 페이지정보만 주면 알아서
			if (sort.equals("voter"))
				return answerRepository.findAllByQuestionOrderByVoter(question, pageable);
			// 기본
			return answerRepository.findAllByQuestion(question, pageable);
		}
	}
	
	
}
