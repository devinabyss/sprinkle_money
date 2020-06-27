package sprinklemoney.domain.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sprinklemoney.domain.user.entity.User;
import sprinklemoney.domain.user.repository.UserRepository;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    @Transactional
    public User createUser(String keyValue) {
        User user = User.builder().keyValue(keyValue).build();
        return userRepository.save(user);
    }

    @Override
    public Optional<User> getUserByKeyValue(String keyValue) {
        return userRepository.findByKeyValue(keyValue);
    }

    @Override
    @Transactional
    public User getUserWithGenerateByKeyValue(String keyValue) {
        return getUserByKeyValue(keyValue).orElseGet(() -> createUser(keyValue));
    }

    @Override
    public Optional<User> getUserById(Long no) {
        return userRepository.findById(no);
    }
}
