package com.mysite.sbb.question;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.user.SiteUser;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Question {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(length = 200)
	private String subject;

	@Column(columnDefinition = "TEXT")
	private String content;

	private LocalDateTime createDate;

	@OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
	private List<Answer> answerList;

	@ManyToOne
	private SiteUser author;

	private LocalDateTime modifyDate;
	
	@ManyToMany
    Set<SiteUser> voter;
	
	private int view = 0;

	public void updateView() {
		this.view++;
	}
	
	/* 게시판 분류
	0 : 질문답변
	1 : 강좌
	2 : 자유게시판
	 */
	private int category;

	public QuestionEnum getCategoryAsEnum() {
		switch (this.category) {
			case 0:
				return QuestionEnum.QNA;
			case 1:
				return QuestionEnum.FREE;
			case 2:
				return QuestionEnum.BUG;
			default:
				throw new RuntimeException("올바르지 않은 접근입니다.");
		}
	}
	
	
	public String getCategoryAsString() {
		switch (this.category) {
			case 0:
				return "질문과답변";
			case 1:
				return "자유게시판";
			case 2:
				return "버그및건의";
			default:
				throw new RuntimeException("올바르지 않은 접근입니다.");
		}
	}
	
}
