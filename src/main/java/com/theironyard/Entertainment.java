package com.theironyard;
import javax.persistence.*;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.List;

/**
 * Created by BennettIronYard on 11/13/15.
 */
@Entity
public class Entertainment {
    @Id
    @GeneratedValue
    Integer id;

    String name;
    String type;
    Integer price;
    Integer rating;
    String comment;

    @ManyToOne
    User user;
    //Owner owner;
}
