package org.udg.pds.todoandroid.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * Created by imartin on 12/02/16.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
    property = "id", scope = Task.class)
public class Task implements Serializable {
    public Long id;
    public String text;
    public String dateLimit;
    public String dateCreated;
    public Boolean completed;
    public Long userId;
}
