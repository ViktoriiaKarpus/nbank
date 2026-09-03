package requests.skelethon.requesters;

import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.BaseModel;
import requests.skelethon.Endpoint;
import requests.skelethon.HttpRequest;
import requests.skelethon.interfaces.CrudEndpointInterface;

public class ValidatedCrudRequester<T extends BaseModel> extends HttpRequest implements CrudEndpointInterface<T> {
 // private CrudRequester crudRequester;

 // public ValidatedCrudRequester(RequestSpecification requestSpecification, Endpoint endpoint, ResponseSpecification responseSpecification) {
 //     super(requestSpecification, endpoint, responseSpecification);
 //     this.crudRequester = new CrudRequester(requestSpecification, endpoint, responseSpecification);
 // }

 // //запросы использукм для позитивных тестов
 // @Override
 // public T post(BaseModel model) {
 //     return (T) crudRequester.post(model).extract().as(endpoint.getResponseModel());
 // }

 // @Override
 // public T get() {
 //     return (T) crudRequester.get().extract().as(endpoint.getResponseModel());
 // }

 // @Override
 // public T get(long id) {
 //     return (T) crudRequester.get(id).extract().as(endpoint.getResponseModel());
 // }

 // @Override
 // public T update(BaseModel model) {
 //     return (T) crudRequester.update(model).extract().as(endpoint.getResponseModel());
 // }

 // @Override
 // public T update(long id, BaseModel model) {
 //     return (T) crudRequester.update(id, model).extract().as(endpoint.getResponseModel());
 // }

 // @Override
 // public T delete(long id) {
 //     crudRequester.delete(id);
 //     try {
 //         return (T) endpoint.getResponseModel().getDeclaredConstructor().newInstance();
 //     } catch (Exception e) {
 //         throw new RuntimeException(e);
 //     }
 // }

 // public String postAndGetHeader(BaseModel model, String headerName) {
 //     return crudRequester.post(model)
 //             .extract()
 //             .header(headerName);
 // }

 // public T[] getTransactions(long accountId) {
 //     return (T[]) crudRequester.getTransactions(accountId)
 //             .extract()
 //             .as(getResponseArrayType());
 // }

 // private Class<T[]> getResponseArrayType() {
 //     return (Class<T[]>) java.lang.reflect.Array
 //             .newInstance(endpoint.getResponseModel(), 0)
 //             .getClass();
 // }
 private CrudRequester crudRequester;

    public ValidatedCrudRequester(RequestSpecification requestSpecification, Endpoint endpoint, ResponseSpecification responseSpecification) {
        super(requestSpecification, endpoint, responseSpecification);
        this.crudRequester = new CrudRequester(requestSpecification, endpoint, responseSpecification);
    }

    @Override
    public T post(BaseModel model) {
        return (T) crudRequester.post(model).extract().as(endpoint.getResponseModel());
    }

    @Override
    public T get() {
        return (T) crudRequester.get().extract().as(endpoint.getResponseModel());
    }

    @Override
    public T get(long id) {
        return (T) crudRequester.get(id).extract().as(endpoint.getResponseModel());
    }

    @Override
    public T update(BaseModel model) {
        return (T) crudRequester.update(model).extract().as(endpoint.getResponseModel());
    }

    @Override
    public T update(long id, BaseModel model) {
        return (T) crudRequester.update(id, model).extract().as(endpoint.getResponseModel());
    }

    @Override
    public T delete(long id) {
        crudRequester.delete(id);
        try {
            return (T) endpoint.getResponseModel().getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String postAndGetHeader(BaseModel model, String headerName) {
        return crudRequester.post(model)
                .extract()
                .header(headerName);
    }

    public T[] getTransactions(long accountId) {
        return (T[]) crudRequester.getTransactions(accountId)
                .extract()
                .as(getResponseArrayType());
    }

    public T[] getList() {
        return (T[]) crudRequester.get()
                .extract()
                .as(getResponseArrayType());
    }

    private Class<T[]> getResponseArrayType() {
        return (Class<T[]>) java.lang.reflect.Array
                .newInstance(endpoint.getResponseModel(), 0)
                .getClass();
    }
}
