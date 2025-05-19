//package com.example.riberrepublicfichajeapi.controller;
//
//import com.example.riberrepublicfichajeapi.model.User;
//import com.example.riberrepublicfichajeapi.service.UserService;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.responses.ApiResponses;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.AuthorityUtils;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Date;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@RestController
//@RequestMapping("/api/users")
//@Tag(name="Usuarios", description = "Usuarios")
//public class UserController {
//    private final UserService authService;
//
//    public UserController(UserService userService) {
//        this.authService = userService;
//    }
//
//
//    @PostMapping("/user")
//    @Operation(summary = "Nuevo Usuario", description = "Crea un nuevo usuario")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Usuario creado"),
//            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta"),
//            @ApiResponse(responseCode = "404", description = "No se pudo crear el usuario")
//    })
//    public User login(@RequestParam ("nombre")String username, @RequestParam ("contrasena")String password) {
//        if ((username.equals("juan") && password.equals("juan"))){
//            System.out.println("entra");
//            String token = getToken(username);
//            User user = new User();
//            user.setNombre(username);
//            user.setContrasena(password);
//            user.setToken(token);
//            return user;
//
//        }else {
//            return null;
//        }
//    }
//    //contruir el token
//    private String getToken(String username) {
//        String secretKey = "mySecretKey";
//        List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER");
//
//        String token = Jwts.builder()
//                .setId("softtekJWT")
//                .setSubject(username)
//                .claim("authorities" , grantedAuthorities.stream()
//                        .map(GrantedAuthority::getAuthority)
//                        .collect(Collectors.toList()))
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + 600000))
//                .signWith(SignatureAlgorithm.HS512, secretKey.getBytes()).compact();
//        return "Bearer " + token;
//    }
//}
