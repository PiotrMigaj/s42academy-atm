package cap.s42academy.user.adapter.in;

import cap.s42academy.user.application.port.in.LoginUserCommand;
import cap.s42academy.user.application.port.in.LoginUserUseCase;
import cap.s42academy.user.application.port.in.RegisterUserCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
class LoginUserRestAdapter {

    private final LoginUserUseCase loginUserUseCase;

    @PostMapping("api/v1/users/login")
    ResponseEntity<Map<String,String>> loginUser(@RequestBody LoginUserCommand command){
        String sessionId = loginUserUseCase.handle(command).getValue().toString();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Map.of("sessionId",sessionId));
    }
}
