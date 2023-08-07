package cap.s42academy.user.adapter.in;

import cap.s42academy.user.application.port.in.RegisterUserCommand;
import cap.s42academy.user.application.port.in.RegisterUserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class RegisterUserRestAdapter {

    private final RegisterUserUseCase registerUserUseCase;

    @PostMapping("api/v1/users")
    ResponseEntity<String> registerUser(@RequestBody RegisterUserCommand command){
        String result = registerUserUseCase.registerUser(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}
