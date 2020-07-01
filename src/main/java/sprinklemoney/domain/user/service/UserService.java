package sprinklemoney.domain.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import sprinklemoney.domain.user.entity.User;
import sprinklemoney.domain.user.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService  {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public User createUser(String keyValue) {
        User user = User.builder().keyValue(keyValue).build();
        return userRepository.save(user);
    }

    public Optional<User> getUserByKeyValue(String keyValue) {
        return userRepository.findByKeyValue(keyValue);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public User getUserWithGenerateByKeyValue(String keyValue) {
        return getUserByKeyValue(keyValue).orElseGet(() -> createUser(keyValue));
    }

    public Optional<User> getUserById(Long no) {
        return userRepository.findById(no);
    }
}

