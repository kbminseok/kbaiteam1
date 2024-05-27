package com.mysite.sbb.question;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mysite.sbb.user.SiteUser;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
	Question findBySubject(String subject);

	Question findBySubjectAndContent(String subject, String content);

	List<Question> findBySubjectLike(String subject);

	Page<Question> findAll(Pageable pageable);

	Page<Question> findAll(Specification<Question> spec, Pageable pageable);
	
	@Query("select "
            + "distinct q "
            + "from Question q " 
            + "left outer join SiteUser u1 on q.author=u1 "
            + "left outer join Answer a on a.question=q "
            + "left outer join SiteUser u2 on a.author=u2 "
            + "where "
            + "   (q.category = :category) "
    		+ "   and ( "
            + "   q.subject like %:kw% "
            + "   or q.content like %:kw% "
            + "   or u1.username like %:kw% "
            + "   or a.content like %:kw% "
            + "   or u2.username like %:kw% "
    		+ "   )")
    	Page<Question> findAllByKeywordAndType(@Param("kw") String kw, @Param("category") Integer category, Pageable pageable);
	
	Long countByAuthor(SiteUser author);

	List<Question> findTop5ByAuthorOrderByCreateDateDesc(SiteUser author);

	@Query("select "
		+ "distinct q "
		+ "from Question q "
		+ "left outer join SiteUser u1 on q.author=u1 "
		+ "left outer join Answer a on a.question=q "
		+ "left outer join SiteUser u2 on a.author=u2 "
		+ "where "
		+ "   (q.author.id = :authorId) "
		+ "   and ( "
		+ "       q.subject like %:kw% "
		+ "       or q.content like %:kw% "
		+ "       or a.content like %:kw% "
		+ "       or u2.username like %:kw% "
		+ "   )")
	Page<Question> findAllByKeywordAndAuthorId(@Param("kw") String kw, @Param("authorId") Long authorId, Pageable pageable);
	

	@Query("select "
		+ "distinct q "
		+ "from Question q "
		+ "left outer join SiteUser u1 on q.author = u1 "
		+ "left outer join q.answerList a "
		+ "left outer join SiteUser u2 on a.author = u2 "
		+ "where "
		+ "   (u2.id = :authorId) "
		+ "   and ( "
		+ "       q.subject like %:kw% "
		+ "       or q.content like %:kw% "
		+ "       or u1.username like %:kw% "
		+ "       or a.content like %:kw% "
		+ "       or u2.username like %:kw% "
		+ "   )")
	Page<Question> findAllByKeywordAndAndAnswer_AuthorId(@Param("kw") String kw, @Param("authorId") Long authorId, Pageable pageable);
}
