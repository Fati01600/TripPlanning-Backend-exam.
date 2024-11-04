package dat.controllers;

import dat.exceptions.ApiException;
import io.javalin.http.Context;

public interface IController<I> {
    void getAll(Context ctx) throws ApiException;
    void getById(Context ctx) throws ApiException;
    void getByType(Context ctx) throws ApiException;
    void add(Context ctx) throws ApiException;
    boolean validatePrimaryKey(I i);
}
