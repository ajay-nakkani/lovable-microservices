package com.ajay.lovable.commonlib.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

import static com.ajay.lovable.commonlib.enums.ProjectPermission.*;


@RequiredArgsConstructor
@Getter
public enum ProjectRole {


    //using constructor using set
    EDITOR(Set.of(EDIT,VIEW,DELETE,VIEW_MEMBERS)),

    //using a defined constructor
    VIEWER(VIEW,VIEW_MEMBERS),
    OWNER(EDIT,VIEW,DELETE,VIEW_MEMBERS,MANAGE_MEMBERS);


    //viewer and owner args works because of this
    ProjectRole(ProjectPermission... permissions)
    {
        this.permissions = Set.of(permissions);
    }

    private final Set<ProjectPermission> permissions;
}

//
//
//Don’t worry — this looks “complex” only because enums in Java are more powerful than people realize.
//        I’ll explain it slowly, simply, and practically.
//
//1️⃣ What an enum really is (important)
//public enum ProjectPermission { ... }
//
//
//👉 An enum is a class with a fixed set of objects.
//
//So this:
//
//VIEW,
//EDIT,
//DELETE
//
//
//Means:
//
//public static final ProjectPermission VIEW = new ProjectPermission();
//public static final ProjectPermission EDIT = new ProjectPermission();
//...
//
//
//        ✔ You can’t create new ones
//✔ Only predefined instances exist
//
//2️⃣ Your enum step by step
//        Code
//@RequiredArgsConstructor
//@Getter
//public enum ProjectPermission {
//    VIEW("project:view"),
//    EDIT("project:edit"),
//    DELETE("project:delete"),
//    MANAGE_MEMBERS("project:manage_members");
//
//    private final String value;
//}
//
//3️⃣ What happens internally (very important)
//Each enum constant is an object
//        VIEW("project:view")
//
//
//Means:
//
//        new ProjectPermission("project:view");
//
//
//So internally:
//
//Enum Constant	value
//VIEW	"project:view"
//EDIT	"project:edit"
//DELETE	"project:delete"
//MANAGE_MEMBERS	"project:manage_members"
//        4️⃣ Why constructor is needed
//private final String value;
//
//
//Enums can have constructors, but:
//
//Constructor is implicitly private
//
//Called once per enum constant
//
//Lombok:
//
//@RequiredArgsConstructor
//
//
//Generates:
//
//private ProjectPermission(String value) {
//    this.value = value;
//}
//
//5️⃣ What @Getter does
//permission.getValue();
//
//
//Returns:
//
//        "project:view"
//
//        6️⃣ How you actually use this enum
//Example 1: Get permission string
//ProjectPermission p = ProjectPermission.VIEW;
//System.out.println(p.getValue());
//
//
//Output:
//
//project:view
//
//Example 2: In Spring Security
//hasAuthority(ProjectPermission.EDIT.getValue())
//
//Example 3: Loop all permissions
//for (ProjectPermission p : ProjectPermission.values()) {
//        System.out.println(p.name() + " -> " + p.getValue());
//        }
//
//        7️⃣ Why enums are used instead of strings
//
//❌ Bad
//
//"project:view"
//
//
//        ✔ Good
//
//ProjectPermission.VIEW
//
//
//Why?
//
//Compile-time safety
//
//No typos
//
//Centralized control
//
//Refactor-safe
//
//8️⃣ Mental model (remember this)
//
//Enum = predefined objects with behavior
//
//Not just constants.
//
//🎯 Interview one-liner
//
//Java enums are special classes with predefined instances that can hold fields, constructors, and methods.
//
//🧠 Ultra-simple analogy
//
//Think of enum like a menu:
//
//Fixed items
//
//Each item has properties
//
//You can’t add new items at runtime
//
//If you want, next I can explain:
//
//        🔹 enum vs class
//
//🔹 enum in database mapping
//
//🔹 enum + permissions design (real project pattern)