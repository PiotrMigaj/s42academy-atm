package cap.s42academy.user.adapter.in;

import cap.s42academy.user.application.port.in.LogoutUserCommand;
import cap.s42academy.user.application.port.in.LogoutUserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class LogoutUserRestAdapter {

    private final LogoutUserUseCase logoutUserUseCase;

    @PatchMapping("api/v1/users/logout")
    ResponseEntity<Void> loginUser(@RequestBody LogoutUserCommand command){
        logoutUserUseCase.handle(command);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
