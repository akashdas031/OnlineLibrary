package UserService.Enums;

import java.util.Set;

public enum Role {

	ADMIN(Set.of(Permission.VIEW_BOOKS, Permission.ADD_BOOK, Permission.EDIT_BOOK, 
            Permission.DELETE_BOOK, Permission.MANAGE_USERS, 
            Permission.CONFIGURE_SYSTEM, Permission.GENERATE_REPORTS)),

LIBRARIAN(Set.of(Permission.VIEW_BOOKS, Permission.ADD_BOOK, Permission.EDIT_BOOK, 
                Permission.DELETE_BOOK, Permission.CHECKOUT_BOOK, 
                Permission.RETURN_BOOK, Permission.RATE_BOOK)),

USER(Set.of(Permission.VIEW_BOOKS, Permission.CHECKOUT_BOOK, 
           Permission.RETURN_BOOK, Permission.RATE_BOOK, 
           Permission.VIEW_PROFILE));

private final Set<Permission> permissions;

Role(Set<Permission> permissions) {
   this.permissions = permissions;
}

public Set<Permission> getPermissions() {
   return permissions;
}
}
