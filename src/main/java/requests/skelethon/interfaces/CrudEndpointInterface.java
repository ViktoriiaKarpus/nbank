package requests.skelethon.interfaces;

import models.BaseModel;

public interface CrudEndpointInterface<T> {
    T post(BaseModel model);

    T get();

    T get(long id);

    T update(BaseModel model);

    T update(long id, BaseModel model);

    T delete(long id);

}
