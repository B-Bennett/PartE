package com.theironyard;

import javax.persistence.*;
import java.util.List;


/**
 * Created by BennettIronYard on 11/13/15.
 */
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    Integer id;

    String name;
    String password;

    Boolean isOwner = false;
    @OneToMany(mappedBy = "user")
    List<Entertainment> entertainments;
    //Owner owner;
}

