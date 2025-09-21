package kr.growup.havrutabe.user.repository;

import kr.growup.havrutabe.common.exception.BusinessException;
import kr.growup.havrutabe.common.exception.ErrorCode;
import kr.growup.havrutabe.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByNickname(String nickname);

    Optional<User> findByEmail(String email);

    @Query("""
            SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END
            FROM User u
            WHERE u.nickname = :nickname
            """)
    Boolean existsByNickname(@Param("nickname") String nickname);

    Boolean existsByEmail(String email);

    default User findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    default User findByUsernameOrThrow(String username) {
        return findByNickname(username).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    default User findByEmailOrThrow(String email) {
        return findByEmail(email).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
