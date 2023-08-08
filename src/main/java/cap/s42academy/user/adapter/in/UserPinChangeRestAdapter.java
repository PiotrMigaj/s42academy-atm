package cap.s42academy.user.adapter.in;

import cap.s42academy.user.application.port.in.LogoutUserCommand;
import cap.s42academy.user.application.port.in.UserPinChangeCommand;
import cap.s42academy.user.application.port.in.UserPinChangeUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class UserPinChangeRestAdapter {

    private final UserPinChangeUseCase userPinChangeUseCase;

    @PatchMapping("api/v1/users/pin-change")
    ResponseEntity<Void> loginUser(@RequestBody UserPinChangeCommand command){
        userPinChangeUseCase.handle(command);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
