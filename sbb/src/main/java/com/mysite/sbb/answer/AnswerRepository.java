package com.mysite.sbb.answer;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mysite.sbb.question.Question;
import com.mysite.sbb.user.SiteUser;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {

	Long countByAuthor(SiteUser author);

	List<Answer> findTop5ByAuthorOrderByCreateDateDesc(SiteUser user);
	
	List<Answer> findTop15ByOrderByCreateDateDesc();
	
	Page<Answer> findAllByQuestion(Question question, Pageable pageable);
	
	@Query("SELECT e "
			+ "FROM Answer e "
			+ "WHERE e.question = :question "
			+ "ORDER BY SIZE(e.voter) DESC, e.createDate")
	Page<Answer>
	findAllByQuestionOrderByVoter(@Param("question") Question question, Pageable pageable);
}
