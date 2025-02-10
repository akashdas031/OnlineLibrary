package BookmarkService.FeignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import BookmarkService.Configurations.FeignClientConfiguration;
import BookmarkService.Response.ResponseApi;

@FeignClient(
    name = "user-service",
    url = "http://localhost:6577/userService/api/v2/",
    configuration = FeignClientConfiguration.class
)
public interface UserServiceClient {

    @GetMapping("/getSingleUser/{userId}")
    ResponseApi getUserByUserId(
        @RequestHeader("Authorization") String token,
        @PathVariable("userId") String userId
    );
}