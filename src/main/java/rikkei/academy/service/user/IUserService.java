package rikkei.academy.service.user;

import rikkei.academy.model.User;

public interface IUserService {
    boolean existedByUsername(String username);
    boolean existedByEmail(String email);
    void save(User user);
}
