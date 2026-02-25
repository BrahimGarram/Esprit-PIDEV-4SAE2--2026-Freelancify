package com.example.freelanceplatformspringapp.Complaints.Services;

import com.example.freelanceplatformspringapp.Complaints.Entity.Response;

import java.util.List;
import java.util.Optional;

public interface IResponseInterface {
    Response addResponse(Response response);
    Response updateResponse(Response response);
    List<Response> getResponseList();
    Optional<Response> getResponseById(Long id);
    void deleteResponse(Long id);
}