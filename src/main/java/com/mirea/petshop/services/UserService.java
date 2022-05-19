package com.mirea.petshop.services;

import com.mirea.petshop.models.User;
import com.mirea.petshop.repositories.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private IUserRepository iUserRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public UserService(IUserRepository iUserRepository) {
        this.iUserRepository = iUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return iUserRepository.findByUsername(s);
    }

    public void create(String email, String username, String password) {
        User u = new User();
        u.setUsername(username);
        u.setPassword(bCryptPasswordEncoder.encode(password));
        u.setEmail(email);
        u.setRole("USER");
        iUserRepository.save(u);
    }

    public List<User> getAll() {
        return iUserRepository.findAll();
    }

    public  void saveUser(User user) {
        iUserRepository.save(user);
    }

    public void deleteUser(int id) {
        iUserRepository.deleteById(id);
    }
}
