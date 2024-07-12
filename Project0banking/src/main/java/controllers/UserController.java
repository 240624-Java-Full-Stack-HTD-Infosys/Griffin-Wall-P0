package controllers;

import io.javalin.Javalin;
import models.User;
import services.UserService;
import io.javalin.http.Context;

public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public void register(Context ctx) {
        User user = ctx.bodyAsClass(User.class);
        try {
            userService.registerUser(user);
            ctx.status(201);
        } catch (Exception e) {
            ctx.status(500).json(e.getMessage());
        }
    }

    public void login(Context ctx) {
        User user = ctx.bodyAsClass(User.class);
        try {
            User authenticatedUser = userService.login(user.getUsername(), user.getPassword());
            if (authenticatedUser != null) {
                ctx.status(200).json(authenticatedUser);
            } else {
                ctx.status(401).json("Invalid username or password");
            }
        } catch (Exception e) {
            ctx.status(500).json(e.getMessage());
        }
    }

    public void updateUser(Context ctx) {
        User user = ctx.bodyAsClass(User.class);
        try {
            userService.updateUser(user);
            ctx.status(200).json(user);
        } catch (Exception e) {
            ctx.status(500).json(e.getMessage());
        }
    }
}
