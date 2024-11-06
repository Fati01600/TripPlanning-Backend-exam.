package dat.controllers;

import dat.exceptions.ApiException;
import io.javalin.http.Context;
/**
 * Definerer CRUD-operationer for en controller
 */

public interface IController<I> {
    void getAll(Context ctx) throws ApiException;

    void getId(Context ctx, String id) throws ApiException;

    void create(Context ctx) throws ApiException;

    void update(Context ctx, String id) throws ApiException;

    void delete(Context ctx, String id) throws ApiException;

}
