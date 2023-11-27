package nl.tudelft.sem.template.example.controllers;

/*import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;*/
//import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
//import nl.tudelft.sem.template.example.authentication.AuthManager;
import nl.tudelft.sem.template.example.domain.ExampleModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/*import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;*/


import java.util.UUID;

/**
 * Hello World example controller.
 * <p>
 * This controller shows how you can extract information from the JWT token.
 * </p>
 */

@RestController
public class DefaultController {

    //private final transient AuthManager authManager;

    /**
     * Instantiates a new controller.
     *
     * //@param authManager Spring Security component used to authenticate and authorize the user
     */
    public DefaultController() {
    }

    /**
     * Gets example by id.
     *
     * @return the example found in the database with the given id
     */
    /*@Operation(value = "Get a specific pet", response = ExampleModel.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Pet found"),
            @ApiResponse(code = 404, message = "Pet not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })*/
    @Operation(summary = "/user/login", description = "To login user")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "1001", description = "Application specific error.") })
    @GetMapping("/hello")
    @ResponseBody
    public ResponseEntity<ExampleModel> helloWorld(@RequestBody UUID uuid) {
        return ResponseEntity.badRequest().build();
        /*try {
            ExampleModel board = new ExampleModel("Hello");
            return ResponseEntity.ok(board);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }*/
    }

    /*@GetMapping("/hello2")
    public ResponseEntity<String> helloWorld2() {
        return ResponseEntity.ok("Hello2 " + authManager.getNetId());

    }*/

}
