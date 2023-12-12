package com.nikita.instazoo.facade;

import com.nikita.instazoo.entity.User;
import com.nikita.instazoo.dto.UserDTO;
import org.springframework.stereotype.Component;

@Component
public class UserFacade {

    public UserDTO userToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setFirstName(user.getName());
        user.setLastName(user.getLastName());
        userDTO.setUsername(user.getUsername());
        userDTO.setBio(user.getBio());
        return userDTO;
    }
}
