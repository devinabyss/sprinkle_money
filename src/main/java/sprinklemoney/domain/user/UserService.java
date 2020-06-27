package sprinklemoney.domain.user;

import sprinklemoney.domain.user.entity.User;

import java.util.Optional;

public interface UserService {

    User createUser(String keyValue);

    Optional<User> getUserByKeyValue(String keyValue);

    User getUserWithGenerateByKeyValue(String keyValue);

    Optional<User> getUserById(Long id);

}
