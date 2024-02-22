package dev.ilkerk.leasing.application.user.service;

import dev.ilkerk.leasing.application.user.dto.request.user.UserCreateDTO;
import dev.ilkerk.leasing.application.user.dto.request.user.UserFindDTO;
import dev.ilkerk.leasing.application.user.dto.response.UserResponse;
import dev.ilkerk.leasing.domain.user.entity.CustomerUsers;
import dev.ilkerk.leasing.domain.user.entity.User;
import dev.ilkerk.leasing.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Example;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final ModelMapper modelMapper;
	private final CustomerUsersService customerUsersService;

	public Mono<UserResponse> get(UUID id) {
		return userRepository.findById(id)
				.flatMap(user -> Mono.just(modelMapper.map(user, UserResponse.class)));
	}

	public Mono<User> getObject(UUID id) {
		return userRepository.findById(id);
	}

	public Flux<UserResponse> getAllByCriteria(UserFindDTO userFindDTO) {
		User user = modelMapper.map(userFindDTO, User.class);

		Example<User> userExample = Example.of(user, UserFindDTO.getExampleMatcher());

		return userRepository.findAll(userExample)
				.flatMap(user1 -> Mono.just(modelMapper.map(user1, UserResponse.class)));
	}

	public Mono<UserResponse> create(UserCreateDTO userCreateDTO) {
		log.info("User creating: " + userCreateDTO.toString());

		User user = modelMapper.map(userCreateDTO, User.class);

		log.debug("Created user object: " + user);

		return userRepository.save(user)
				.flatMap(user1 -> Mono.just(modelMapper.map(user1, UserResponse.class)))
				.flatMap(userResponse -> customerUsersService.create(new CustomerUsers(userCreateDTO.getCustomerId(), userResponse.getId()))
						.flatMap(customerUser -> Mono.just(userResponse))
				);
	}

	public Mono<UserResponse> update(UUID id, UserCreateDTO userCreateDTO) {
		log.info("User updating: " + userCreateDTO.toString());

		return this.getObject(id)
				.map(Optional::of)
				.switchIfEmpty(Mono.error(new Exception("User not found with id: " + id)))
				.flatMap(optionalUser -> {
					if (optionalUser.isPresent()) {
						User updatedUser = this.getUpdatedUser(optionalUser.get(), userCreateDTO);

						log.info("Updated user object: " + updatedUser);

						return userRepository.save(updatedUser);
					}
					return Mono.empty();
				})
				.flatMap(user1 -> Mono.just(modelMapper.map(user1, UserResponse.class)));
	}

	private User getUpdatedUser(User user, UserCreateDTO userCreateDTO) {
		if (userCreateDTO.getName() != null) {
			user.setName(userCreateDTO.getName());
		}

		if (userCreateDTO.getEmail() != null) {
			user.setEmail(userCreateDTO.getEmail());
		}

		if (userCreateDTO.getPassword() != null) {
			user.setPassword(userCreateDTO.getPassword());
		}

		if (userCreateDTO.getActive() != null) {
			user.setActive(userCreateDTO.getActive());
		}

		if (userCreateDTO.getRoles() != null) {
			user.setRoles(userCreateDTO.getRoles());
		}

		return user;
	}

	public Mono<Void> deleteById(UUID id) {
		log.info("User deleting: " + id);

		return userRepository.deleteById(id);
	}

	public Mono<UUID> findCustomerIdByEmail(String email) {
		return userRepository.findByEmail(email)
				.flatMap(user -> customerUsersService.findCustomerIdByUserId(user.getId()).flatMap(Mono::just));
	}

	public Mono<UUID> findUserIdByEmail(String email) {
		return userRepository.findByEmail(email).flatMap(user -> Mono.just(user.getId()));
	}

	public Map<String, String> getUserIdAndCustomerId(Authentication authentication) {
		String customerId;
		String userId;
		try {
			customerId = (String) ((Map<?, ?>) authentication.getDetails()).get("customerId");
			userId = (String) ((Map<?, ?>) authentication.getDetails()).get("userId");
		} catch (Exception e) {
			log.error("Error while getting customerId and userId from authentication: {}", authentication.getDetails(), e);
			throw new RuntimeException("Error while getting customerId and userId from authentication: " + authentication.getDetails(), e);
		}

		return Map.of("customerId", customerId, "userId", userId);
	}
}
